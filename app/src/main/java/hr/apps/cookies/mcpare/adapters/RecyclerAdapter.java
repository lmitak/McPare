package hr.apps.cookies.mcpare.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import hr.apps.cookies.mcpare.R;
import hr.apps.cookies.mcpare.data.Zapis;
import hr.apps.cookies.mcpare.objects.Podaci;

/**
 * Created by lmita_000 on 26.5.2015..
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    private List<Zapis> listaPodataka;

    public RecyclerAdapter(Context context, List<Zapis> lista){
        listaPodataka = lista;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = inflater.inflate(R.layout.row, viewGroup, false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        Zapis podatak = listaPodataka.get(i);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - dd. MMMM (EE)");
        SimpleDateFormat dbOutFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //sdf.setTimeZone(TimeZone.getDefault());

        java.util.Date pocDate = null,zavDate = null;
        try {
            pocDate = dbOutFormat.parse(podatak.getDatum_od());
            zavDate = dbOutFormat.parse(podatak.getDatum_do());
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("RecyclerAdapterCrko","parsanje datume kihnulo");
        }

        myViewHolder.pozicija.setText(podatak.getPozicija());
        Double placa = podatak.getKoefPlaca() * podatak.getOsnovica();
        myViewHolder.datum_od.setText(sdf.format(pocDate));
       // myViewHolder.datum_od.setText(new java.util.Date(podatak.getDatum_od().getTime()).toString());
        myViewHolder.datum_do.setText(sdf.format(zavDate));
        //myViewHolder.datum_do.setText("dadas");
    }

    @Override
    public int getItemCount() {
        return listaPodataka.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView pozicija, datum_od, datum_do;

        public MyViewHolder(View itemView) {
            super(itemView);
            pozicija = (TextView) itemView.findViewById(R.id.pozicija);
            datum_od = (TextView) itemView.findViewById(R.id.pocetak);
            datum_do = (TextView) itemView.findViewById(R.id.kraj);
        }
    }
}


