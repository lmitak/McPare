package hr.apps.cookies.mcpare.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Date;
import java.util.List;

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
        myViewHolder.stavka1.setText(podatak.getPozicija());
        Double placa = podatak.getKoefPlaca() * podatak.getOsnovica();
        myViewHolder.stavka2.setText(placa.toString());
        myViewHolder.stavka3.setText(podatak.getDatum_do().toString());
    }

    @Override
    public int getItemCount() {
        return listaPodataka.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView stavka1, stavka2, stavka3;

        public MyViewHolder(View itemView) {
            super(itemView);
            stavka1 = (TextView) itemView.findViewById(R.id.stavka1);
            stavka2 = (TextView) itemView.findViewById(R.id.stavka2);
            stavka3 = (TextView) itemView.findViewById(R.id.stavka3);
        }
    }
}


