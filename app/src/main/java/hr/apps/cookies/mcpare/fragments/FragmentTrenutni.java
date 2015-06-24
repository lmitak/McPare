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

import java.util.Collections;
import java.util.List;

import hr.apps.cookies.mcpare.R;
import hr.apps.cookies.mcpare.adapters.RecyclerAdapter;
import hr.apps.cookies.mcpare.data.DBHelper;
import hr.apps.cookies.mcpare.data.Posao;
import hr.apps.cookies.mcpare.objects.RecyclerItemClickListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTrenutni extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    FragmentTrenutniComunicator comunicator;
    List<Posao> podaci;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.comunicator = (FragmentTrenutniComunicator) activity;
    }


    public FragmentTrenutni() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DBHelper helper = new DBHelper(getActivity().getApplicationContext());
        podaci = helper.getAllJobsInMonth(new java.util.Date().getTime());

        View layout = inflater.inflate(R.layout.fragment_list, container, false);

        //FloatingActionButton button = (FloatingActionButton) layout.findViewById(R.id.fab);
        //button.show(true);

        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler);
        recyclerView.setTag("trenutni");
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


        Button addZapis = (Button) layout.findViewById(R.id.addZapis);
        addZapis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comunicator.startDialog();
            }
        });

        return layout;
    }

    public interface FragmentTrenutniComunicator {
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

    public void updateItemInRecycle(Posao p, int position){
        podaci.add(position, p);
        podaci.remove(position + 1);
        Collections.sort(podaci);
        adapter.notifyDataSetChanged();
    }
    public void pozoviComunicator(){
        comunicator.startDialog();
    }

}
