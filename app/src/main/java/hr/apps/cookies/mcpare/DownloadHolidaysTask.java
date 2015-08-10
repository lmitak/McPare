package hr.apps.cookies.mcpare;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hr.apps.cookies.mcpare.MainActivity;
import hr.apps.cookies.mcpare.asyncTasks.RacunanjeTask;
import hr.apps.cookies.mcpare.data.DBHelper;

/**
 * Created by lmita_000 on 4.8.2015..
 */
public class DownloadHolidaysTask extends AsyncTask<Void, Void, Void> {

    private MainActivity mActivity;
    DBHelper helper;

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        RacunanjeTask racunanjeTask = new RacunanjeTask(mActivity);
        racunanjeTask.execute(0.0);
    }

    /**
     * Constructor.
     * @param activity MainActivity that spawned this task.
     */
    DownloadHolidaysTask(MainActivity activity) {
        this.mActivity = activity;
    }

    /**
     * Background task to call Google Calendar API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {
            helper = new DBHelper(mActivity.getApplicationContext());
            //dodavanje skinutih datuma u kalendar
            helper.fillHolidaysTable(getHolidays());

            //File folder = mActivity.getCacheDir();
            //File file = new File(folder, "holidays.txt");


        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
            mActivity.showGooglePlayServicesAvailabilityErrorDialog(
                    availabilityException.getConnectionStatusCode());

        } catch (UserRecoverableAuthIOException userRecoverableException) {
            mActivity.startActivityForResult(
                    userRecoverableException.getIntent(),
                    MainActivity.REQUEST_AUTHORIZATION);


        } catch (Exception e) {
            Log.d("lukasTask", e.getMessage());
            mActivity.updateStatus("The following error occurred:\n" +
                    e.getMessage());
        }
        return null;
    }

    //izvlači sve datume u ms iz google kalendara (od trenutnog datuma do zadnje godine (u biti trenutna i slj.))
    private List<Long> getHolidays() throws IOException{
        Log.d("lukasTask", "iii krenuli smo...");
        List<Long> datumiUMs = new ArrayList<>();
        //Events events = mActivity.mService.events().list("en.usa#holiday@group.v.calendar.google.com")
        /*
        com.google.api.services.calendar.model.Calendar calendar =
                mActivity.mService.calendars().get("hr.croatian#holiday@group.v.calendar.google.com")
                        .execute();

        Collection collection = calendar.values();
        Set<String> set = calendar.keySet();
        for (String s: set){
            Log.d("kolekcija", "key: " + s + ", value: " + calendar.get(s));
        }*/
        // Construct the {@link Calendar.Events.List} request, but don't execute it yet.
        Calendar.Events.List request = mActivity.mService.events().list("hr.croatian#holiday@group.v.calendar.google.com");
        // Load the sync token stored from the last execution, if any.
        String syncToken = getSyncToken();

        if (syncToken == null){
            //Performing full sync.

            // Set the filters you want to use during the full sync. Sync tokens aren't compatible with
            // most filters, but you may want to limit your full sync to only a certain date range.
            // In this example we are only syncing events up to a year old.
            request.setTimeMin(new DateTime(System.currentTimeMillis()));
        }else {
            //Performing incremental sync.
            request.setSyncToken(syncToken);
            Log.d("lukasTask", "do tu ide i daje synctoken= "  + syncToken);
        }
        // Retrieve the events, one page at a time.
        String pageToken = null;
        Events events = null;

        do{
            request.setPageToken(pageToken);
            Log.d("lukasTask", "do tu ide3");
            try {
                events = request.execute();
                Log.d("lukasTask", "do tu ide4");
            }catch (GoogleJsonResponseException e){
                Log.d("lukasTask", "wupsy");
                if (e.getStatusCode() == 410) {
                    // A 410 status code, "Gone", indicates that the sync token is invalid.
                    Log.d("lukasTask","Invalid sync token, clearing event store and re-syncing.");
                    deleteSyncToken();
                    doInBackground();
                } else {
                    throw e;
                }
            }
            Log.d("lukasTask", "i do tu ide");
            List<Event> eventList = events.getItems();
            if (eventList.size() == 0){
                Log.d("lukasTask", "nothing to sync");
            }else {
                //List<Long> spremljeniPraznici = new ArrayList<>();
                Log.d("lukasTask", "sync amount: " + eventList.size());
                //spremljeniPraznici = helper.getAllHolidyas();

                for (Event e : eventList){

                    /*
                    Log.d("lukas3", "Summary: " + e.getSummary() + "\nDate" + dateTime.toString());
                    datumiUMs.add(dateTime.getValue());*/
                    syncEvent(e);

                }

            }
            pageToken = events.getNextPageToken();
            Log.d("lukasTask", "pageToken: " + pageToken);
        }while (pageToken != null);
        /*Events events = mActivity.mService.events().list("hr.croatian#holiday@group.v.calendar.google.com")
                .setTimeMin(new DateTime(System.currentTimeMillis())).execute();*/
        //Log.d("kolekcija", "token: " + events.getNextSyncToken());

        putSyncToken(events.getNextSyncToken());
        Log.d("lukasTask", "syncToken: " + events.getNextSyncToken());
        return datumiUMs;
    }

    private void syncEvent(Event e) {

        EventDateTime eDateTime = e.getStart();
        DateTime dateTime = eDateTime.getDate();

        if ("canceled".equals(e.getStatus())){
            helper.deleteHoliday(dateTime.getValue());
        }else {
            helper.insertHoliday(dateTime.getValue());
        }

    }

    private void writeDate(File file, List<Long> longs){
        FileWriter writter = null;
        BufferedWriter bw = null;
        try {
            writter = new FileWriter(file);
            bw = new BufferedWriter(writter);
            for (Long l: longs){
                bw.append(l.toString());
                bw.newLine();
            }
        }catch (IOException ex){
            Log.d("lukas", ex.getMessage());
        }finally {
            if (bw != null){
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (writter != null){
                try {
                    writter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<Long> readData(File file){
        FileReader reader = null;
        BufferedReader br = null;
        String line;
        ArrayList<Long> list = new ArrayList<>();
        try {
            reader = new FileReader(file);
            br = new BufferedReader(reader);
            while ((line = br.readLine()) != null){
                list.add(Long.parseLong(line));
            }
        }catch (IOException ex){
            Log.d("lukas", ex.getMessage());
        }finally {
            if (br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return  list;
    }

    private String getSyncToken(){
        SharedPreferences sp = mActivity.getSharedPreferences(
                mActivity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sp.getString("synctoken", null);
    }

    private void putSyncToken(String token){
        SharedPreferences sp = mActivity.getSharedPreferences(
                mActivity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("synctoken", token);
        editor.apply();
    }

    private void deleteSyncToken(){
        SharedPreferences sp = mActivity.getSharedPreferences(
                mActivity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("synctoken", null);
        editor.apply();
    }


}
