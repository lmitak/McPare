package hr.apps.cookies.mcpare.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import hr.apps.cookies.mcpare.R;
import hr.apps.cookies.mcpare.adapters.RecyclerAdapter;
import hr.apps.cookies.mcpare.data.Zapis;
import hr.apps.cookies.mcpare.objects.Podaci;

/**
 * Created by lmita_000 on 26.5.2015..
 */
public class FragmentProsli extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;


    public FragmentProsli() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        List<Zapis> podaci= new ArrayList<Zapis>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date start = null,end = null;

        try {
            start = sdf.parse("2015-02-11 11-00-00");
            end = sdf.parse("2015-02-11 19-00-00");
        }
        catch (ParseException e){
            Log.i("djuro exception", "Puko datum kod parsanja iz SDF-a u java.util.sql.Date");
        }

        podaci.add(new Zapis("LOB",start,end,18,1.3));      // osnovica ili koeficijent ikog smetaju popričat
        // ćemo al ovo ostaje samo nezz jel ćemo to unosit u svim slučajevima
        podaci.add(new Zapis("KUH",start,end,19,1.8));
        podaci.add(new Zapis("IST",start,end,18,1.0));

        View layout = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler);
        adapter = new RecyclerAdapter(getActivity(), podaci);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        TextView whatFragment = (TextView) layout.findViewById(R.id.whatFragment);
        whatFragment.setText("Ovo je fragment -1");

        return layout;
    }
}
