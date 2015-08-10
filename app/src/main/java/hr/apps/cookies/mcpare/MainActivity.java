package hr.apps.cookies.mcpare;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import hr.apps.cookies.mcpare.adapters.PagerAdapter;
import hr.apps.cookies.mcpare.asyncTasks.CleanDBTask;
import hr.apps.cookies.mcpare.asyncTasks.RacunanjeTask;
import hr.apps.cookies.mcpare.data.DBHelper;
import hr.apps.cookies.mcpare.data.Posao;
import hr.apps.cookies.mcpare.dialogs.EditDialog;
import hr.apps.cookies.mcpare.dialogs.RadnoVrijemeDialog;
import hr.apps.cookies.mcpare.fragments.FragmentSljedeci;
import hr.apps.cookies.mcpare.fragments.FragmentTrenutni;
import hr.apps.cookies.mcpare.tabs.SlidingTabLayout;


public class MainActivity extends ActionBarActivity
        implements FragmentTrenutni.FragmentTrenutniComunicator,
        FragmentSljedeci.FragmentSljedeciComunicator,
        //FragmentProsli.FragmentProsliComunicator,
        RadnoVrijemeDialog.DialogComunicator,
        EditDialog.EditDialogComunicator{

    Toolbar toolbar;
    SlidingTabLayout tabs;
    ViewPager pager;
    FloatingActionButton flowButton;
    PagerAdapter pagerAdapter;
    TextView sati_text, placa_text;
    int pozicijaFragmenta;

    //stvari od GoogleCalendar-a
    com.google.api.services.calendar.Calendar mService;

    GoogleAccountCredential credential;
    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sati_text = (TextView) findViewById(R.id.sati_text);
        placa_text = (TextView) findViewById(R.id.placa_text);

        ciscenjeBaze();
        /*provjera da li je tablica s blagdanima prazna,
        **ako je onda obavještavamo korisnika što trebamo napraviti i punimo tablicu
        */

        pozicijaFragmenta = 1;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.mclogo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        pager = (ViewPager) findViewById(R.id.pager);
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);

        flowButton = (FloatingActionButton) findViewById(R.id.fab);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), getApplicationContext());
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(1, true);
        tabs.setViewPager(pager);
        int colors[] = {getResources().getColor(R.color.mc_yellow)};
        tabs.setSelectedIndicatorColors(colors);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pozicijaFragmenta = position;
                //Toast.makeText(getApplicationContext(), position + "", Toast.LENGTH_SHORT).show();
                if (position == 0) {
                    flowButton.hide(true);
                } else {
                    flowButton.show(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        /*ovo �e biti jebeno sjebano, ali evo probat �u*/
        flowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pozicijaFragmenta == 1) {
                    FragmentTrenutni fragment;
                    fragment = (FragmentTrenutni) pagerAdapter.getFragmentAtPosition(pager.getCurrentItem());
                    Toast.makeText(getApplicationContext(), "pozicija je 1", Toast.LENGTH_SHORT).show();
                    fragment.pozoviComunicator();

                } else if (pozicijaFragmenta == 2) {
                    FragmentSljedeci fragment;
                    fragment = (FragmentSljedeci) pagerAdapter.getFragmentAtPosition(pager.getCurrentItem());
                    fragment.pozoviComunicator();
                }
            }
        });

        initializeGoogleCalendarObjects();
        /*
        if (isGooglePlayServicesAvailable()) {
            refreshResults();
        } else {
            Toast.makeText(getApplicationContext(), "Google Play Services required: " +
                    "after installing, close and relaunch this app.", Toast.LENGTH_LONG).show();
        }*/
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        alertDialogBuilder.setMessage("Aplikaciji treba pristupiti kalendaru kako bi točno računala plaću.");
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //provjeravamo da li je googlePlay usluga dostupna te punimo bazu
                if (isGooglePlayServicesAvailable()) {
                    refreshResults();
                } else {
                    Toast.makeText(getApplicationContext(), "Google Play Services required: " +
                            "after installing, close and relaunch this app.", Toast.LENGTH_LONG).show();
                }
            }
        });
        alertDialogBuilder.show();

    }
    // Initialize credentials and service object.
    private void initializeGoogleCalendarObjects(){
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));

        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("McPare")
                .build();
    }

    private void ciscenjeBaze() {

        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(new Date());
        if (currentDate.get(Calendar.DAY_OF_MONTH) == 1){
            //DBHelper helper = new DBHelper(getApplicationContext());
            currentDate.add(Calendar.MONTH, -1);
            //helper.deleteAllFromPosaoToDate(currentDate.getTimeInMillis());
            CleanDBTask dbTask = new CleanDBTask(getApplicationContext());
            dbTask.execute(currentDate.getTimeInMillis());

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.satnica){
            startActivity(new Intent(getApplicationContext(), SatniceActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void startDialog() {
        DialogFragment dialog = new RadnoVrijemeDialog();

        Bundle args = new Bundle();
        Date currentDate = new Date();
        if (pozicijaFragmenta == 1)
        {
            args.putLong("datum", currentDate.getTime());
        }else {
            args.putLong("datum", pocetakSljMjeseca(currentDate.getTime()));
        }
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "TAG1");
    }

    @Override
    public void editDialog(int position, Posao posao) {
        DialogFragment dialog = new EditDialog();

        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putParcelable("posao", posao);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "TAG1");
    }


    @Override
    public void pozoviSljDialog(long datumUMs) {
        DialogFragment dialog = new RadnoVrijemeDialog();
        Bundle args = new Bundle();
        args.putLong("datum", datumUMs);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "Dialog");
    }

    @Override
    public void saljiPodatke(String pozicija, String pocetak, String kraj, Date currentDate) {
        //long startDate = dateGetter(currentDate, pocetak);
        //long endDate = dateGetter(currentDate, kraj);
        Long[] beginingAndEnd = beginingAndEndDate(currentDate, pocetak, kraj);
        //Posao posao = new Posao(pozicija, getIdOfPosition(pozicija), startDate, endDate);
        Posao posao = new Posao(pozicija, getIdOfPosition(pozicija), beginingAndEnd[0], beginingAndEnd[1]);
        DBHelper helper = new DBHelper(getApplicationContext());
        long idPosla = helper.insertJob(posao);


        if (krajMjeseca() > beginingAndEnd[0])
        {
            FragmentTrenutni fragment;
            fragment = (FragmentTrenutni) pagerAdapter.getFragmentAtPosition(1);
            fragment.dodajURecycler(posao);
        }else {
            FragmentSljedeci fragment;
            fragment = (FragmentSljedeci) pagerAdapter.getFragmentAtPosition(2);
            fragment.dodajURecycler(posao);
        }

        Calendar today = Calendar.getInstance();
        if (today.getTimeInMillis() > beginingAndEnd[1]){
            Log.d("lukas", "prosao uvjet");
            pozoviRacunanje(idPosla);
        }

    }

    private void pozoviRacunanje(Long id) {

        String satiTekst = sati_text.getText().toString();
        String placaTekst = placa_text.getText().toString();
        Double sati = Double.parseDouble(satiTekst.substring(0, satiTekst.indexOf(" h")));
        Double placa = Double.parseDouble( placaTekst.substring(0, placaTekst.indexOf(" kn")) );
        RacunanjeTask racunanjeTask = new RacunanjeTask(this);
        racunanjeTask.execute(placa, sati, id.doubleValue());


    }

    @Override
    public void brisanje(int position, Posao posao) {
        DBHelper helper = new DBHelper(getApplicationContext());
        helper.deleteJob(posao.getId());
        if (pozicijaFragmenta == 1){
            FragmentTrenutni fragment;
            fragment = (FragmentTrenutni) pagerAdapter.getFragmentAtPosition(pager.getCurrentItem());
            fragment.izbrisiIzRecycler(position);
        }else if (pozicijaFragmenta == 2){
            FragmentSljedeci fragment;
            fragment = (FragmentSljedeci) pagerAdapter.getFragmentAtPosition(pager.getCurrentItem());
            fragment.izbrisiIzRecycler(position);
        }
    }

    @Override
    public void updatePodataka(String pozicija, String pocetak, String kraj, Date currentDate, int position, int idPosla) {
        DBHelper helper = new DBHelper(getApplicationContext());

        Long[] beginingAndEnd = beginingAndEndDate(currentDate, pocetak, kraj);
        Posao newPosao = new Posao(pozicija, getIdOfPosition(pozicija),
                beginingAndEnd[0],
                beginingAndEnd[1]);
        newPosao.setId(idPosla);
        helper.updateRow(newPosao);

        if (pozicijaFragmenta == 1){
            FragmentTrenutni fragment;
            fragment = (FragmentTrenutni) pagerAdapter.getFragmentAtPosition(1);
            fragment.izbrisiIzRecycler(position);
        }else if (pozicijaFragmenta == 2){
            FragmentSljedeci fragment;
            fragment = (FragmentSljedeci) pagerAdapter.getFragmentAtPosition(2);
            fragment.izbrisiIzRecycler(position);
        }

        if (beginingAndEnd[0] < krajMjeseca()){
            FragmentTrenutni fragment2;
            fragment2 = (FragmentTrenutni) pagerAdapter.getFragmentAtPosition(1);
            fragment2.dodajURecycler(newPosao);
        }else {
            FragmentSljedeci fragment2;
            fragment2 = (FragmentSljedeci) pagerAdapter.getFragmentAtPosition(2);
            fragment2.dodajURecycler(newPosao);
        }

    }

    private long dateGetter(Date currentDate, String pocetak){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        String[] sati_i_minute = pocetak.split(":");

        calendar.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                Integer.parseInt(sati_i_minute[0]),
                Integer.parseInt(sati_i_minute[1])
        );

        return  calendar.getTimeInMillis();
    }

    private Long[] beginingAndEndDate(Date currentDate, String pocetak, String kraj){

        Long[] vars;
        Calendar begining = Calendar.getInstance();
        Calendar ending = Calendar.getInstance();
        begining.setTime(currentDate);
        ending.setTime(currentDate);

        String[] sati_i_minute_pocetka = pocetak.split(":");
        String[] sati_i_minute_kraja = kraj.split(":");

        begining.set(begining.get(Calendar.YEAR),
                begining.get(Calendar.MONTH),
                begining.get(Calendar.DAY_OF_MONTH),
                Integer.parseInt(sati_i_minute_pocetka[0]),
                Integer.parseInt(sati_i_minute_pocetka[1])
        );

        ending.set(ending.get(Calendar.YEAR),
                ending.get(Calendar.MONTH),
                ending.get(Calendar.DAY_OF_MONTH),
                Integer.parseInt(sati_i_minute_kraja[0]),
                Integer.parseInt(sati_i_minute_kraja[1])
        );

        if (begining.getTimeInMillis() > ending.getTimeInMillis())
            ending.add(Calendar.MONTH, 1);
        vars = new Long[]{begining.getTimeInMillis(), ending.getTimeInMillis()};
        return vars;
    }


    private int getIdOfPosition(String pozicija){
        DBHelper helper = new DBHelper(getApplicationContext());
        return  helper.getPositionId(pozicija);
    }

    private long krajMjeseca() {

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.getActualMaximum(Calendar.DAY_OF_MONTH), 0, 0);
        return c.getTimeInMillis();
    }

    private long pocetakSljMjeseca(long var) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(var);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, 1, 0, 0, 0);
        return c.getTimeInMillis();
    }

    //funkcije google calendara

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    isGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        credential.setSelectedAccountName(accountName);
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                    }
                    refreshResults();
                } else if (resultCode == RESULT_CANCELED) {
                    //mStatusText.setText("Account unspecified.");
                    Toast.makeText(getApplicationContext(), "Account unspecified.", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode != RESULT_OK) {
                    chooseAccount();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Attempt to get a set of data from the Google Calendar API to display. If the
     * email address isn't known yet, then call chooseAccount() method so the
     * user can pick an account.
     */
    private void refreshResults() {

        if (credential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            if (isDeviceOnline()) {
                new DownloadHolidaysTask(MainActivity.this).execute();
            } else {
                Toast.makeText(getApplicationContext(), "No network connection available.", Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * Starts an activity in Google Play Services so the user can pick an
     * account.
     */
    private void chooseAccount() {
        startActivityForResult(
                credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date. Will
     * launch an error dialog for the user to update Google Play Services if
     * possible.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS ) {
            return false;
        }
        return true;
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    public void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,
                        MainActivity.this,
                        REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }
    public void updateStatus(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                Log.d("lukas", message);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        RacunanjeTask racunanjeTask = new RacunanjeTask(MainActivity.this);
        racunanjeTask.execute(0.0);
    }
}
