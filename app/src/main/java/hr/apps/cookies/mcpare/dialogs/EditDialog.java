package hr.apps.cookies.mcpare.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import hr.apps.cookies.mcpare.R;
import hr.apps.cookies.mcpare.data.CalculationHelper;
import hr.apps.cookies.mcpare.data.Posao;


public class EditDialog extends DialogFragment {

    Spinner positionSpinner;
    EditDialogComunicator editComunicator;
    int position;
    Posao posao;
    Button btnOdustani, btnSave, btnRemove;
    EditText pocetakET, krajET, trenutniDatumET;

    DateFormat dateFormat;

    public EditDialog() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        editComunicator = (EditDialogComunicator) activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_edit_dialog, null);

        if (getArguments() != null){
            Bundle bundle = getArguments();
            position = bundle.getInt("position");
            posao = bundle.getParcelable("posao");
        }
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        dateFormat = new SimpleDateFormat("dd.MM.yyyy");


        positionSpinner = (Spinner)view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(getActivity(), R.array.positions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        positionSpinner.setAdapter(adapter);

        pocetakET = (EditText)view.findViewById(R.id.pocetak_tp);
        krajET = (EditText)view.findViewById(R.id.kraj_tp);
        pocetakET.setText(timeFormat.format(new java.util.Date(posao.getPocetak())));
        krajET.setText(timeFormat.format(new java.util.Date(posao.getKraj())));


        trenutniDatumET = (EditText) view.findViewById(R.id.trenutniDatumTv);
        trenutniDatumET.setText(dateFormat.format(new java.util.Date(posao.getPocetak())));


        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnRemove = (Button) view.findViewById(R.id.btnRemove);
        btnOdustani = (Button) view.findViewById(R.id.btnOdustani);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ninja = trenutniDatumET.getText().toString();

                String pos = positionSpinner.getSelectedItem().toString();
                String startHour = pocetakET.getText().toString();
                String endHour = krajET.getText().toString();

                if ((pos.length() == 0) || (startHour.length() == 0) || (endHour.length() == 0) || (givenDate() == null)) {
                    Toast.makeText(getActivity(), "Nisu ispunjena sva polja", Toast.LENGTH_SHORT).show();
                    //treba se nekako zaustaviti zatvaranje dijaloga u slučaju pogreške
                } else {
                    if (CalculationHelper.isMonthBefore(givenDate().getTime()) ||
                            CalculationHelper.isMonthTooFarForward(givenDate().getTime())){
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Možete unositi podatke samo za trenutni i sljedeći mjesec", Toast.LENGTH_LONG).show();
                    }else {
                        if (startHour.length() != 5 || endHour.length() != 5){
                            if (startHour.length() <= 5 && endHour.length() <=5){
                                try {
                                    String startTime = startHour.length() < 5 ? popraviTimeFormat(startHour) : startHour;
                                    String endTime = endHour.length() < 5 ? popraviTimeFormat(endHour) : endHour;
                                    editComunicator.updatePodataka(pos, startTime, endTime, givenDate(), position, posao.getId());
                                    dismiss();
                                }catch (Exception e) {
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "Niste unijeli pravilan format sati hh:mm", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Niste unijeli pravilan format sati hh:mm", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            editComunicator.updatePodataka(pos, startHour, endHour, givenDate(), position, posao.getId());
                            dismiss();
                        }
                    }
                }
            }
        });

        btnOdustani.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editComunicator.brisanje(position, posao);
                dismiss();
            }
        });

        //vrši se postavljanje trenutnih podataka iz baze

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        /*
        builder.setNegativeButton("Obriši", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //vrši se brisanje
                editComunicator.brisanje(position, posao);
            }
        });
        builder.setNeutralButton("Odustani", null);
        builder.setPositiveButton("Spremi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String ninja = trenutniDatumET.getText().toString();

                String pos = positionSpinner.getSelectedItem().toString();
                String startHour = pocetakET.getText().toString();
                String endHour = krajET.getText().toString();

                java.util.Date givenDate = new java.util.Date();

                //treba se provjeriti da li korisnik piše u prošli mjesec



                try {
                    givenDate = dateFormat.parse(ninja);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (CalculationHelper.isMonthBefore(givenDate.getTime())
                        || CalculationHelper.isMonthTooFarForward(givenDate.getTime())){
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Možete unositi podatke samo za trenutni i sljedeći mjesec", Toast.LENGTH_LONG).show();
                }else{
                    editComunicator.updatePodataka(pos, startHour, endHour, givenDate, position, posao.getId());
                }
            }
        });*/

        return builder.create();
    }

    //dobalvja datum koji je korisnik zadao u dijalogu
    public Date givenDate(){
        String ninja = trenutniDatumET.getText().toString();
        Date datumText;
        try {
            datumText = dateFormat.parse(ninja);
        } catch (ParseException e) {
            datumText = null;
        }
        return datumText;
    }
    //pokuša popraviti String u format HH:mm
    private String popraviTimeFormat(String time) throws Exception{
        String[] tekst = time.split(":");
        StringBuilder sati = new StringBuilder().append(tekst[0]);
        StringBuilder minute = new StringBuilder().append(tekst[1]);
        if (tekst.length == 0){
            throw new Exception();
        }else {
            if (tekst[0].length() < 2){
                sati.insert(0, "0");
            }
            if (tekst[1].length() < 2){
                minute.insert(0, "0");
            }
            return sati.toString() + ":" + minute.toString();
        }
    }

    public interface EditDialogComunicator{
        public void brisanje(int position, Posao posao);
        public void updatePodataka(String pozicija, String pocetak, String kraj, Date currentDate, int position, int idPosla);
    }


}
