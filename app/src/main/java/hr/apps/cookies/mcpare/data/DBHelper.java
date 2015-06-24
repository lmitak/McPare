package hr.apps.cookies.mcpare.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
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

    //Columns
    //table posao
    private static String COLUMN_ID = "id";
    private static String COLUMN_ID_POZICIJE = "id_pozicije";
    private static String COLUMN_POCETAK = "pocetak";
    private static String COLUMN_KRAJ = "kraj";
    //table pozicija
    private static String COLUMN_NAZIV = "naziv";
    private static String COLUMN_POZICIJA_ID = "pozicija_id";

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
    public void insertJob(Posao p){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_POCETAK, p.getPocetak());
        values.put(COLUMN_KRAJ, p.getKraj());
        values.put(COLUMN_ID_POZICIJE, p.getPozicija().getId());

        db.insert(TABLE_POSAO, null, values);
        db.close();
    }

    public void deleteJob(int id){
        SQLiteDatabase db = getWritableDatabase();
        //Log.d("lukas", "id: " + id);
        String whereClause = COLUMN_ID + " = " + id;

        db.delete(TABLE_POSAO, whereClause, null);
        db.close();
    }

    public void deleteAllFromPosao(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_POSAO);
        db.close();
    }

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

}
