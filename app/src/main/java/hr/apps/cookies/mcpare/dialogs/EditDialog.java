package hr.apps.cookies.mcpare.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import hr.apps.cookies.mcpare.R;


public class EditDialog extends DialogFragment {

    String recylcerTAG;
    Spinner positionSpinner;
    EditDialogComunicator editComunicator;
    int position;

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
            recylcerTAG = bundle.getString("recylcerTAG");
            position = bundle.getInt("position");
        }

        positionSpinner = (Spinner)view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(getActivity(), R.array.positions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        positionSpinner.setAdapter(adapter);

        final EditText pocetakET = (EditText)view.findViewById(R.id.pocetak_tp);
        final EditText krajET = (EditText)view.findViewById(R.id.kraj_tp);

        final EditText trenutniDatum = (EditText) view.findViewById(R.id.trenutniDatumTv);
        final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        //vrši se postavljanje trenutnih podataka iz baze

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setNegativeButton("Obriši", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //vrši se brisanje
                editComunicator.brisanje(position);
            }
        });
        builder.setNeutralButton("Odustani", null);
        builder.setPositiveButton("Spremi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String ninja = trenutniDatum.getText().toString();

                String pos = positionSpinner.getSelectedItem().toString();
                String startHour = pocetakET.getText().toString();
                String endHour = krajET.getText().toString();

                //ovo vjerojatno može i bolje
                Calendar cStart = Calendar.getInstance();
                Calendar cEnd = Calendar.getInstance();
                Calendar cCurrent = Calendar.getInstance();
                try {
                    cStart.setTime(dateFormat.parse(startHour));
                    cEnd.setTime(dateFormat.parse(endHour));
                    cCurrent.setTime(dateFormat.parse(ninja));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Date startSql = new Date(cStart.getTimeInMillis());
                Date endSql = new Date(cEnd.getTimeInMillis());
                Date currentSql = new Date(cCurrent.getTimeInMillis());

                editComunicator.updatePodatka(pos, startSql, endSql, currentSql, recylcerTAG, position);
            }
        });

        return builder.create();
    }

    public interface EditDialogComunicator{
        public void updatePodatka(String pozicija, java.sql.Date start, java.sql.Date end, java.sql.Date currentDate, String recyclerTAG, int position);
        public void brisanje(int position);
    }
}
