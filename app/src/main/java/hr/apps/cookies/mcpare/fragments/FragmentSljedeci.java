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

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import hr.apps.cookies.mcpare.R;
import hr.apps.cookies.mcpare.adapters.RecyclerAdapter;
import hr.apps.cookies.mcpare.asyncTasks.FillRecyclerTask;
import hr.apps.cookies.mcpare.data.DBHelper;
import hr.apps.cookies.mcpare.data.Posao;
import hr.apps.cookies.mcpare.objects.RecyclerItemClickListener;

/**
 * Created by lmita_000 on 26.5.2015..
 */
public class FragmentSljedeci extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    FragmentSljedeciComunicator comunicator;
    List<Posao> podaci;


    public FragmentSljedeci() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.comunicator = (FragmentSljedeciComunicator) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        calendar.add(Calendar.MONTH,1);


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
        recyclerView.setTag("sljedeci");
        adapter = new RecyclerAdapter(getActivity(), podaci);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        comunicator.editDialog(position, podaci.get(position));
                    }
                })
        );

        return layout;
    }

    public interface FragmentSljedeciComunicator{
        public void startDialog();
        public void editDialog(int position, Posao posao);
    }
    public void dodajURecycler(Posao posao){
        podaci.add(posao);
        Collections.sort(podaci);
        adapter.notifyDataSetChanged();
    }


    public void izbrisiIzRecycler(int pozicija) {
        podaci.remove(pozicija);
        adapter.notifyDataSetChanged();
    }
    public void pozoviComunicator(){
        comunicator.startDialog();
    }
}
