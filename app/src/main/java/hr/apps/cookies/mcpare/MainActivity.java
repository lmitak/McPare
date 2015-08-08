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

import hr.apps.cookies.mcpare.adapters.PagerAdapter;
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
    int pozicijaFragmenta;

    //stvari od GoogleCalendar-a
    com.google.api.services.calendar.Calendar mService;

    GoogleAccountCredential credential;
    private TextView mStatusText;
    private TextView mResultsText;
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

        ciscenjeBaze();
        /*provjera da li je tablica s blagdanima prazna,
        **ako je onda je punimo sa blagdanima,
        * ako nije, onda možemo izračunati plaću
        */

        if (!(new DBHelper(getApplicationContext()).checkHolidaysTable())){
            Log.d("bugiranje","barem sam probao");
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
        } else {
            Log.d("bugiranje","ima neš čini se");
        }


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

        //metode google calendara

        // Initialize credentials and service object.
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));

        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();


    }

    /*!treba biti async*/
    private void ciscenjeBaze() {

        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(new Date());
        if (currentDate.get(Calendar.DAY_OF_MONTH) == 1){
            DBHelper helper = new DBHelper(getApplicationContext());
            currentDate.add(Calendar.MONTH, -1);
            helper.deleteAllFromPosaoToDate(currentDate.getTimeInMillis());

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
        args.putInt("fragment", pozicijaFragmenta);
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
        args.putInt("fragment", pozicijaFragmenta);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "Dialog");
    }

    @Override
    public void saljiPodatke(String pozicija, String pocetak, String kraj, Date currentDate) {
        long startDate = dateGetter(currentDate, pocetak);
        long endDate = dateGetter(currentDate, kraj);

        Posao posao = new Posao(pozicija, getIdOfPosition(pozicija), startDate, endDate);
        DBHelper helper = new DBHelper(getApplicationContext());
        helper.insertJob(posao);


        if (krajMjeseca() > startDate)
        {
            FragmentTrenutni fragment;
            fragment = (FragmentTrenutni) pagerAdapter.getFragmentAtPosition(pager.getCurrentItem());
            fragment.dodajURecycler(posao);
        }else {
            FragmentSljedeci fragment;
            fragment = (FragmentSljedeci) pagerAdapter.getFragmentAtPosition(2);
            fragment.dodajURecycler(posao);
        }

    }

    @Override
    public void brisanje(int position, Posao posao) {
        DBHelper helper = new DBHelper(getApplicationContext());
        //Log.v("lukas", "" +posao.getId());
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
        Posao newPosao = new Posao(pozicija, getIdOfPosition(pozicija),
                        dateGetter(currentDate, pocetak),
                        dateGetter(currentDate, kraj));
        newPosao.setId(idPosla);
        helper.updateRow(newPosao);

        /*** trebalo bi se gledat ne koji je trenutno fragment nego koji pocetakDatum je u pitanju ***/



        if (pozicijaFragmenta == 1){
            FragmentTrenutni fragment;
            fragment = (FragmentTrenutni) pagerAdapter.getFragmentAtPosition(pager.getCurrentItem());
            fragment.updateItemInRecycle(newPosao, position);
        }else if (pozicijaFragmenta == 2){
            FragmentSljedeci fragment;
            fragment = (FragmentSljedeci) pagerAdapter.getFragmentAtPosition(pager.getCurrentItem());
            fragment.izbrisiIzRecycler(position);
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
                        editor.commit();
                    }
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
                new DownloadHolidaysTask(this).execute();
            } else {
                //mStatusText.setText("No network connection available.");
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
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
