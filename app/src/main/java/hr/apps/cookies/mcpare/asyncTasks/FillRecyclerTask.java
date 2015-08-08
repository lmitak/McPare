package hr.apps.cookies.mcpare.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import hr.apps.cookies.mcpare.data.DBHelper;
import hr.apps.cookies.mcpare.data.Posao;

/**
 * Created by lmita_000 on 4.8.2015..
 */
public class FillRecyclerTask extends AsyncTask<Long, Void, List<Posao>> {

    Context context;

    public FillRecyclerTask(Context c) {
        context = c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<Posao> doInBackground(Long... longs) {
        DBHelper helper = new DBHelper(context);
        return helper.getAllJobsInMonth(longs[0]);
    }
}
