package hr.apps.cookies.mcpare;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.github.clans.fab.FloatingActionButton;

import java.util.Date;
import java.util.List;

import hr.apps.cookies.mcpare.adapters.PagerAdapter;
import hr.apps.cookies.mcpare.dialogs.EditDialog;
import hr.apps.cookies.mcpare.dialogs.RadnoVrijemeDialog;
import hr.apps.cookies.mcpare.fragments.FragmentProsli;
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
                if (position == 0){
                    flowButton.hide(true);
                }else {
                    flowButton.show(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
    public void startDialog(String recyclerTAG) {
        DialogFragment dialog = new RadnoVrijemeDialog();

        Bundle args = new Bundle();
        args.putString("recylcerTAG", recyclerTAG);
        dialog.setArguments(args);

        /*mo?emo slati tag fragmenta*/
        dialog.show(getSupportFragmentManager(), "TAG1");
    }

    @Override
    public void editDialog(String recylcerTag, int position) {
        DialogFragment dialog = new EditDialog();

        Bundle args = new Bundle();
        args.putString("recylcerTAG", recylcerTag);
        args.putInt("position", position);
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
    public void saljiPodatke(String pozicija, java.sql.Date start, java.sql.Date end, java.sql.Date currentDate, String recyclerTag) {
        if (recyclerTag.equals("trenutni")){
            FragmentTrenutni fragment;
            fragment = (FragmentTrenutni) pagerAdapter.getFragmentAtPosition(pager.getCurrentItem());
            fragment.dodajURecycler(pozicija, start, end, currentDate);

        }else if (recyclerTag.equals("sljedeci")){
            FragmentSljedeci fragment;
            fragment = (FragmentSljedeci) pagerAdapter.getFragmentAtPosition(pager.getCurrentItem());
            fragment.dodajURecycler(pozicija, start, end, currentDate);
        }
    }

    @Override
    public void updatePodatka(String pozicija, java.sql.Date start, java.sql.Date end, java.sql.Date currentDate, String recyclerTAG, int position) {
        if (recyclerTAG.equals("trenutni")){
            FragmentTrenutni fragment;
            fragment = (FragmentTrenutni) pagerAdapter.getFragmentAtPosition(pager.getCurrentItem());
            fragment.updateItemInRecycle(pozicija, start, end ,currentDate, position);

        }else if (recyclerTAG.equals("sljedeci")){
            FragmentSljedeci fragment;
            fragment = (FragmentSljedeci) pagerAdapter.getFragmentAtPosition(pager.getCurrentItem());
            fragment.updateItemInRecycle(pozicija, start, end ,currentDate, position);
        }
    }

    @Override
    public void brisanje(int position) {
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
}
