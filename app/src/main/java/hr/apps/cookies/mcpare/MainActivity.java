package hr.apps.cookies.mcpare;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.github.clans.fab.FloatingActionButton;

import hr.apps.cookies.mcpare.adapters.PagerAdapter;
import hr.apps.cookies.mcpare.dialogs.NumberPickerDialog;
import hr.apps.cookies.mcpare.dialogs.RadnoVrijemeDialog;
import hr.apps.cookies.mcpare.fragments.FragmentTrenutni;
import hr.apps.cookies.mcpare.tabs.SlidingTabLayout;


public class MainActivity extends ActionBarActivity implements FragmentTrenutni.FragmentComunicator, RadnoVrijemeDialog.DialogComunicator {

    Toolbar toolbar;
    SlidingTabLayout tabs;
    ViewPager pager;
    public Button flowButton;

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

        //flowButton = (Button) findViewById(R.id.fab);

        pager.setAdapter(new PagerAdapter(getSupportFragmentManager(), getApplicationContext()));
        pager.setCurrentItem(1, true);
        tabs.setViewPager(pager);
        int colors[] = {getResources().getColor(R.color.mc_yellow)};
        tabs.setSelectedIndicatorColors(colors);

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
        /*kada ga tek pozivamo je rednibroj dana 0(nula), tj. trenutni dan*/
        Bundle args = new Bundle();
        args.putInt("redniBroj", 0);
        dialog.setArguments(args);
        /*mo�emo slati tag fragmenta*/
        dialog.show(getSupportFragmentManager(), "TAG1");
    }

    @Override
    public void pozoviSljDialog(int redniBroj) {
        DialogFragment dialog = new RadnoVrijemeDialog();
        Bundle args = new Bundle();
        args.putInt("redniBroj", redniBroj);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "Dialog");
    }
}
