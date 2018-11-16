package com.example.biank.meuapp.Classes;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.biank.meuapp.Activity.MostrarEstrategia;
import com.example.biank.meuapp.Activity.PrincipalActivity;
import com.example.biank.meuapp.R;

import java.util.ArrayList;




public class AdapterPesquisaEstrategia extends RecyclerView.Adapter<AdapterPesquisaEstrategia.ViewHolder>{


    private ArrayList<String> nomes, autores;
    FragmentActivity context;


    public AdapterPesquisaEstrategia(FragmentActivity context , ArrayList<String> names,ArrayList<String> autores) {
        this.context =  context;
        this.nomes = names;
        this.autores = autores;

    }
    public void filterList(ArrayList<String> filterdNames, ArrayList<String> filterdAutor) {
        this.nomes = filterdNames;
        this.autores = filterdAutor;
        notifyDataSetChanged();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listpesquisaestrategia, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.nomeEstrategia.setText(nomes.get(position));
        holder.nomeAutor.setText(autores.get(position));
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                PrincipalActivity p = (PrincipalActivity) context;
                MostrarEstrategia.setValor(nomes.get(position));
                Intent i = new Intent(p,MostrarEstrategia.class);
                p.startActivity(i);

            }
        });
    }

    @Override
    public int getItemCount() {
        if(nomes != null){
            return nomes.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView nomeEstrategia , nomeAutor;
        private ItemClickListener itemClickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            nomeEstrategia = (TextView) itemView.findViewById(R.id.tvtitulo);
            nomeAutor = (TextView) itemView.findViewById(R.id.tvautor);
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
