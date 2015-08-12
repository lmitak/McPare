package hr.apps.cookies.mcpare.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

//import com.github.clans.fab.FloatingActionButton;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import hr.apps.cookies.mcpare.R;
import hr.apps.cookies.mcpare.adapters.RecyclerAdapter;
import hr.apps.cookies.mcpare.asyncTasks.FillRecyclerTask;
import hr.apps.cookies.mcpare.data.DBHelper;
import hr.apps.cookies.mcpare.data.Posao;

/**
 * Created by lmita_000 on 26.5.2015..
 */
public class FragmentProsli extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    List<Posao> podaci;


    public FragmentProsli() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        calendar.add(Calendar.MONTH, -1);

        View layout = inflater.inflate(R.layout.fragment_list, container, false);

        FillRecyclerTask recyclerTask = new FillRecyclerTask(getActivity().getApplicationContext());
        recyclerTask.execute(calendar.getTimeInMillis());
        try {
            podaci = recyclerTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler);
        recyclerView.setTag("prosli");
        adapter = new RecyclerAdapter(getActivity(), podaci);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        return layout;
    }

}
