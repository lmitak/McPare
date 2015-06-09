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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import hr.apps.cookies.mcpare.R;
import hr.apps.cookies.mcpare.data.Zapis;
import hr.apps.cookies.mcpare.data.ZapisHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class RadnoVrijemeDialog extends DialogFragment {

    DialogComunicator dialogComunicator;
    long datumUMs;
    String recylcerTAG;
    Spinner positionSpinner;

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
        final  Calendar today = Calendar.getInstance();;

        if (getArguments() != null){
            Bundle bundle = getArguments();
            datumUMs = bundle.getLong("datum", today.getTimeInMillis());
            today.setTimeInMillis(datumUMs);
            recylcerTAG = bundle.getString("recylcerTAG");
        }

        //final Calendar today = Calendar.getInstance();
        /*Bundle bundle = getArguments();
        datumUMs = bundle.getLong("datum", today.getTimeInMillis());
        today.setTimeInMillis(datumUMs);
        */



        positionSpinner = (Spinner)view.findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(getActivity(), R.array.positions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        positionSpinner.setAdapter(adapter);

        final EditText pocetakET = (EditText)view.findViewById(R.id.pocetak_tp);
        final EditText krajET = (EditText)view.findViewById(R.id.kraj_tp);

        final EditText trenutniDatum = (EditText) view.findViewById(R.id.trenutniDatumTv);
        final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");



        trenutniDatum.setText(dateFormat.format(today.getTime()));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        builder.setPositiveButton("Sljedeći", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Toast.makeText(getActivity(), "Sad bi trebo sljedeći...", Toast.LENGTH_SHORT).show();
                String ninja = trenutniDatum.getText().toString();
                Date datumText = new Date();
                try {
                    datumText = dateFormat.parse(ninja);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar caki = Calendar.getInstance();
                caki.setTime(datumText);
                caki.add(Calendar.DAY_OF_YEAR, 1);
                dialogComunicator.pozoviSljDialog(caki.getTimeInMillis());


                //spremiti promjenu u bazu

                String pos = positionSpinner.getSelectedItem().toString();
                String startHour = pocetakET.getText().toString();
                String endHour = krajET.getText().toString();

                //slanje podataka recylcerView-u fragmenta

                if ((pos.length() == 0) || (startHour.length() == 0) || (endHour.length() == 0) || (ninja.toString().length() == 0)){
                    Toast.makeText(getActivity(), "Nisu ispunjena sva polja", Toast.LENGTH_SHORT).show();
                }else{
                    //dialogComunicator.saljiPodatke(pos, );
                    //Čitaj!
                    //u dialog comunicator pošalješ podatke da se pokažu u recycler view-u bez da update baze
                    // gore sam ga započeo; šalješ poziciju, početni datum, završni datum i trenutni datum(imaš ih u biti sve u if-u)
                    // ako želiš drugačije tipove ili nešto, radi ih :P     <-- plazim ti jezik
                }

/*
                today.set(today.YEAR, today.MONTH, today.DAY_OF_MONTH, Integer.parseInt(startHour.substring(0, 2)), Integer.parseInt(startHour.substring(3, 5)));

                java.util.Date pocDate = today.getTime();
                java.util.Date zavDate = null;

                if (Integer.parseInt(endHour.substring(0, 2)) > Integer.parseInt(startHour.substring(0, 2))) {
                    Calendar endDate = today;
                    endDate.add(endDate.DAY_OF_MONTH, 1);
                    zavDate = endDate.getTime();
                } else {
                    Calendar endDate = today;
                    endDate.set(today.YEAR, today.MONTH, today.DAY_OF_MONTH, Integer.parseInt(endHour.substring(0, 2)), Integer.parseInt(endHour.substring(3, 5)));
                    zavDate = endDate.getTime();
                }

                Zapis zapis = new Zapis(pos, new java.sql.Date(pocDate.getTime()), new java.sql.Date(zavDate.getTime()), new Double(0), new Double(0));


                ZapisHelper zapisHelper = new ZapisHelper(getActivity().getApplicationContext());
                zapisHelper.insertZapis(zapis);
*/
            }
        });

        builder.setNeutralButton("Završi", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(), "Zavrsit cu", Toast.LENGTH_SHORT).show();
                //spremiti promjenu u bazu

                String ninja = trenutniDatum.getText().toString();

                String pos = positionSpinner.getSelectedItem().toString();
                String startHour = pocetakET.getText().toString();
                String endHour = krajET.getText().toString();

                if ((pos.length() == 0) || (startHour.length() == 0) || (endHour.length() == 0) || (ninja.toString().length() == 0)){
                    Toast.makeText(getActivity(), "Nisu ispunjena sva polja", Toast.LENGTH_SHORT).show();
                }else{
                    //dialogComunicator.saljiPodatke(pos, );
                    //Čitaj!
                    //u dialog comunicator pošalješ podatke da se pokažu u recycler view-u bez da update baze
                    // gore sam ga započeo; šalješ poziciju, početni datum, završni datum i trenutni datum(imaš ih u biti sve u if-u)
                    // ako želiš drugačije tipove ili nešto, radi ih :P     <-- plazim ti jezik
                }
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
        public void pozoviSljDialog(long trenutniDateTime);
        public void saljiPodatke(String pozicija, java.sql.Date start, java.sql.Date end, java.sql.Date currentDate, String recyclerTAG);
    }


}
