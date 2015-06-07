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
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import hr.apps.cookies.mcpare.R;

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

        final EditText trenutniDatum = (EditText) view.findViewById(R.id.trenutniDatumTv);
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Calendar today = Calendar.getInstance();
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
