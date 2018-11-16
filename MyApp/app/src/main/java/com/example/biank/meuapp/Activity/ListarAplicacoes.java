package com.example.biank.meuapp.Activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.biank.meuapp.Classes.AdapterListAplicacoes;
import com.example.biank.meuapp.Classes.AdapterPesquisaEstrategia;
import com.example.biank.meuapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListarAplicacoes extends AppCompatActivity {


    String titulo;
    RecyclerView recyclerProcura;
    final ArrayList<String> lista = new ArrayList<String>();
    final ArrayList<String> listadisc = new ArrayList<String>();
    final ArrayList<String> listaautor = new ArrayList<String>();
    final ArrayList<String> listaid = new ArrayList<String>();
    DatabaseReference referenciaFirebase;
    AdapterListAplicacoes adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Listar Aplicações");
        setContentView(R.layout.activity_listar_aplicacoes);
        recyclerProcura = (RecyclerView) findViewById(R.id.rvlistaapp);
        referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        Bundle mBundle = getIntent().getExtras();

        if(mBundle != null){

            titulo = mBundle.getString("titulo");
        }
        AsyncTaskRetro as = new AsyncTaskRetro();
        as.execute();

        recyclerProcura.setHasFixedSize(true);
        recyclerProcura.setLayoutManager(new LinearLayoutManager(this));
    }

    private void Preenche() {

        referenciaFirebase.child("aplicacoes").orderByChild("estrategia").equalTo(titulo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    final String estrategia = postSnapshot.child("estrategia").getValue(String.class);
                    final String disc = postSnapshot.child("disciplina").getValue(String.class);
                    final String user = postSnapshot.child("user").getValue(String.class);
                    final String id = postSnapshot.getKey();
                    lista.add(estrategia);
                    listadisc.add("Em: "+disc);
                    listaautor.add("Por: "+user);
                    listaid.add(id);
                }

                adapter = new AdapterListAplicacoes(getApplicationContext(),lista,listadisc,listaautor,listaid);
                recyclerProcura.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });}

    @Override
    protected void onResume() {
        super.onResume();
    }

    private class AsyncTaskRetro extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ListarAplicacoes.this);
            progressDialog.setTitle("Carregando...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            Preenche();
            progressDialog.dismiss();

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {

        }

    }
}
