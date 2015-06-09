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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
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
        final View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_radno_vrijeme_dialog, null);

        Bundle bundle = getArguments();
        redniBroj = bundle.getInt("redniBroj");

        final EditText trenutniDatum = (EditText) view.findViewById(R.id.trenutniDatumTv);
        final DateTime now = DateTime.now();
        //final Calendar today = now.toGregorianCalendar();
        //today.add(Calendar.DAY_OF_YEAR, redniBroj);

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        //trenutniDatum.setText(dateFormat.format(today.getTime()));
        trenutniDatum.setText(now.toString("dd.MM.yyyy"));//TODO pzai na ovo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        builder.setPositiveButton("Sljedeći", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Toast.makeText(getActivity(), "Sad bi trebo sljedeći...", Toast.LENGTH_SHORT).show();
                String ninja = trenutniDatum.getText().toString();
                ninja = ninja.substring(0,2);
                Integer broj = Integer.parseInt(ninja);
                //Toast.makeText(getActivity(), broj.toString(), Toast.LENGTH_SHORT).show();
                dialogComunicator.pozoviSljDialog(++redniBroj);
                //spremiti promjenu u bazu

                zapisiUBazu(view,now);
            }
        });

        builder.setNeutralButton("Završi", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Toast.makeText(getActivity(), "Zavrsit cu", Toast.LENGTH_SHORT).show();
                //spremiti promjenu u bazu

                zapisiUBazu(view,now);
            }
        });

        builder.setNegativeButton("Odustani", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Toast.makeText(getActivity(), "Gasim se", Toast.LENGTH_SHORT).show();
                /*u biti tu ne treba ništa; može umjesto onclick listener se stavit null*/
            }
        });

        return builder.create();
    }


    public interface DialogComunicator{
        public void pozoviSljDialog(int redniBroj);
    }

    public void zapisiUBazu(View view,DateTime today){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        final Spinner positionSpinner = (Spinner)view.findViewById(R.id.spinner);
        final EditText pocetakET = (EditText)view.findViewById(R.id.pocetak_tp);
        final EditText krajET = (EditText)view.findViewById(R.id.kraj_tp);
        final TextView trenDat = (TextView)view.findViewById(R.id.trenutniDatumTv);

        Log.e("smeće",trenDat.toString());
        DateTime uneseniDT = null;

//        try{
            String dateNTime = trenDat.getText().toString() + " " + pocetakET.getText();
            uneseniDT = DateTime.parse(dateNTime, DateTimeFormat.forPattern("dd.MM.yyyy HH:mm"));
            Log.e("tryatch",uneseniDT.toString("yyyy-MM-dd HH:mm"));
//        }
//        catch (ParseException e){
//            e.printStackTrace();
//        }

        String pos = positionSpinner.getSelectedItem().toString();
        String startHour = pocetakET.getText().toString();
        String endHour = krajET.getText().toString();

        int pocBrojSati = Integer.parseInt(startHour.substring(0, 2));
        int pocBrojMin = Integer.parseInt(startHour.substring(3, 5));

        int zavBrojSati = Integer.parseInt(endHour.substring(0, 2));
        int zavBrojMin = Integer.parseInt(endHour.substring(3,5));


        //today.set(today.YEAR,today.MONTH,today.DAY_OF_MONTH,Integer.parseInt(startHour.substring(0,2)),Integer.parseInt(startHour.substring(3,5)),0);
        Log.e("today as naou",uneseniDT.toString("dd.MM.yyyy HH:mm"));
        Log.e("num of h_start",Integer.toString(pocBrojSati));
        Log.e("num of m_start",Integer.toString(pocBrojMin));

        Log.e("num of h_end",Integer.toString(zavBrojSati));

        Log.e("num of m_end",Integer.toString(zavBrojMin));


//        java.util.Date pocDate = today.getTime();
//        java.util.Date zavDate = null;


        DateTime pocDate = new DateTime(uneseniDT.getYear(),uneseniDT.getMonthOfYear(),uneseniDT.getDayOfMonth(),uneseniDT.getHourOfDay(),uneseniDT.getMinuteOfHour());
        DateTime zavDate = null;

        //Log.e("asdasd", pocDate.toString("y-MM-dd HH:mm"));
//        java.util.Date zavDate = null;


        if(zavBrojSati < pocBrojSati){
            DateTime endDate = new DateTime(pocDate.getYear(),pocDate.getMonthOfYear(),pocDate.getDayOfMonth()+1,zavBrojSati,zavBrojMin);
            //endDate.plusDays(1);
            zavDate = endDate;
        }
        else{
            zavDate = new DateTime(pocDate.getYear(),pocDate.getMonthOfYear(),pocDate.getDayOfMonth(),zavBrojSati,zavBrojMin);
            //endDate.plusMinutes((zavBrojSati*60 + zavBrojMin) - (pocBrojSati*60 + pocBrojMin));
            //zavDate = endDate;
        }

        Log.e("zavDate_blahmeh",zavDate.toString("y-MM-dd HH:mm"));
        Log.e("pocDate_blahmeh",pocDate.toString("y-MM-dd HH:mm"));



//        Log.e("pocDate",pocDate.toString());
//        Log.e("zavDate",zavDate.toString());

//        String pattern = "yyyy-MM-dd HH:mm";
//        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
//
//        Timestamp pocetni = null, kraj = null;
//        try{
//            pocetni =  new java.sql.Timestamp(sdf.parse(pocDate.toString("y-MM-dd HH:mm")).getTime());
//            kraj = new java.sql.Timestamp(sdf.parse(zavDate.toString("y-MM-dd HH:mm")).getTime());
////            pocetni = sdf.parse(pocDate.toString("y-MM-dd HH:mm"));
////            kraj = sdf.parse(zavDate.toString("y-MM-dd HH:mm"));
//        }
//        catch (ParseException ex){
//            ex.printStackTrace();
//        }
//
//        Log.e("pocetni",pocetni.toString());
      //  Log.e("kraj", kraj.toString());


        Zapis zapis = new Zapis(pos,pocDate.toString("y-MM-dd HH:mm"),zavDate.toString("y-MM-dd HH:mm"));

        Log.e("pocDate",zapis.getDatum_od().toString());
        Log.e("zavDate",zapis.getDatum_do().toString());

        ZapisHelper zapisHelper = new ZapisHelper(getActivity().getApplicationContext());
        zapisHelper.insertZapis(zapis);
    }
}
