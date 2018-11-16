package com.example.biank.meuapp.Classes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biank.meuapp.Activity.Inicial;
import com.example.biank.meuapp.Activity.MostrarEstrategia;
import com.example.biank.meuapp.Activity.PrincipalActivity;
import com.example.biank.meuapp.Activity.editMetodo;
import com.example.biank.meuapp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class AdapterPesquisaHome extends RecyclerView.Adapter<AdapterPesquisaHome.ViewHolder>{


    private ArrayList<String> nomes;
    FragmentActivity context;
    DatabaseReference referenciaFirebase;

    public AdapterPesquisaHome(FragmentActivity context , ArrayList<String> names) {
        this.context =  context;
        this.nomes = names;


    }
    public void filterList(ArrayList<String> filterdNames) {
        this.nomes = filterdNames;
        notifyDataSetChanged();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.suasestrategias, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.nomeEstrategia.setText(nomes.get(position));
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                PrincipalActivity p = (PrincipalActivity) context;
                MostrarEstrategia.setValor(nomes.get(position));
                Intent i = new Intent(p, MostrarEstrategia.class);
                p.startActivity(i);


            }
        });
        holder.apaga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                referenciaFirebase.child("metodologias").orderByChild("titulo").equalTo(nomes.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            postSnapshot.getRef().removeValue();


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });

            }
        });
        holder.edita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


               PrincipalActivity p = (PrincipalActivity) context;
                editMetodo.setValor(nomes.get(position));
                Intent i = new Intent(p,editMetodo.class);
                p.startActivity(i);



            }
        });

    }



    @Override
    public int getItemCount() {

            return nomes.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView nomeEstrategia;
        ImageButton apaga,edita;
        private ItemClickListener itemClickListener;



        public ViewHolder(View itemView) {
            super(itemView);
            nomeEstrategia = (TextView) itemView.findViewById(R.id.tvtituloinicial);
            apaga = (ImageButton) itemView.findViewById(R.id.apaga);
            edita = (ImageButton) itemView.findViewById(R.id.edit);
            referenciaFirebase = FirebaseDatabase.getInstance().getReference();
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
