package hr.apps.cookies.mcpare.dialogs;


import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import hr.apps.cookies.mcpare.R;
import hr.apps.cookies.mcpare.data.Zapis;
import hr.apps.cookies.mcpare.data.ZapisHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class RadnoVrijemeDialog extends DialogFragment {

    DialogComunicator dialogComunicator;
    int redniBroj;

    public RadnoVrijemeDialog(){};


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dialogComunicator = (DialogComunicator) activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_radno_vrijeme_dialog, null);

        Bundle bundle = getArguments();
        redniBroj = bundle.getInt("redniBroj");

        final Spinner positionSpinner = (Spinner)getActivity().findViewById(R.id.spinner);
        final EditText pocetakET = (EditText)getActivity().findViewById(R.id.pocetak_tp);
        final EditText krajET = (EditText)getActivity().findViewById(R.id.kraj_tp);

        final EditText trenutniDatum = (EditText) view.findViewById(R.id.trenutniDatumTv);
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        final Calendar today = Calendar.getInstance();
        today.add(Calendar.DAY_OF_YEAR, redniBroj);

        trenutniDatum.setText(dateFormat.format(today.getTime()));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        builder.setPositiveButton("Sljedeći", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Toast.makeText(getActivity(), "Sad bi trebo sljedeći...", Toast.LENGTH_SHORT).show();
                String ninja = trenutniDatum.getText().toString();
                ninja = ninja.substring(0,2);
                Integer broj = Integer.parseInt(ninja);
                Toast.makeText(getActivity(), broj.toString(), Toast.LENGTH_SHORT).show();
                dialogComunicator.pozoviSljDialog(++redniBroj);
                //spremiti promjenu u bazu

                String pos = positionSpinner.getSelectedItem().toString();
                String startHour = pocetakET.getText().toString();
                String endHour = krajET.getText().toString();


                today.set(today.YEAR,today.MONTH,today.DAY_OF_MONTH,Integer.parseInt(startHour.substring(0,2)),Integer.parseInt(startHour.substring(3,5)));

                java.util.Date pocDate = today.getTime();
                java.util.Date zavDate = null;

                if(Integer.parseInt(endHour.substring(0,2)) > Integer.parseInt(startHour.substring(0,2))){
                    Calendar endDate = today;
                    endDate.add(endDate.DAY_OF_MONTH,1);
                    zavDate = endDate.getTime();
                }
                else{
                    Calendar endDate = today;
                    endDate.set(today.YEAR,today.MONTH,today.DAY_OF_MONTH,Integer.parseInt(endHour.substring(0,2)),Integer.parseInt(endHour.substring(3,5)));
                    zavDate = endDate.getTime();
                }

                Zapis zapis = new Zapis(pos,new java.sql.Date(pocDate.getTime()),new java.sql.Date(zavDate.getTime()),new Double(0),new Double(0));


                    ZapisHelper zapisHelper = new ZapisHelper(getActivity().getApplicationContext());
                    zapisHelper.insertZapis(zapis);

            }
        });

        builder.setNeutralButton("Završi", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(), "Zavrsit cu", Toast.LENGTH_SHORT).show();
                //spremiti promjenu u bazu
            }
        });

        builder.setNegativeButton("Odustani", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(), "Gasim se", Toast.LENGTH_SHORT).show();
                /*u biti tu ne treba ništa; može umjesto onclick listener se stavit null*/
            }
        });

        return builder.create();
    }


    public interface DialogComunicator{
        public void pozoviSljDialog(int redniBroj);
    }
}
