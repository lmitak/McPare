package hr.apps.cookies.mcpare;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;


public class SatniceActivity extends ActionBarActivity{

    Toolbar toolbar;
    EditText normalna, nocna, nedjeljna, ned_nocna, blagdan, blagdan_nocna;
    SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_satnice);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.mclogo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        normalna = (EditText) findViewById(R.id.normalna);
        nocna = (EditText) findViewById(R.id.nocna);
        nedjeljna = (EditText) findViewById(R.id.nedjeljna);
        ned_nocna = (EditText) findViewById(R.id.ned_nocna);
        blagdan = (EditText) findViewById(R.id.blagdan);
        blagdan_nocna = (EditText) findViewById(R.id.blag_nocna);

        sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        normalna.setText(sharedPref.getString("normalna", "0"));
        nocna.setText(sharedPref.getString("nocna", "0"));
        nedjeljna.setText(sharedPref.getString("nedjeljna", "0"));
        ned_nocna.setText(sharedPref.getString("ned_nocna", "0"));
        blagdan.setText(sharedPref.getString("blagdan", "0"));
        blagdan_nocna.setText(sharedPref.getString("blagdan_nocna", "0"));



        normalna.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                editor.putString("normalna", editable.toString());
                editor.apply();
            }
        });


        nocna.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                editor.putString("nocna", editable.toString());
                editor.apply();
            }
        });
        nedjeljna.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                editor.putString("nedjeljna", editable.toString());
                editor.apply();
            }
        });
        ned_nocna.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                editor.putString("ned_nocna", editable.toString());
                editor.apply();
            }
        });
        blagdan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                editor.putString("blagdan", editable.toString());
                editor.apply();
            }
        });
        blagdan_nocna.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                editor.putString("blagdan_nocna", editable.toString());
                editor.apply();
            }
        });



    }

}
