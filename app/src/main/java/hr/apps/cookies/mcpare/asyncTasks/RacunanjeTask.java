package hr.apps.cookies.mcpare.asyncTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

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
public class RacunanjeTask extends AsyncTask<Double, Double, Double[]> {
    private MainActivity mAcitivity;
    private DBHelper helper;
    private List<Long> listaBlagdana;

    public RacunanjeTask(MainActivity mAcitivity) {
        this.mAcitivity = mAcitivity;
    }

    @Override
    protected void onPostExecute(Double[] aDouble) {
        for (Double d:aDouble){
            Log.d("taskovi", "post: " + d.toString());
        }
        TextView sati_tv = (TextView) mAcitivity.findViewById(R.id.sati_text);
        TextView placa_tv = (TextView) mAcitivity.findViewById(R.id.placa_text);
        sati_tv.setText(aDouble[1].toString() + " h");
        placa_tv.setText(aDouble[0].toString() + " kn");
    }

    @Override
    protected Double[] doInBackground(Double... doubles) {
        Log.d("taskovi", "Racunanje se pokrenulo");
        double pocetnaVrijednost = doubles[0];

        /* Ako je pocetna vrijednost 0, to znaci da placa jos nikad nije racunata
        ** Ako je placa veca od 0, to znaci da je se samo treba uvecati za odredjenu vrijednost
        * koja se nalazi u jednom poslu
        * u async task šaljemo vrijednost koja je već izracunata, ako je nema onda je 0;
        * ako nije 0, onda uz nju šaljemo i odrađene sate, id posla
         */
        /*gore navedeno je upitno za izradu, korisnik neće unosit posao koji već radi, nego
        * unaprijed*/
        for (Double d : doubles){
            Log.d("taskovi", d.toString());
        }
        if (pocetnaVrijednost == 0){
            return racunaj();
        }else {
            return racunaj(doubles[0], doubles[1], doubles[2]);
        }
    }

