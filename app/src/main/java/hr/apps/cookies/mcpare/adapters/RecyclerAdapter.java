package hr.apps.cookies.mcpare.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import hr.apps.cookies.mcpare.R;
import hr.apps.cookies.mcpare.data.Posao;

/**
 * Created by lmita_000 on 26.5.2015..
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    private List<Posao> listaPodataka;

    public RecyclerAdapter(Context context, List<Posao> lista){
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

        Posao podatak = listaPodataka.get(i);
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE, dd.");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        Calendar calendar = Calendar.getInstance();
        myViewHolder.pozicija.setText(podatak.getPozicija().getIme_pozicija());
        myViewHolder.datum_do.setText(timeFormat.format(podatak.getKraj()));
        myViewHolder.datum_od.setText(timeFormat.format(podatak.getPocetak()));
        myViewHolder.datum.setText(dayFormat.format(podatak.getPocetak()));
    }

    @Override
    public int getItemCount() {
        return listaPodataka.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView pozicija, datum_od, datum_do, datum;

        public MyViewHolder(View itemView) {
            super(itemView);
            pozicija = (TextView) itemView.findViewById(R.id.pozicijaText);
            datum_od = (TextView) itemView.findViewById(R.id.pocetakText);
            datum_do = (TextView) itemView.findViewById(R.id.krajText);
            datum = (TextView) itemView.findViewById(R.id.datumText);
        }
    }
}


