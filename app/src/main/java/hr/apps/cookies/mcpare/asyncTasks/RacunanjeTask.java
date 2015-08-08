package hr.apps.cookies.mcpare.asyncTasks;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hr.apps.cookies.mcpare.MainActivity;
import hr.apps.cookies.mcpare.R;
import hr.apps.cookies.mcpare.data.DBHelper;
import hr.apps.cookies.mcpare.data.Posao;

/**
 * Created by lmita_000 on 4.8.2015..
 */
public class RacunanjeTask extends AsyncTask<Double, Double, Double> {
    MainActivity mAcitivity;
    DBHelper helper;

    @Override
    protected Double doInBackground(Double... doubles) {
        double pocetnaVrijednost = doubles[0];

        /* Ako je pocetna vrijednost 0, to znaci da placa jos nikad nije racunata
        ** Ako je placa veca od 0, to znaci da je se samo treba uvecati za odredjenu vrijednost
         */
        if (pocetnaVrijednost > 0){
            publishProgress(pocetnaVrijednost + doubles[1]);
        }else {
            publishProgress(racunaj());
        }

        return null;
    }

    private Double[] racunaj() {
        double iznos = 0;
        double sati = 0;
         helper = new DBHelper(mAcitivity.getApplicationContext());

        SharedPreferences sp =
                mAcitivity.getSharedPreferences(
                        mAcitivity.getString(R.string.preference_file_key), MainActivity.MODE_PRIVATE);
        double dnevna = Double.parseDouble(sp.getString("normalna", "0"));
        double nocna = Double.parseDouble(sp.getString("nocna", "0"));
        double nedjeljna = Double.parseDouble(sp.getString("nedjeljna", "0"));
        double ned_nocna = Double.parseDouble(sp.getString("ned_nocna", "0"));
        double blagdan = Double.parseDouble(sp.getString("blagdan", "0"));
        double blagdan_nocna = Double.parseDouble(sp.getString("blagdan_nocna", "0"));

        List<Posao> listaPoslova = new ArrayList<>();
        listaPoslova = helper.getAllJobsTillNow();

        for (Posao p : listaPoslova){
            sati += radniSati(p.getPocetak(), p.getKraj());

            //komentar uvjeta se nalazi ispod njega
            if (isHoliday(p.getPocetak()) && isHoliday(p.getKraj())){
                //ako počinje i završava radni dan cijeli u blagdan smjeni (u blagdan uvjet treba ubacit i nedelju)
                if (isNocna(p.getPocetak()) && isNocna(p.getKraj())){
                    //ako počinje i završava radni dan cijeli u noćnoj smjeni istog blagdana
                    iznos += blagdan_nocna * radniSati(p.getPocetak(), p.getKraj());

                }else if(isNocna(p.getPocetak())) {
                    //ako započinje u noćnoj praznika i sutradan je ponovo praznik
                    iznos += blagdan_nocna * pocetakUNocnojNocniSati(p.getPocetak())
                            + blagdan * pocetakUNocnojDnevniSati(p.getKraj());
                }else if (isNocna(p.getKraj())){
                    //da li završava barem u noćnoj
                    //počinje na blagdan, u dnevnoj smjeni i završavamo za vrijeme noćne (istog dana)
                    iznos += blagdan * pocetakUDnevnojDnevniSati(p.getPocetak()) +
                            blagdan_nocna * pocetakUDnevnojNocniSati(p.getKraj());

                }else {//cijeli posao mu je po danu
                    iznos += blagdan * radniSati(p.getPocetak(), p.getKraj());
                }
            }else if (isHoliday(p.getPocetak())){
                //ako počinje raditi za vrijeme blagdana, ali se nastavlja na sutradan
                if (isNocna(p.getPocetak()) && isNocna(p.getKraj())){
                    //ako počinje i završava radni dan cijeli u noćnoj smjeni i sutradan nije praznik
                    iznos += blagdan_nocna * radniSati(p.getPocetak(), p.getKraj());

                }else if (isNocna(p.getPocetak())){
                    //ako počinje raditi u noćnoj blagdana, ali nastavlja se na sutradan (koji nije blagdan)
                    /*nocna se treba razdvojiti na dva dijela. Dio koji je za vrijeme blagdana i dio koji je
                    * za vrijeme normalnog radnog dana*/
                    iznos += blagdan_nocna * pocetakUNocnojNocniSati(p.getPocetak())
                            + dnevna * pocetakUNocnojDnevniSati(p.getKraj());
                }else if (isNocna(p.getKraj())){
                    //ako počinje raditi na blagdan i završava u noćnoj smjeni istog dana
                    // onda je još uvijek u istom blagdanu <-- uvijet nepotreban
                }else {

                }
            }
        }

        return null;
    }

