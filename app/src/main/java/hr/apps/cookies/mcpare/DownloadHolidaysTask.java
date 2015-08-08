package hr.apps.cookies.mcpare;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hr.apps.cookies.mcpare.MainActivity;
import hr.apps.cookies.mcpare.data.DBHelper;

/**
 * Created by lmita_000 on 4.8.2015..
 */
public class DownloadHolidaysTask extends AsyncTask<Void, Void, Void> {

    private MainActivity mActivity;


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
            //mActivity.clearResultsText();
            //mActivity.updateResultsText(getDataFromApi());
            //testEvnta();
            DBHelper helper = new DBHelper(mActivity.getApplicationContext());
            //dodavanje skinutih datuma u kalendar
            helper.fillHolidaysTable(getHolidays());

        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
            mActivity.showGooglePlayServicesAvailabilityErrorDialog(
                    availabilityException.getConnectionStatusCode());

        } catch (UserRecoverableAuthIOException userRecoverableException) {
            mActivity.startActivityForResult(
                    userRecoverableException.getIntent(),
                    MainActivity.REQUEST_AUTHORIZATION);


        } catch (Exception e) {
            mActivity.updateStatus("The following error occurred:\n" +
                    e.getMessage());
        }
        return null;
    }

    //izvlači sve datume u ms iz google kalendara (od trenutnog datuma do zadnje godine (u biti trenutna i slj.))
    private List<Long> getHolidays() throws IOException{

        List<Long> datumiUMs = new ArrayList<>();
            //Events events = mActivity.mService.events().list("en.usa#holiday@group.v.calendar.google.com")
            Events events = mActivity.mService.events().list("hr.croatian#holiday@group.v.calendar.google.com")
                    .setTimeMin(new DateTime(System.currentTimeMillis())).execute();
            for (Event e : events.getItems()){
                EventDateTime eDateTime = e.getStart();
                DateTime dateTime = eDateTime.getDate();
                Log.d("lukas3", "Summary: " + e.getSummary() + "\nDate" + dateTime.toString());
                datumiUMs.add(dateTime.getValue());
            }
        return datumiUMs;
    }


}
