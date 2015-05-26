package hr.apps.cookies.mcpare.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hr.apps.cookies.mcpare.R;
import hr.apps.cookies.mcpare.adapters.RecyclerAdapter;
import hr.apps.cookies.mcpare.objects.Podaci;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTrenutni extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;


    public FragmentTrenutni() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        List<Podaci> podaci= new ArrayList<Podaci>();
        podaci.add(new Podaci("wupy", "mupy", "u grupi"));
        podaci.add(new Podaci("baza", "maza", "gaza"));
        podaci.add(new Podaci("kisa", "misa", "cesta"));

        View layout = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler);
        adapter = new RecyclerAdapter(getActivity(), podaci);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        TextView whatFragment = (TextView) layout.findViewById(R.id.whatFragment);
        whatFragment.setText("Ovo je fragment 0");

        return layout;
    }


}