    private long pocetakMjeseca(long datum){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(datum);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1, 0, 0, 0);
        return c.getTimeInMillis();
    }

    //ako je sve u istoj smjeni
    private int radniSati(long pocetak_rada, long kraj_rada){
        Calendar pocetak = Calendar.getInstance();
        pocetak.setTimeInMillis(pocetak_rada);
        Calendar kraj = Calendar.getInstance();
        kraj.setTimeInMillis(kraj_rada);


        //ako su sati normalni (nrp. od 12 do 16 => 16-12 = 4h)
        //ako nisu onda povecaj za 24h kraj (npr. od 22 do 6 => (6+24) - 22 = 8h)
        int sati = (kraj.get(Calendar.HOUR_OF_DAY) - pocetak.get(Calendar.HOUR_OF_DAY)) > 0
                ? (kraj.get(Calendar.HOUR_OF_DAY) - pocetak.get(Calendar.HOUR_OF_DAY))
                : ((kraj.get(Calendar.HOUR_OF_DAY) + 24 ) - pocetak.get(Calendar.HOUR_OF_DAY));

        if (((double)(kraj.get(Calendar.MINUTE) - pocetak.get(Calendar.MINUTE))) < 0){
            --sati;
        }

        return sati;
    }

    private boolean isHoliday(long pocetakRada){
        List<Long> listaBlagdana;
        listaBlagdana = helper.getAllHolidaysTillNow();

        boolean isTrue = false;

        for (long blagdan : listaBlagdana){
            Calendar datumRada = Calendar.getInstance();
            Calendar datumBlagdana = Calendar.getInstance();
            datumRada.setTimeInMillis(pocetakRada);
            datumBlagdana.setTimeInMillis(blagdan);

            if((datumRada.get(Calendar.YEAR) == datumBlagdana.get(Calendar.YEAR))
                    && (datumRada.get(Calendar.MONTH) == datumBlagdana.get(Calendar.MONTH))
                    && (datumRada.get(Calendar.DAY_OF_MONTH) == datumBlagdana.get(Calendar.DAY_OF_MONTH)))
                isTrue =  true;

        }
        return  isTrue;
    }

    private boolean isSunday(long pocetakRada){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(pocetakRada);
        return c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }

    private boolean isNocna(long pocetakRada){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(pocetakRada);

        if(c.get(Calendar.HOUR_OF_DAY) < 6 || c.get(Calendar.HOUR_OF_DAY) >= 22)
            return true;
        else
            return false;
    }
    //kada mu posao pocine u nocnoj, a zavrsava u dnevnoj,
    // ovo su sati koje zadobije za vrijeme nocne smjene
    private double pocetakUNocnojNocniSati(long pocetakRada){

        Calendar pocetak = Calendar.getInstance();
        pocetak.setTimeInMillis(pocetakRada);

        int pocetakBr = pocetak.get(Calendar.HOUR_OF_DAY);

        //ako počinje u ili iza 22 ali ne prije 24 prva solucijam inaće druga

        return (24 - pocetakBr) <= 2 ? ((24 - pocetakBr) + 6) : (6 - pocetakBr);
    }
    //kada mu posao pocinje u nocnoj, a zavrsava u dnevnoj
    // ovo su sati koji zadobije za vrijeme dnevne smjene
    private double pocetakUNocnojDnevniSati(long krajRada){

        Calendar kraj = Calendar.getInstance();
        kraj.setTimeInMillis(krajRada);

        int krajBr = kraj.get(Calendar.HOUR_OF_DAY);

        return krajBr - 6;
    }
    //kada mu posao pocinje u dnevnoj, a zavrsava u nocnoj,
    // ovo su sati koje zadobije u dnevnoj
    private double pocetakUDnevnojDnevniSati(long pocetakRada){

        Calendar pocetak = Calendar.getInstance();
        pocetak.setTimeInMillis(pocetakRada);

        int pocetakBr = pocetak.get(Calendar.HOUR_OF_DAY);

        return 22 - pocetakBr;
    }
    //kada mu posao pocinje u dnevnoj, a zavrsava u nocnoj,
    // ovo su sati koje zadobije u nocnoj
    private double pocetakUDnevnojNocniSati(long krajRada){

        Calendar kraj = Calendar.getInstance();
        kraj.setTimeInMillis(krajRada);

        int krajBr = kraj.get(Calendar.HOUR_OF_DAY);

        return (24 - krajBr) <= 2 ? (24 - krajBr) : (krajBr + 2);
    }



    @Override
    protected void onProgressUpdate(Double... values) {
        super.onProgressUpdate(values);
    }
}
