package com.example.biank.meuapp.Classes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biank.meuapp.Activity.CadastrarAplicacao;
import com.example.biank.meuapp.Activity.PesquisarEstrategia;
import com.example.biank.meuapp.Activity.PrincipalActivity;
import com.example.biank.meuapp.R;


import java.util.ArrayList;
class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

    TextView titulo;
    TextView autor;
    EditText estrategia;

    private ItemClickListener itemClickListener;


    public SearchViewHolder(View itemView) {
        super(itemView);
        titulo = (TextView) itemView.findViewById(R.id.Optitulo);
        autor = (TextView) itemView.findViewById(R.id.OpAutor);
        estrategia = (EditText) itemView.findViewById(R.id.selectmetodo);


        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }
    public void setItemClickListener (ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);

    }

    @Override
    public boolean onLongClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),true);
        return true;
    }
}

public class SearchEstrategia extends RecyclerView.Adapter<SearchViewHolder> {

    Context context;
    ArrayList<String> tituloList;
    ArrayList<String>user_cadastroList;



    public SearchEstrategia(FragmentActivity context, ArrayList<String> tituloList, ArrayList<String> user_cadastroList) {
        this.context = context;
        this.tituloList = tituloList;
        this.user_cadastroList = user_cadastroList;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.buscaestrategia, parent,false);
        return new SearchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SearchViewHolder holder, int position) {
        holder.titulo.setText(tituloList.get(position));
        holder.autor.setText(user_cadastroList.get(position));
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {

                CadastrarAplicacao.getEstrategia().setText(tituloList.get(position).toString());
                //view.findViewById(R.id.estrategialayout).setVisibility(View.GONE);









            }

        });

    }

    @Override
    public int getItemCount() {
        return tituloList.size();
    }




}
