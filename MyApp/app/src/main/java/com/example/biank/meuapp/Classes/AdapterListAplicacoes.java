package com.example.biank.meuapp.Classes;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biank.meuapp.Activity.ListarAplicacoes;
import com.example.biank.meuapp.Activity.MainActivity;
import com.example.biank.meuapp.Activity.MostrarAplicacao;
import com.example.biank.meuapp.Activity.MostrarEstrategia;
import com.example.biank.meuapp.Activity.PesquisarEstrategia;
import com.example.biank.meuapp.Activity.PrincipalActivity;
import com.example.biank.meuapp.R;

import java.util.ArrayList;




public class AdapterListAplicacoes extends RecyclerView.Adapter<AdapterListAplicacoes.ViewHolder>{


    private ArrayList<String> nomes;
    private ArrayList<String> nomesd;
    private ArrayList<String> nomesa;
    private ArrayList<String> ids;
    Context context;


    public AdapterListAplicacoes( Context context, ArrayList<String> names,ArrayList<String> nomesd,ArrayList<String> nomesa,ArrayList<String> ids) {
        this.context = context;
        this.nomes = names;
        this.nomesd = nomesd;
        this.nomesa = nomesa;
        this.ids = ids;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listaplicacoes, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.disc.setText(nomesd.get(position));
        holder.estrategia.setText(nomes.get(position));
        holder.autor.setText(nomesa.get(position));
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {

                Intent i = new Intent(context, MostrarAplicacao.class);
                i.putExtra("id",ids.get(position));
                i.putExtra("pos",position);
                context.startActivity(i);


            }
        });
    }

    @Override
    public int getItemCount() {
        return nomes.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {


        TextView estrategia;
        TextView autor;
        TextView disc;
        private ItemClickListener itemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            disc = (TextView) itemView.findViewById(R.id.tvdisc);
            estrategia = (TextView) itemView.findViewById(R.id.tvestrategia1);
            autor = (TextView) itemView.findViewById(R.id.tvautorap);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }
        public void setItemClickListener (ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }


        public void onClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),false);

        }

        public boolean onLongClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),true);
            return true;
        }
    }

}
