package hr.apps.cookies.mcpare.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import hr.apps.cookies.mcpare.data.DBHelper;

/**
 * Created by lmita_000 on 9.8.2015..
 */
public class CleanDBTask extends AsyncTask<Long, Void, Void> {

    Context context;

    public CleanDBTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Long... longs) {
        DBHelper helper = new DBHelper(context);
        helper.deleteAllFromPosaoToDate(longs[0]);
        return null;
    }
}
