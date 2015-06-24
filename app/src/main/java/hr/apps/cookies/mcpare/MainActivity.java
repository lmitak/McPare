package hr.apps.cookies.mcpare;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.util.Calendar;
import java.util.Date;

import hr.apps.cookies.mcpare.adapters.PagerAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ciscenjeBaze();

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
                if (pozicijaFragmenta == 1){
                    FragmentTrenutni fragment;
                    fragment = (FragmentTrenutni) pagerAdapter.getFragmentAtPosition(pager.getCurrentItem());
                    Toast.makeText(getApplicationContext(), "pozicija je 1", Toast.LENGTH_SHORT).show();
                    fragment.pozoviComunicator();

                }else if (pozicijaFragmenta == 2){
                    FragmentSljedeci fragment;
                    fragment = (FragmentSljedeci) pagerAdapter.getFragmentAtPosition(pager.getCurrentItem());
                    fragment.pozoviComunicator();
                }
            }
        });

    }

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
}
