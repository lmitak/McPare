package hr.apps.cookies.mcpare.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

//import com.github.clans.fab.FloatingActionButton;

import com.github.clans.fab.FloatingActionButton;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import hr.apps.cookies.mcpare.R;
import hr.apps.cookies.mcpare.adapters.RecyclerAdapter;
import hr.apps.cookies.mcpare.data.Zapis;
import hr.apps.cookies.mcpare.data.ZapisHelper;
import hr.apps.cookies.mcpare.objects.Podaci;

/**
 * Created by lmita_000 on 26.5.2015..
 */
public class FragmentProsli extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    //FragmentProsliComunicator comunicator;
    List<Zapis> podaci;


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
        // Inflate the layout for this fragment
        ZapisHelper zapisHelper = new ZapisHelper(getActivity().getApplication());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        //calendar.setTime(new java.util.Date());
        calendar.add(Calendar.MONTH,1);
        java.util.Date datum = calendar.getTime();
        Log.e("Datum out: ", datum.toString());

        podaci = zapisHelper.getListZapisByMonth(new java.sql.Date(datum.getTime()));

        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //java.util.Date start = null,end = null;

       /* try {
            start = sdf.parse("2015-02-11 11:00:00");
            end = sdf.parse("2015-02-11 19:00:00");
        }
        catch (ParseException e){
            Log.i("djuro exception", "Puko datum kod parsanja iz SDF-a u java.util.sql.Date");
        }*/

      /*  podaci.add(new Zapis("LOB",new java.sql.Date(start.getTime()),new java.sql.Date(end.getTime()),(double)18,1.3));      // osnovica ili koeficijent ikog smetaju popričat
        // ćemo al ovo ostaje samo nezz jel ćemo to unosit u svim slučajevima
        podaci.add(new Zapis("KUH",new Date(start.getTime()),new Date(end.getTime()),(double)21,1.8));
        podaci.add(new Zapis("IST",new Date(start.getTime()),new Date(end.getTime()),(double)18,1.0));*/

        //FloatingActionButton button = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        View layout = inflater.inflate(R.layout.fragment_list, container, false);
        //FloatingActionButton button = (FloatingActionButton) layout.findViewById(R.id.fab);
        //button.hide(true);
        //FloatingActionButton floatingButton = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        //((LinearLayout)floatingButton.getParent()).removeView(floatingButton);

        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler);
        recyclerView.setTag("prosli");
        adapter = new RecyclerAdapter(getActivity(), podaci);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        TextView whatFragment = (TextView) layout.findViewById(R.id.whatFragment);
        whatFragment.setText("Ovo je fragment -1");

        //brisanje
        Button addZapis = (Button) layout.findViewById(R.id.addZapis);
        ((LinearLayout)addZapis.getParent()).removeView(addZapis);
/*

        addZapis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comunicator.startDialog();
            }
        });
*/
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
