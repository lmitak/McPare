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
import android.util.Log;
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
import java.util.Map;

import hr.apps.cookies.mcpare.R;
import hr.apps.cookies.mcpare.data.CalculationHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class RadnoVrijemeDialog extends DialogFragment {

    DialogComunicator dialogComunicator;
    long zadanDatumMs;
    Spinner positionSpinner;
    int kojiFragment;
    EditText pocetakET, krajET, trenutniDatum;
    Calendar today, zadanDatum;

    DateFormat dateFormat;

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

        today = Calendar.getInstance();
        zadanDatum = Calendar.getInstance();

        dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        DateFormat monthFormat = new SimpleDateFormat("MM");
        DateFormat yearFormat = new SimpleDateFormat("yyyy");

        if (getArguments() != null){
            Bundle bundle = getArguments();
            zadanDatumMs = bundle.getLong("datum", today.getTimeInMillis());
            zadanDatum.setTimeInMillis(zadanDatumMs);
        }


        //ako je zadan mjesec veći od trenutnoga u pitanju je slj. mjesec
        //možda nije potrebno
        if (today.get(Calendar.MONTH) < zadanDatum.get(Calendar.MONTH)){



            Calendar startOfNextMonth = Calendar.getInstance();
            startOfNextMonth.setTimeInMillis(zadanDatumMs);
            startOfNextMonth.set(startOfNextMonth.get(Calendar.YEAR),
                    startOfNextMonth.get(Calendar.MONTH),
                    1);
            today.setTimeInMillis(startOfNextMonth.getTimeInMillis());

        }

        positionSpinner = (Spinner)view.findViewById(R.id.spinner);


        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(getActivity(), R.array.positions, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        positionSpinner.setAdapter(adapter);

        pocetakET = (EditText)view.findViewById(R.id.pocetak_tp);
        krajET = (EditText)view.findViewById(R.id.kraj_tp);
        trenutniDatum = (EditText) view.findViewById(R.id.trenutniDatumTv);




        trenutniDatum.setText(dateFormat.format(zadanDatum.getTime()));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        builder.setPositiveButton("Sljedeći", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String pos = positionSpinner.getSelectedItem().toString();
                String startHour = pocetakET.getText().toString();
                String endHour = krajET.getText().toString();

                //slanje podataka recylcerView-u fragmenta
                if ((pos.length() == 0) || (startHour.length() == 0) || (endHour.length() == 0) || (givenDate() == null)) {
                    Toast.makeText(getActivity(), "Nisu ispunjena sva polja", Toast.LENGTH_SHORT).show();
                    //treba se nekako zaustaviti zatvaranje dijaloga u slučaju pogreške
                } else {
                    if (CalculationHelper.isMonthBefore(givenDate().getTime()) ||
                            CalculationHelper.isMonthTooFarForward(givenDate().getTime())){
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Možete unositi podatke samo za trenutni i sljedeći mjesec", Toast.LENGTH_LONG).show();
                    }else {
                        dialogComunicator.saljiPodatke(pos, startHour, endHour, givenDate());
                        dialogComunicator.pozoviSljDialog(nextDay(givenDate()));
                    }

                }
            }
        });

        builder.setNeutralButton("Završi", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String pos = positionSpinner.getSelectedItem().toString();
                String startHour = pocetakET.getText().toString();
                String endHour = krajET.getText().toString();

                if ((pos.length() == 0) || (startHour.length() == 0) || (endHour.length() == 0) || (givenDate() == null)) {
                    Toast.makeText(getActivity(), "Nisu ispunjena sva polja", Toast.LENGTH_SHORT).show();

                } else {
                    dialogComunicator.saljiPodatke(pos, startHour, endHour, givenDate());
                }
            }
        });

        builder.setNegativeButton("Odustani", null);

        return builder.create();
    }

    //dobavlja datum slj. dana
    public long nextDay(Date currentDate){

        Calendar nextDay = Calendar.getInstance();
        nextDay.setTime(currentDate);
        nextDay.add(Calendar.DAY_OF_YEAR, 1);
        return nextDay.getTimeInMillis();
    }
    //dobalvja datum koji je korisnik zadao u dijalogu
    public Date givenDate(){
        String ninja = trenutniDatum.getText().toString();
        Date datumText;
        try {
            datumText = dateFormat.parse(ninja);
        } catch (ParseException e) {
            Log.d("datumGreska", e.getMessage());
            datumText = null;
        }
        return datumText;
    }


    public interface DialogComunicator{
        public void pozoviSljDialog(long sljedeciDan);
        public void saljiPodatke(String pozicija, String pocetak, String kraj, Date currentDate);
    }


}
