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
    //FragmentProsliComunicator comunicator;
    //List<Zapis> podaci;
    List<Posao> podaci;


    public FragmentProsli() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //this.comunicator = (FragmentProsliComunicator) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        calendar.add(Calendar.MONTH, -1);

        //DBHelper helper = new DBHelper(getActivity().getApplicationContext());
        //podaci = helper.getAllJobsInMonth(calendar.getTimeInMillis());


        //FloatingActionButton button = (FloatingActionButton) getActivity().findViewById(R.id.fab);
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

        //FloatingActionButton button = (FloatingActionButton) layout.findViewById(R.id.fab);
        //button.hide(true);
        //FloatingActionButton floatingButton = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        //((LinearLayout)floatingButton.getParent()).removeView(floatingButton);

        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler);
        recyclerView.setTag("prosli");
        adapter = new RecyclerAdapter(getActivity(), podaci);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //brisanje gumba da se ne vidi
        /*Button addZapis = (Button) layout.findViewById(R.id.addZapis);
        ((LinearLayout)addZapis.getParent()).removeView(addZapis);*/

        return layout;
    }
    /*
        public interface FragmentProsliComunicator{
            public void startDialog();
        }

    */
    public void izbrisiIzRecycler(int pozicija) {
        podaci.remove(pozicija);
        adapter.notifyDataSetChanged();
    }
}
