package com.example.antonio.androidappseminar.data.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonio on 21.5.2015..
 */
public class ZapisHelper extends SQLiteOpenHelper {

    private static final String LOG = "DBKnjigeHelper";

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "McPare";

    private static final String TABLE_ZAPIS = "zapis";

    private static final String KEY_ID = "id";
    private static final String KEY_POZICIJA = "pozicija";
    private static final String KEY_DATUM_OD = "datum_od";
    private static final String KEY_DATUM_DO = "datum_do";
    private static final String KEY_OSNOVICA = "osnovica";
    private static final String KEY_KOEF_PLACA = "koefPlaca";

    public static final String CREATE_TABLE_ZAPIS = "CREATE TABLE " + TABLE_ZAPIS + "(" + KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_POZICIJA + " VARCHAR(5)," + KEY_DATUM_OD + " DATETIME," + KEY_DATUM_DO + " DATETIME," +
            KEY_OSNOVICA + " DECIMAL(7,2)," + KEY_KOEF_PLACA + " DECIMAL(3,2)" + ")";

    public ZapisHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ZAPIS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ZAPIS);
        onCreate(db);
    }

    /**
     * NOVI ZAPIS
     */
    public long createTableZapis(Zapis zapis){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_POZICIJA,zapis.getPozicija());
        values.put(KEY_DATUM_OD, String.valueOf(zapis.getDatum_od()));
        values.put(KEY_DATUM_DO, String.valueOf(zapis.getDatum_do()));
        values.put(KEY_OSNOVICA, zapis.getOsnovica());
        values.put(KEY_KOEF_PLACA, zapis.getKoefPlaca());

        long id = db.insert(TABLE_ZAPIS, null, values);

        return id;
    }

    public long insertZapis(Zapis zapis){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_POZICIJA,zapis.getPozicija());
        values.put(KEY_DATUM_OD, String.valueOf(new java.sql.Date(zapis.getDatum_od().getTime())));//TODO watch this if it goes batshit crazy then something is wrong
        values.put(KEY_DATUM_DO, String.valueOf(new java.sql.Date(zapis.getDatum_do().getTime())));//TODO watch this if it goes batshit crazy then something is wrong
        values.put(KEY_OSNOVICA, zapis.getOsnovica());
        values.put(KEY_KOEF_PLACA, zapis.getKoefPlaca());

        long id = db.insert(TABLE_ZAPIS,null,values);

        return id;
    }

    public long deleteZapis(Zapis zapis){
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = KEY_POZICIJA + " = " + zapis.getPozicija() + " AND " +
                            KEY_DATUM_OD + " = " + zapis.getDatum_od() + " AND " +
                            KEY_DATUM_DO + " = " + zapis.getDatum_do() + " AND " +
                            KEY_OSNOVICA + " = " + zapis.getOsnovica() + " AND " +
                            KEY_KOEF_PLACA + " = " + zapis.getKoefPlaca();

        long id = db.delete(TABLE_ZAPIS, whereClause, null);

        return id;
    }

    public void deleteZapisByID(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ZAPIS, KEY_ID + " = " + id, null);
    }

    public void deleteAllFromZapis(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_ZAPIS);
    }

    /**
     *
     * @param order String for ordering instruction ASC -> order ASCENGING by start date,
     *              DESC -> order DESCENDING by start date, EMPTY STRING for default DB ordering
     *
     * @return
     */
    public List<Zapis> getAllFromZapis(String order){

        List<Zapis> listaZapisa = new ArrayList<Zapis>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = null;

        if(order.equals("ASC"))
            selectQuery = "SELECT * FROM " + TABLE_ZAPIS + " ORDER BY " + KEY_DATUM_OD + " ASC;";
        else if(order.equals("DESC"))
            selectQuery = "SELECT * FROM " + TABLE_ZAPIS + " ORDER BY " + KEY_DATUM_OD + " DESC;";
        else
            selectQuery = "SELECT * FROM " + TABLE_ZAPIS + ";";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //ovako izgleda format datuma java.util.sql.Date

        if (c.moveToFirst()) {
            do {
                Zapis z = new Zapis();
                z.setId(c.getInt(0));
                z.setPozicija(c.getString(1));

                java.util.Date dateStart = null;
                java.util.Date dateFinish = null;
                try {
                    dateStart = df.parse(c.getString(2));
                    dateFinish = df.parse(c.getString(3));
                }
                catch (ParseException ex){
                    Log.i("parse exception", ex.getMessage());
                }

                z.setDatum_od(new java.sql.Date(dateStart.getTime()));//TODO check this stuff it might not work
                z.setDatum_do(new java.sql.Date(dateFinish.getTime()));//TODO
                z.setOsnovica(c.getDouble(4));
                z.setKoefPlaca(c.getDouble(5));

                listaZapisa.add(z);
            } while (c.moveToNext());
        }

        return  listaZapisa;
    }

    public List<Zapis> getListZapisByMonth(java.sql.Date datum){
        List<Zapis> listaZapisa = new ArrayList<Zapis>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = null;

        selectQuery = "SELECT * FROM " + TABLE_ZAPIS + " WHERE strftime('%m'," +
                KEY_DATUM_OD + ") = strftime('%m'," + datum.toString() + ") AND strftime('%Y'," +
                KEY_DATUM_OD + ") = strftime('%Y'," + datum.toString() + ") ORDER BY " +
                KEY_DATUM_OD + " ASC;";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        if (c.moveToFirst()) {
            do {
                Zapis z = new Zapis();
                z.setId(c.getInt(0));
                z.setPozicija(c.getString(1));

                java.util.Date dateStart = null;
                java.util.Date dateFinish = null;
                try {
                    dateStart = df.parse(c.getString(2));
                    dateFinish = df.parse(c.getString(3));
                }
                catch (ParseException ex){
                    Log.i("parse exception", ex.getMessage());
                }

                z.setDatum_od(new java.sql.Date(dateStart.getTime()));//TODO check this stuff it might not work
                z.setDatum_do(new java.sql.Date(dateFinish.getTime()));//TODO
                z.setOsnovica(c.getDouble(4));
                z.setKoefPlaca(c.getDouble(5));

                listaZapisa.add(z);
            } while (c.moveToNext());
        }

        return  listaZapisa;
    }

    public long getZapisID(Zapis zapis){
        String selectQuery = "SELECT " + KEY_ID + " FROM " + TABLE_ZAPIS + " WHERE "
                + KEY_POZICIJA + " = " + zapis.getPozicija() + " AND "
                + KEY_DATUM_OD + " = " + zapis.getDatum_od() + " AND "
                + KEY_DATUM_DO + " = " + zapis.getDatum_do() + " AND "
                + KEY_KOEF_PLACA + " = " + zapis.getKoefPlaca() + " AND "
                + KEY_OSNOVICA + " = " + zapis.getOsnovica() + ";";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c =  db.rawQuery(selectQuery, null);                    //db.execSQL(selectQuery);

        if(c.moveToFirst())
            return c.getInt(0);
        else
            return -1;
    }

    public long updateZapis(Zapis oldZapis,Zapis newZapis){
        long id = getZapisID(oldZapis);
        String updateQuery = "UPDATE " + TABLE_ZAPIS +
                            " SET " + KEY_POZICIJA + " = " + newZapis.getPozicija() + "," +
                            KEY_DATUM_OD + " = " + newZapis.getDatum_od() + "," +
                            KEY_DATUM_DO + " = " + newZapis.getDatum_do() + "," +
                            KEY_KOEF_PLACA + " = " + newZapis.getKoefPlaca() + "," +
                            KEY_OSNOVICA +  " = " + newZapis.getOsnovica() +
                            " WHERE " + KEY_ID + " = " + id + ";";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(updateQuery,null);

        if(c.moveToFirst())
            return id;
        else
            return -1;
    }

    public long updateZapisByID(long id,Zapis newZapis){

        String updateQuery = "UPDATE " + TABLE_ZAPIS +
                " SET " + KEY_POZICIJA + " = " + newZapis.getPozicija() + "," +
                KEY_DATUM_OD + " = " + newZapis.getDatum_od() + "," +
                KEY_DATUM_DO + " = " + newZapis.getDatum_do() + "," +
                KEY_KOEF_PLACA + " = " + newZapis.getKoefPlaca() + "," +
                KEY_OSNOVICA +  " = " + newZapis.getOsnovica() +
                " WHERE " + KEY_ID + " = " + id + ";";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(updateQuery,null);

        if(c.moveToFirst())
            return id;
        else
            return -1;
    }
}
