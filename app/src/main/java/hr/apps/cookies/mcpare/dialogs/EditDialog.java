package hr.apps.cookies.mcpare.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
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

import hr.apps.cookies.mcpare.R;
import hr.apps.cookies.mcpare.data.CalculationHelper;
import hr.apps.cookies.mcpare.data.Posao;


public class EditDialog extends DialogFragment {

    Spinner positionSpinner;
    EditDialogComunicator editComunicator;
    int position;
    Posao posao;

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
        final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");


        positionSpinner = (Spinner)view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(getActivity(), R.array.positions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        positionSpinner.setAdapter(adapter);

        final EditText pocetakET = (EditText)view.findViewById(R.id.pocetak_tp);
        //pocetakET.setText(zapis.getDatum_od().toString());
        final EditText krajET = (EditText)view.findViewById(R.id.kraj_tp);
        pocetakET.setText(timeFormat.format(new java.util.Date(posao.getPocetak())));
        krajET.setText(timeFormat.format(new java.util.Date(posao.getKraj())));

        final EditText trenutniDatumET = (EditText) view.findViewById(R.id.trenutniDatumTv);
        trenutniDatumET.setText(dateFormat.format(new java.util.Date(posao.getPocetak())));

        //vrši se postavljanje trenutnih podataka iz baze

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
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
        });

        return builder.create();
    }

    public interface EditDialogComunicator{
        public void brisanje(int position, Posao posao);
        public void updatePodataka(String pozicija, String pocetak, String kraj, java.util.Date currentDate, int position, int idPosla);
    }


}
