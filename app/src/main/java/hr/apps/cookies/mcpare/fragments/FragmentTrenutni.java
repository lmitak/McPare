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
import android.widget.TextView;

import java.sql.Date;
import java.util.List;

import hr.apps.cookies.mcpare.R;
import hr.apps.cookies.mcpare.adapters.RecyclerAdapter;
import hr.apps.cookies.mcpare.data.Zapis;
import hr.apps.cookies.mcpare.data.ZapisHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTrenutni extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    FragmentTrenutniComunicator comunicator;
    List<Zapis> podaci;


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
        // Inflate the layout for this fragment

        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//        Button btnAdd = (Button)getActivity().findViewById(R.id.addZapis);
//        btnAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                NumberPickerDialog dialog = new NumberPickerDialog();
//                dialog.show(getActivity().getFragmentManager(),"trenutni");
//            }
//        });


        ZapisHelper zapisHelper = new ZapisHelper(getActivity().getApplication());
        podaci = zapisHelper.getListZapisByMonth(new Date(new java.util.Date().getTime()));

        java.util.Date start = null,end = null;

//        try {
//            start = sdf.parse("2015-02-11 11:00:00");
//            end = sdf.parse("2015-02-11 19:00:00");
//        }
//        catch (ParseException e){
//            Log.i("djuro exception", "Puko datum kod parsanja iz SDF-a u java.util.sql.Date");
//        }

       /* podaci.add(new Zapis("LOB",new java.sql.Date(start.getTime()),new Date(end.getTime()),(double)18,1.3));      // osnovica ili koeficijent ikog smetaju popričat
        // ćemo al ovo ostaje samo nezz jel ćemo to unosit u svim slučajevima

        podaci.add(new Zapis("KUH",new java.sql.Date(start.getTime()),new java.sql.Date(end.getTime()), (double) 19,1.8));
        podaci.add(new Zapis("IST",new java.sql.Date(start.getTime()),new Date(end.getTime()),(double)18,1.0));
*/
        View layout = inflater.inflate(R.layout.fragment_list, container, false);

        //FloatingActionButton button = (FloatingActionButton) layout.findViewById(R.id.fab);
        //button.show(true);

        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler);
        recyclerView.setTag("trenutni");
        adapter = new RecyclerAdapter(getActivity(), podaci);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        TextView whatFragment = (TextView) layout.findViewById(R.id.whatFragment);
        whatFragment.setText("Ovo je fragment 0");

        Button addZapis = (Button) layout.findViewById(R.id.addZapis);
        addZapis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comunicator.startDialog(recyclerView.getTag().toString());
            }
        });

        return layout;
    }

    public interface FragmentTrenutniComunicator {
        public void startDialog(String recylcerTag);
    }

    public void dodajURecycler(String pozicija, Date datum, Date start, Date end){
        podaci.add(new Zapis(pozicija, start, end, (double)18,1.3));
        adapter.notifyDataSetChanged();
    }
    public void izbrisiIzRecycler(int pozicija) {
        podaci.remove(pozicija);
        adapter.notifyDataSetChanged();
    }

}
