package hr.apps.cookies.mcpare.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hr.apps.cookies.mcpare.R;

/**
 * Created by lmita_000 on 23.6.2015..
 */
public class DBHelper extends SQLiteOpenHelper {

    private static int DATABASE_VERSION = 1;
    private static String DATABASE_NAME = "McPare";

    //Tables
    private static String TABLE_POSAO = "posao";
    private static String TABLE_POZICIJA = "pozicija";
    private static String TABLE_BLAGDANI = "blagdani";

    //Columns
    //table posao
    private static String COLUMN_ID = "id";
    private static String COLUMN_ID_POZICIJE = "id_pozicije";
    private static String COLUMN_POCETAK = "pocetak";
    private static String COLUMN_KRAJ = "kraj";
    //table pozicija
    private static String COLUMN_NAZIV = "naziv";
    private static String COLUMN_POZICIJA_ID = "pozicija_id";
    //table blagdani
    private static String COLUMN_ID_BLAGDANA = "_id";
    private static String COLUMN_DATUM_BLAGDANA = "datum";


    Context context;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_POZICIJA + "( "
                + COLUMN_POZICIJA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAZIV + " TEXT"
                + ")");

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_POSAO + "( "
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ID_POZICIJE + " INTEGER , "
                + COLUMN_POCETAK + " INTEGER, "
                + COLUMN_KRAJ + " INTEGER,"
                + "FOREIGN KEY (" + COLUMN_ID_POZICIJE + ") REFERENCES " + TABLE_POZICIJA + "(" + COLUMN_POZICIJA_ID + ")"
                + ")");

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_BLAGDANI + "("
                + COLUMN_ID_BLAGDANA + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_DATUM_BLAGDANA + " INTEGER "
                + ");");

        addInitialPositions(sqLiteDatabase);
    }

    private void addInitialPositions(SQLiteDatabase db) {

        String[] pozicije = context.getResources().getStringArray(R.array.positions);
        for(String pozicija : pozicije){
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_NAZIV, pozicija);
            db.insert(TABLE_POZICIJA, null, contentValues);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_POZICIJA);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_POSAO);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_BLAGDANI);
        onCreate(sqLiteDatabase);
    }

    //izvlačenje svih poslova u određenom mjesecu
    public List<Posao> getAllJobsInMonth(long datum){

        List<Posao> poslovi = new ArrayList<Posao>();

        Calendar pocetakMjeseca = Calendar.getInstance();
        pocetakMjeseca.setTimeInMillis(datum);
        //pocetakDana.add(Calendar.DAY_OF_MONTH, -1);
        //pocetakDana.set(pocetakDana.get(Calendar.YEAR), pocetakDana.get(Calendar.MONTH), pocetakDana.get(Calendar.DAY_OF_MONTH), 0, 0);
        pocetakMjeseca.set(pocetakMjeseca.get(Calendar.YEAR), pocetakMjeseca.get(Calendar.MONTH), 1, 0, 0);

        Calendar krajMjeseca = Calendar.getInstance();
        krajMjeseca.setTimeInMillis(datum);
        //krajDana.set(krajDana.get(Calendar.YEAR), krajDana.get(Calendar.MONTH), krajDana.get(Calendar.DAY_OF_MONTH), 23, 59);
        int max_broj_dana_u_mj = krajMjeseca.getActualMaximum(Calendar.DAY_OF_MONTH);
        krajMjeseca.set(krajMjeseca.get(Calendar.YEAR), krajMjeseca.get(Calendar.MONTH), max_broj_dana_u_mj, 23, 59);

        SQLiteDatabase db = getReadableDatabase();

        String select_query = "SELECT * FROM " + TABLE_POSAO
                + " JOIN " + TABLE_POZICIJA + " ON " + COLUMN_POZICIJA_ID + " = " + COLUMN_ID_POZICIJE
                + " WHERE " + COLUMN_POCETAK + " BETWEEN " + pocetakMjeseca.getTimeInMillis() + " AND " + krajMjeseca.getTimeInMillis()
                + " ORDER BY " + COLUMN_POCETAK + " ASC";

        Cursor c = db.rawQuery(select_query, null);

        if (c.moveToFirst()){
            do {
                Posao posel = new Posao();
                posel.setId(c.getInt(c.getColumnIndex(COLUMN_ID)));
                posel.setPozicija(c.getString(c.getColumnIndex(COLUMN_NAZIV)), c.getInt(c.getColumnIndex(COLUMN_POZICIJA_ID)));
                posel.setPocetak(c.getLong(c.getColumnIndex(COLUMN_POCETAK)));
                posel.setKraj(c.getLong(c.getColumnIndex(COLUMN_KRAJ)));
                posel.setPozicija_id(c.getInt(c.getColumnIndex(COLUMN_POZICIJA_ID)));
                poslovi.add(posel);
            }while (c.moveToNext());
        }
        c.close();
        db.close();
        return poslovi;
    }
    //dobavljanje svih pozicija
    public ArrayList<Pozicija> getAllPositions(){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Pozicija> pozicije = new ArrayList<Pozicija>();
        String select_query = "SELECT * FROM " + TABLE_POZICIJA;

        Cursor c = db.rawQuery(select_query, null);
        if (c.moveToFirst()){
            do {
                Pozicija pozicija =
                        new Pozicija(c.getInt(c.getColumnIndex(COLUMN_POZICIJA_ID)), c.getString(c.getColumnIndex(COLUMN_NAZIV)));
                pozicije.add(pozicija);
            }while (c.moveToNext());
        }
        c.close();
        db.close();
        return pozicije;
    }
    //dobivanje ID-ja od pozicije
    public int getPositionId(String pozicija){
        SQLiteDatabase db = getReadableDatabase();

        String select_query = "SELECT " + COLUMN_POZICIJA_ID + " FROM " + TABLE_POZICIJA
                + " WHERE " + COLUMN_NAZIV + " = '" + pozicija + "'";
        int id;
        Cursor c = db.rawQuery(select_query, null);

        c.moveToFirst();
        id = c.getInt(c.getColumnIndex(COLUMN_POZICIJA_ID));

        c.close();
        db.close();
        return id;
    }




    //dodavanje posla
    public long insertJob(Posao p){
        SQLiteDatabase db = getWritableDatabase();
        long id;
        Log.d("taskoviRac", "Posao: id = " + p.getId() + ", pocetak: " + p.getPocetak()
            + ", kraj: " + p.getKraj());
        ContentValues values = new ContentValues();
        values.put(COLUMN_POCETAK, p.getPocetak());
        values.put(COLUMN_KRAJ, p.getKraj());
        values.put(COLUMN_ID_POZICIJE, p.getPozicija().getId());

        id = db.insert(TABLE_POSAO, null, values);
        db.close();
        Log.d("taskoviRac", "a sad je id= " + id);
        return  id;
    }
    //brisanje posla
    public void deleteJob(int id){
        SQLiteDatabase db = getWritableDatabase();
        //Log.d("lukas", "id: " + id);
        String whereClause = COLUMN_ID + " = " + id;

        db.delete(TABLE_POSAO, whereClause, null);
        db.close();
    }
    //brisanje svih poslova
    public void deleteAllFromPosao(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_POSAO);
        db.close();
    }
    //brisanje svih poslova do određenog datuma
    public void deleteAllFromPosaoToDate(long datum_proslog_mjeseca){
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = COLUMN_POCETAK + " < " + datum_proslog_mjeseca;

        db.delete(TABLE_POSAO, whereClause, null);
        db.close();
    }


    //uređivanje posla
    public void updateRow(Posao p){
        SQLiteDatabase db = getWritableDatabase();
        //Log.v("lukas", "id: " + p.getId());

        ContentValues values = new ContentValues();
        values.put(COLUMN_POCETAK, p.getPocetak());
        values.put(COLUMN_KRAJ, p.getKraj());
        values.put(COLUMN_ID_POZICIJE, p.getPozicija().getId());

        String whereCaluse = COLUMN_ID + " = " + p.getId();

        db.update(TABLE_POSAO, values, whereCaluse, null);

        db.close();
    }

    //provjera da li je tablica blagdana popunjena
    public boolean checkHolidaysTable(){
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.query(TABLE_BLAGDANI, new String[]{COLUMN_ID_BLAGDANA}, null, null, null, null, null);
        c.moveToFirst();
        Log.d("lukas", "Broj redova u cursoru: " + c.getCount());
        //ako nešđo ima vrati true
        if (c.getCount() > 1)
        {
            c.close();
            db.close();
            return true;
        }

        else {
            c.close();
            db.close();
            return false;
        }
    }

    //dodavanje svih blagdana u tablicu
    public void fillHolidaysTable(List<Long> datumiMs){
        SQLiteDatabase db = getWritableDatabase();

        for (int i = 0; i < datumiMs.size(); i++){
            ContentValues values = new ContentValues();
            values.put(COLUMN_DATUM_BLAGDANA, datumiMs.get(i));
            db.insert(TABLE_BLAGDANI, null, values);
        }
        db.close();
    }

    public List<Posao> getAllJobsTillNow(){
        List<Posao> poslovi = new ArrayList<>();
        Date now = new Date();
        SQLiteDatabase db = getReadableDatabase();
        String select_query = "SELECT * FROM " + TABLE_POSAO
                + " JOIN " + TABLE_POZICIJA + " ON " + COLUMN_POZICIJA_ID + " = " + COLUMN_ID_POZICIJE
                + " WHERE " + COLUMN_KRAJ + " < " + now.getTime()
                + " ORDER BY " + COLUMN_POCETAK + " ASC";
        Cursor c = db.rawQuery(select_query, null);
        //db.rawQuery("SELECT " + COLUMN_POCETAK + " FROM " + TABLE_POSAO + " WHERE")
        if(c.moveToFirst()){
            do {
                Posao p = new Posao();
                p.setId(c.getInt(c.getColumnIndex(COLUMN_ID)));
                p.setPocetak(c.getLong(c.getColumnIndex(COLUMN_POCETAK)));
                p.setKraj(c.getLong(c.getColumnIndex(COLUMN_KRAJ)));
                poslovi.add(p);
            }while (c.moveToNext());
        }
        c.close();
        db.close();
        return poslovi;
    }

    public List<Long> getAllHolidaysTillNow(){
        List<Long> datumiBlagdana = new ArrayList<>();
        Date now = new Date();
        SQLiteDatabase db = getReadableDatabase();
        String whereClause = COLUMN_DATUM_BLAGDANA + " < " + now.getTime();
        Cursor c = db.query(TABLE_BLAGDANI, new String[]{COLUMN_DATUM_BLAGDANA}, whereClause, null, null, null, null);
        if(c.moveToFirst()){
            do {
                datumiBlagdana.add(c.getLong(c.getColumnIndex(COLUMN_DATUM_BLAGDANA)));
            }while (c.moveToNext());
        }
        c.close();
        db.close();
        return datumiBlagdana;
    }

    public void deleteHoliday(long date){
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = COLUMN_DATUM_BLAGDANA + " = " + date;
        db.delete(TABLE_BLAGDANI, whereClause, null);
    }

    public void insertHoliday(long date){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATUM_BLAGDANA, date);
        db.insert(TABLE_BLAGDANI, null, values);
    }


    public Posao getJobOnId(int id){
        Posao p = new Posao();
        SQLiteDatabase db = getReadableDatabase();
        String whereClause = COLUMN_ID + "=" + id;
        Cursor c = db.query(TABLE_POSAO, null, whereClause, null, null, null, null);
        if (c.getCount() > 0){
            c.moveToFirst();
            p.setPocetak(c.getLong(c.getColumnIndex(COLUMN_POCETAK)));
            p.setKraj(c.getLong(c.getColumnIndex(COLUMN_KRAJ)));
            p.setId(id);
            p.setPozicija_id(c.getInt(c.getColumnIndex(COLUMN_ID_POZICIJE)));
            c.close();
            db.close();
            return p;
        }else {
            c.close();
            db.close();
            return null;
        }
    }

}
