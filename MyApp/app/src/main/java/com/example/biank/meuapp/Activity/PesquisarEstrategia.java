package com.example.biank.meuapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.biank.meuapp.Classes.AdapterPesquisaEstrategia;
import com.example.biank.meuapp.Classes.SearchEstrategia;
import com.example.biank.meuapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PesquisarEstrategia extends android.support.v4.app.Fragment {

    RecyclerView recyclerProcura;
    BootstrapEditText editTextSearch;
    ArrayList<String> nomesList = new ArrayList<String>();
    ArrayList<String> autorList = new ArrayList<String>();
    DatabaseReference referenciaFirebase;
    AdapterPesquisaEstrategia adapter;
    ArrayList<String> filterdNames;
    ArrayList<String> filterdAutor;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.activity_pesquisar_estrategia, container, false);

        recyclerProcura = (RecyclerView) v.findViewById(R.id.rvlistaestrategia);
        editTextSearch = (BootstrapEditText) v.findViewById(R.id.edtProcurar);
        referenciaFirebase = FirebaseDatabase.getInstance().getReference();


        AsyncTaskRetro as = new AsyncTaskRetro();
        as.execute();
        recyclerProcura.setHasFixedSize(true);
        recyclerProcura.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerProcura.getContext(),
                new LinearLayoutManager(getActivity()).getOrientation());
        recyclerProcura.addItemDecoration(dividerItemDecoration);
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(adapter!=null){
                    filter(editable.toString());
                }
            }
        });


        return v;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    private void filter(final String text) {
        filterdNames = new ArrayList<String>();
        filterdAutor = new ArrayList<String>();
        referenciaFirebase.child("metodologias").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    final String titulo = postSnapshot.child("titulo").getValue(String.class);
                    String user_cadastro = postSnapshot.child("user").getValue(String.class);

                    if(titulo.toLowerCase().contains(text.toLowerCase())){
                        if (titulo.toLowerCase().equals(text.toLowerCase())){


                        }
                        else{
                           filterdNames.add(titulo);
                           filterdAutor.add(user_cadastro);

                        }

                    }

                    adapter.filterList(filterdNames,filterdAutor);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void Preenche() {

        referenciaFirebase.child("metodologias").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

             nomesList.clear();
                if(dataSnapshot.exists()){
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    final String titulo = postSnapshot.child("titulo").getValue(String.class);
                    final String autor = postSnapshot.child("user").getValue(String.class);
                    nomesList.add(titulo);
                    autorList.add(autor);
                }

                adapter = new AdapterPesquisaEstrategia(getActivity(),nomesList,autorList);
                adapter.notifyDataSetChanged();
                recyclerProcura.setAdapter(adapter);

            }}

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Pesquisar Ideias");
    }

    @Override
    public void onResume() {
        super.onResume();

    }
    private class AsyncTaskRetro extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getContext());
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


