package hr.apps.cookies.mcpare.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import hr.apps.cookies.mcpare.R;
import hr.apps.cookies.mcpare.adapters.RecyclerAdapter;
import hr.apps.cookies.mcpare.data.Zapis;
import hr.apps.cookies.mcpare.data.ZapisHelper;

/**
 * Created by lmita_000 on 26.5.2015..
 */
public class FragmentSljedeci extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    FragmentProsli.FragmentComunicator comunicator;



    public FragmentSljedeci() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        ZapisHelper zapisHelper = new ZapisHelper(getActivity().getApplication());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        calendar.add(Calendar.MONTH,1);
        java.util.Date datum = calendar.getTime();

        List<Zapis> podaci = zapisHelper.getListZapisByMonth(new Date(datum.getTime()));

        //java.util.Date start = null,end = null;

        /*try {
            start = sdf.parse("2015-02-11 11:00:00");
            end = sdf.parse("2015-02-11 19:00:00");
        }
        catch (ParseException e){
            Log.i("djuro exception", "Puko datum kod parsanja iz SDF-a u java.util.sql.Date");
        }*/

       /* podaci.add(new Zapis("LOB",new Date(start.getTime()),new Date(end.getTime()),(double)18,1.3));      // ako osnovica ili koeficijent ikog smetaju popričat
        // ćemo al ovo ostaje samo nezz jel ćemo to unosit u svim slučajevima
        podaci.add(new Zapis("KUH",new Date(start.getTime()),new Date(end.getTime()),(double) 19,1.8));
        podaci.add(new Zapis("IST",new Date(start.getTime()),new Date(end.getTime()),(double)18,1.0));*/


        View layout = inflater.inflate(R.layout.fragment_list, container, false);
/*
        FloatingActionButton button = (FloatingActionButton) layout.findViewById(R.id.fab);
        button.show(true);
*/
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler);
        adapter = new RecyclerAdapter(getActivity(), podaci);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        TextView whatFragment = (TextView) layout.findViewById(R.id.whatFragment);
        whatFragment.setText("Ovo je fragment +1");

        Button addZapis = (Button) layout.findViewById(R.id.addZapis);
        addZapis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comunicator.startDialog();
            }
        });

        return layout;
    }

    public interface FragmentComunicator{
        public void startDialog();
    }
}