    private Double[] racunaj(Double... params) {

        double iznos;
        double sati;
        List<Posao> listaPoslova = new ArrayList<>();
        helper = new DBHelper(mAcitivity.getApplicationContext());
        listaBlagdana = helper.getAllHolidaysTillNow();

        SharedPreferences sp =
                mAcitivity.getSharedPreferences(
                        mAcitivity.getString(R.string.preference_file_key), MainActivity.MODE_PRIVATE);
        double dnevna = Double.parseDouble(sp.getString("normalna", "0"));
        double nocna = Double.parseDouble(sp.getString("nocna", "0"));
        double nedjeljna = Double.parseDouble(sp.getString("nedjeljna", "0"));
        double ned_nocna = Double.parseDouble(sp.getString("ned_nocna", "0"));
        double blagdan = Double.parseDouble(sp.getString("blagdan", "0"));
        double blagdan_nocna = Double.parseDouble(sp.getString("blagdan_nocna", "0"));
        Log.d("taskovi", "satnica: " + dnevna);
        if (params.length > 0){
            iznos = params[0];
            sati = params[1];
            listaPoslova.add(helper.getJobOnId(params[2].intValue()));
        }else {
            iznos = 0;
            sati = 0;
            listaPoslova = helper.getAllJobsTillNow();
        }
        Log.d("taskovi", "Kol. poslova: " + listaPoslova.size());

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
                if (isSunday(p.getKraj())){
                    //ako je dan kada zavrsava nedjelja
                    if (isNocna(p.getPocetak()) && isNocna(p.getKraj())) {
                        //ako počinje i završava radni dan cijeli u noćnoj smjeni i sutradan nije praznik, nego nedjelja
                        //pošto je uvijet prošao vrijednost ne može biti manja ili veća od opsega noćne
                        //ne može početi prije 22 i završiti nakon 6
                        iznos += blagdan_nocna * pocinjeNaDanSati(p.getPocetak())
                                + ned_nocna * zavrsavaNaDanSati(p.getKraj());
                    }else if(isNocna(p.getPocetak())){
                        //ako počinje na dan koji je blagdan i nastavlja raditi sutradan kada uje nedjelja i radi dalje
                        // od kraja nocne smjene
                        /*nocna se treba razdvojiti na dva dijela. Dio koji je za vrijeme blagdana i dio koji je
                         * za vrijeme nedjelje
                         * Pošto radi sigurno iznad vremena noćne smjene, to znači da je sigurno odradio od
                         * ponoći do 6, nakon toga se samo zbroji ostatak*/
                        iznos += blagdan_nocna * pocinjeNaDanSati(p.getPocetak())
                                + ned_nocna * 6
                                + nedjeljna * (zavrsavaNaDanSati(p.getKraj()) - 6);
                    }else if (isNocna(p.getKraj())){
                        //ako počinje raditi na blagdan u dnevnim satima
                        // i završava sutradan na nedjelju u noćnoj smjeni (do 6)
                        iznos += blagdan * pocetakUDnevnojDnevniSati(p.getPocetak())
                                + blagdan_nocna * 2
                                + ned_nocna * (zavrsavaNaDanSati(p.getKraj()));
                    }else {
                        //ako se prelazi iz dana u dan, onda mora jedan dio  biti noćni
                        // uvijet nepotreban
                    }
                }else {
                    //ako dan na koji zavrsava radni dan (ni blagdan ni nedjelja)
                    if (isNocna(p.getPocetak()) && isNocna(p.getKraj())){
                        //ako počinje i završava radni dan cijeli u noćnoj smjeni i sutradan nije praznik
                        //pošto je uvijet prošao vrijednost ne može biti manja ili veća od opsega noćne
                        iznos += blagdan_nocna * pocinjeNaDanSati(p.getPocetak())
                                + nocna * zavrsavaNaDanSati(p.getKraj());

                    }else if (isNocna(p.getPocetak())){
                        //ako počinje raditi u noćnoj blagdana, ali nastavlja se na sutradan (koji nije blagdan)
                    /*nocna se treba razdvojiti na dva dijela. Dio koji je za vrijeme blagdana i dio koji je
                    * za vrijeme normalnog radnog dana.
                    * Pošto radi sigurno iznad vremena noćne smjene, to znači da je sigurno odradio od
                    * ponoći do 6, nakon toga se samo zbroji ostatak*/

                        iznos += blagdan_nocna * pocinjeNaDanSati(p.getPocetak())
                                + nocna * 6
                                + dnevna * (zavrsavaNaDanSati(p.getKraj()) - 6);
                    }else if (isNocna(p.getKraj())){
                        //ako počinje raditi na blagdan u dnevnim satima
                        // i završava sutradan na radni dan u noćnoj smjeni (do 6)
                        iznos += blagdan * pocetakUDnevnojDnevniSati(p.getPocetak())
                                + blagdan_nocna * 2
                                + nocna * (zavrsavaNaDanSati(p.getKraj()));
                    }else {
                        //ako se prelazi iz dana u dan, onda mora jedan dio  biti noćni
                        // uvijet nepotreban
                    }
                }
            }else if (isHoliday(p.getKraj())){
                //ako počinje raditi dan prije blagdana i nastavlja se na blagdan
                if (isSunday(p.getPocetak())){
                    //ako dan prije blagdana je nejdelja
                    if (isNocna(p.getPocetak()) && isNocna(p.getKraj())){
                        //ako počinje raditi dan prije blagdana  u noćnoj(koji je nedjelja)
                        // i završava u noćnoj sutradan kada je blagdan(do 6 dakle)
                        iznos += ned_nocna * pocinjeNaDanSati(p.getPocetak()) +
                                blagdan_nocna * zavrsavaNaDanSati(p.getKraj());
                    }else if (isNocna(p.getPocetak())){
                        //ako počinje raditi dan prije blagdana u noćnoj(koji je nedjelja)
                        // i završava sutradan iza noćne
                        iznos += ned_nocna * pocinjeNaDanSati(p.getPocetak())
                                + blagdan_nocna * 6
                                + blagdan * (zavrsavaNaDanSati(p.getPocetak()) -6);
                    }else if(isNocna(p.getKraj())){
                        //ako počinje raditi u dnevnoj smjeni nedjelje prije blagdana i nastavlja
                        //raditi do sutradana, ali završava do 6
                        iznos += nedjeljna * pocetakUDnevnojDnevniSati(p.getPocetak())
                                + ned_nocna * 2
                                + blagdan_nocna * zavrsavaNaDanSati(p.getKraj());
                    }else {
                        //ako se prelazi iz dana u dan, onda mora jedan dio  biti noćni
                        // uvijet nepotreban
                    }
                }else {
                    //ako dan prije je radni dan (ni blagdan ni nedjelja)
                    if (isNocna(p.getPocetak()) && isNocna(p.getKraj())){
                        //ako počinje raditi dan prije blagdana  u noćnoj(koji nije blagdan ni nedjelja)
                        // i završava u noćnoj sutradan kada je blagdan(do 6 dakle)
                        iznos += nocna * pocinjeNaDanSati(p.getPocetak()) +
                                blagdan_nocna * zavrsavaNaDanSati(p.getKraj());
                    }else if (isNocna(p.getPocetak())){
                        //ako počinje raditi dan prije blagdana u noćnoj(koji je radni)
                        // i završava sutradan iza noćne
                        iznos += nocna * pocinjeNaDanSati(p.getPocetak())
                                + blagdan_nocna * 6
                                + blagdan * (zavrsavaNaDanSati(p.getPocetak()) -6);
                    }else if(isNocna(p.getKraj())){
                        //ako počinje raditi u dnevnoj smjeni nedjelje prije radnog dana i nastavlja
                        //raditi do sutradana, ali završava do 6
                        iznos += dnevna * pocetakUDnevnojDnevniSati(p.getPocetak())
                                + nocna * 2
                                + blagdan_nocna * zavrsavaNaDanSati(p.getKraj());
                    }else {
                        //ako se prelazi iz dana u dan, onda mora jedan dio  biti noćni
                        // uvijet nepotreban
                    }
                }
            }else {
                Log.d("taskovi", "barem do tu(nedjelja ili radni dan)");
                //niti ne počinje niti ne završava na blagdan
                if (isSunday(p.getPocetak()) && isSunday(p.getKraj())){
                    //ako počinje i završava na nedjelju
                    if (isNocna(p.getPocetak()) && isNocna(p.getKraj())){
                        //da li u nedjelju radi noćnu od 22 do 24
                        //ili od 0 do 6
                        iznos += ned_nocna * radniSati(p.getPocetak(), p.getKraj());
                    }else if (isNocna(p.getPocetak())){
                        //ako počinje u noćnoj i završava u dnevnoj, onda prelazi u drugi dan, što više nije nedjelja
                        //nepotreban uvijet
                    }else if (isNocna(p.getKraj())){
                        //ako počinje raditi u dnevnoj nedjelje i radi noćnu do 24
                        iznos += nedjeljna * pocetakUDnevnojDnevniSati(p.getPocetak())
                                + ned_nocna * pocetakUDnevnojNocniSati(p.getKraj());
                    }else {
                        //sve radi u dnevnoj smjeni nedjelje
                        iznos += nedjeljna * radniSati(p.getPocetak(), p.getKraj());
                    }
                }else if (isSunday(p.getPocetak())){
                    //ako počinje raditi u nedjelju ali završava na radni dan
                    if (isNocna(p.getPocetak()) && isNocna(p.getKraj())){
                        //ako počinje počinje raditi u noćnoj nedjelje
                        //i nastavlja u noćnoj slj. dana
                        iznos += ned_nocna * pocinjeNaDanSati(p.getPocetak())
                                + nocna * zavrsavaNaDanSati(p.getKraj());
                    }else if (isNocna(p.getPocetak())){
                        //ako počinje raditi u noćnoj nedjelje
                        //i nsatavi raditi u slj. danu dalje od noćne
                        iznos += ned_nocna * pocinjeNaDanSati(p.getPocetak())
                                + nocna * 6
                                + dnevna * (zavrsavaNaDanSati(p.getKraj())-6);
                    }else if (isNocna(p.getKraj())){
                        //ako počinje raditi u dnevnoj nedjelje i radi noćnu iza 24, te prelazi u drugi dan
                        iznos += nedjeljna * pocetakUDnevnojDnevniSati(p.getPocetak())
                                + ned_nocna * 2
                                + nocna * zavrsavaNaDanSati(p.getKraj());
                    }else {
                        //ako se prelazi iz dana u dan, onda mora jedan dio  biti noćni
                        // uvijet nepotreban
                    }
                }else if (isSunday(p.getKraj())){
                    //ako počinje raditi na radni dan i prelazi u nejdelju
                    if (isNocna(p.getPocetak()) && isNocna(p.getKraj())){
                        //ako počinje počinje raditi u noćnoj radnog dana
                        //i nastavlja u noćnoj slj. dana (nedjelje)
                        iznos += nocna * pocinjeNaDanSati(p.getPocetak())
                                + ned_nocna * zavrsavaNaDanSati(p.getKraj());
                    }else if (isNocna(p.getPocetak())){
                        //ako počinje raditi u noćnoj radnog dana
                        //i nsatavi raditi u slj. danu (nedjelji) dalje od noćne
                        iznos += nocna * pocinjeNaDanSati(p.getPocetak())
                                + ned_nocna * 6
                                + nedjeljna * (zavrsavaNaDanSati(p.getKraj())-6);
                    }else if (isNocna(p.getKraj())){
                        //ako počinje raditi u dnevnoj radnog dana i radi noćnu iza 24, te prelazi u drugi dan (nedjelju)
                        iznos += dnevna * pocetakUDnevnojDnevniSati(p.getPocetak())
                                + nocna * 2
                                + ned_nocna * zavrsavaNaDanSati(p.getKraj());
                    }else {
                        //ako se prelazi iz dana u dan, onda mora jedan dio  biti noćni
                        // uvijet nepotreban
                    }
                }else {
                    Log.d("taskovi", "barem do tu(radni dan)");
                    //nije ni blagdan niti nedjelja (radni dan)
                    // niti ne počinje niti ne završava na nedjelju ulu blagdan
                    if (isNocna(p.getPocetak()) && isNocna(p.getKraj())){
                        //ako počinje i završava u noćnoj radnog dana
                        iznos += nocna * radniSati(p.getPocetak(), p.getKraj());
                    }else if (isNocna(p.getPocetak())){
                        //ako počinje raditi u noćnoj radnog dana
                        //i nsatavi raditi u slj. radnom danu dalje od noćne
                        iznos += nocna * pocinjeNaDanSati(p.getPocetak())
                                + nocna * 6
                                + dnevna * (zavrsavaNaDanSati(p.getKraj())-6);
                    }else if (isNocna(p.getKraj())){
                        //ako počinje raditi u dnevnoj radnog dana i radi noćnu iza 24, te prelazi u drugi dan (nedjelju)
                        iznos += dnevna * pocetakUDnevnojDnevniSati(p.getPocetak())
                                + nocna * 2
                                + nocna * zavrsavaNaDanSati(p.getKraj());
                    }else {
                        Log.d("taskovi", "dnevni radni dan");
                        //ako počinje i završava u dnevnoj
                        iznos += dnevna * radniSati(p.getPocetak(), p.getKraj());
                    }
                }
            }
        }
        Double[] dupli = {iznos, sati};
        return dupli;
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

        return (24 - krajBr) <= 2 ? (krajBr - 22) : (krajBr + 2);
    }
    //kada pocinje na blagdan, nedjelju ili radni dan ali zavrsava sutra
    // svaki od tih dana završava u 24h, samo izračunamo od pocetka do tada koliko je prošlo
    private double pocinjeNaDanSati(long pocetakRada){

        Calendar pocetak = Calendar.getInstance();
        pocetak.setTimeInMillis(pocetakRada);

        return 24 - pocetak.get(Calendar.HOUR_OF_DAY);
    }
    //kada zavrsava na blagdan, nedjelju ili radni dan, ali je poceo jucer
    //jednostavno sati, jer toliko ih je radio od ponoci
    private double zavrsavaNaDanSati (long krajRada){
        Calendar kraj = Calendar.getInstance();
        kraj.setTimeInMillis(krajRada);
        return kraj.get(Calendar.HOUR_OF_DAY);
    }
    //provjerava da li je nocna jutarnja

}
