package com.example.biank.meuapp.Activity;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.biank.meuapp.Classes.AdapterPesquisaHome;
import com.example.biank.meuapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Inicial extends Fragment {

    RecyclerView recyclerProcura;
    BootstrapEditText editTextSearch;
    final ArrayList<String> nomesList = new ArrayList<String>();
    final ArrayList<String> dataList = new ArrayList<String>();
    DatabaseReference referenciaFirebase;
    AdapterPesquisaHome adapter;
    String email;
    static String autor ;
    BootstrapButton edit;
    TextView apaga;
    ArrayList<String> filterdNames;

    private Handler handler = new Handler();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.activity_inicial, container, false);
        recyclerProcura = (RecyclerView) v.findViewById(R.id.rvlistaestrategiahome);
        editTextSearch = (BootstrapEditText) v.findViewById(R.id.edtProcurarhome);
        referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        apaga=(TextView)v.findViewById(R.id.naoCadastra);
        final FirebaseAuth autenticacao;
        autenticacao = FirebaseAuth.getInstance();
        email = autenticacao.getCurrentUser().getEmail();

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
            }}
        });
        referenciaFirebase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                apagaEstrategia(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return v;
    }



    private void filter(String text) {

        filterdNames = new ArrayList<>();

        for (String s : nomesList) {
            //if the existing elements contains the search input
            if (s.toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        adapter.filterList(filterdNames);
    }

    private void Preenche(String autor) {


        referenciaFirebase.child("metodologias").orderByChild("user").equalTo(autor).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               nomesList.clear();
                if (dataSnapshot.exists()){

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    final String titulo = postSnapshot.child("titulo").getValue(String.class);
                    //final String data = postSnapshot.child("data").getValue(String.class);
                    nomesList.add(titulo);
                    //dataList.add(data);
                }
                    adapter = new AdapterPesquisaHome(getActivity(),nomesList);
                    adapter.notifyDataSetChanged();
                    recyclerProcura.setAdapter(adapter);
                }
                else{
                    editTextSearch.setEnabled(false);
                   apaga.setText("Você ainda não cadastrou nenhuma estratégia!");
                   apaga.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    private void apagaEstrategia(DataSnapshot dataSnapshot){
    referenciaFirebase.child("metodologias").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singlesnapchat : dataSnapshot.getChildren()){

                    String title = singlesnapchat.getValue(String.class);

                    for (int i = 0; i<nomesList.size();i++) {

                        if(nomesList.get(i).equals(title)){
                            nomesList.remove(i);
                        }
                        adapter = new AdapterPesquisaHome(getActivity(),nomesList);
                        adapter.notifyDataSetChanged();
                        recyclerProcura.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

       }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        getActivity().setTitle("Suas Estratégias");
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    @Override
    public void onResume() {
        super.onResume();
        //progressDialog.dismiss();

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
            referenciaFirebase.child("usuarios").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        String nome = postSnapshot.child("nome_completo").getValue().toString();

                        Preenche(nome);

                    }

                progressDialog.dismiss();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {

        }

    }




}






