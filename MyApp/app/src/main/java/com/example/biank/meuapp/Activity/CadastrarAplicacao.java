package com.example.biank.meuapp.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.biank.meuapp.Classes.Aplicacao;
import com.example.biank.meuapp.Classes.SearchEstrategia;
import com.example.biank.meuapp.DAO.ConfiguracaoFirebase;
import com.example.biank.meuapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CadastrarAplicacao extends android.support.v4.app.Fragment {

    private Spinner disciplina;

    public static EditText getEstrategia() {
        return estrategia;
    }

    public static void setEstrategia(BootstrapEditText  estrategia) {
        CadastrarAplicacao.estrategia = estrategia;
    }

    private static BootstrapEditText estrategia;
    private BootstrapEditText  pros;
    private BootstrapEditText  contras;
    private BootstrapButton btnCadastar;
    private FirebaseAuth autenticacao;
    private FirebaseDatabase database;
    private Aplicacao aplicacao;
    private String user;
    private RecyclerView busca;
    ArrayList<String>tituloList;
    ArrayList<String>user_cadastroList;
    SearchEstrategia searchEstrategia;


    DatabaseReference referenciaFirebase;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.activity_cadastrar_aplicacao, container, false);

        estrategia = (BootstrapEditText ) v.findViewById(R.id.selectmetodo);
        disciplina = (Spinner) v.findViewById(R.id.spinnerDisc);
        busca = (RecyclerView) v.findViewById(R.id.buscametodo);
        pros = (BootstrapEditText ) v.findViewById(R.id.edtpros);
        contras = (BootstrapEditText ) v.findViewById(R.id.edtContras);
        btnCadastar = (BootstrapButton) v.findViewById(R.id.buttonCadApp);


        referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        ListarDisciplinas();
        busca.setHasFixedSize(true);
        busca.setLayoutManager(new LinearLayoutManager(getActivity()));
        busca.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        tituloList = new ArrayList<>();
        user_cadastroList = new ArrayList<>();

        estrategia.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!s.toString().isEmpty()) {
                    setAdapter(s.toString());
                } else {
                    user_cadastroList.clear();
                    tituloList.clear();
                    busca.removeAllViews();



            }}
        });
        String email = autenticacao.getInstance().getCurrentUser().getEmail();

        referenciaFirebase.child("usuarios").orderByChild("email").equalTo(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String nome = postSnapshot.child("nome_completo").getValue().toString();
                    user = nome;

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        btnCadastar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnCadastar.setEnabled(false);
                if (!estrategia.getText().toString().equals("") && !disciplina.getSelectedItem().toString().equals("")
                        && !pros.getText().toString().equals("") &&
                        !disciplina.getSelectedItem().toString().equals("SELECIONE")) {
                    aplicacao = new Aplicacao();
                    aplicacao.setEstrategia(estrategia.getText().toString());
                    aplicacao.setDisciplina(disciplina.getSelectedItem().toString());
                    aplicacao.setPros(pros.getText().toString());
                    aplicacao.setContras(contras.getText().toString());
                    aplicacao.setUser(user);
                    aplicacao.setData(pegaData());

                    cadastrarAplicacao();
                } else {
                    Toast.makeText(getActivity(), "Preencha todos os campos para prosseguir!", Toast.LENGTH_LONG).show();
                }

            }

        });


        return v;

    }


    private void setAdapter(final String procura) {

        referenciaFirebase.child("metodologias").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user_cadastroList.clear();
                tituloList.clear();
                busca.removeAllViews();
                int counter =0;

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    final String titulo = postSnapshot.child("titulo").getValue(String.class);
                    String user_cadastro = postSnapshot.child("user").getValue(String.class);

                    if(titulo.toLowerCase().contains(procura.toLowerCase())){
                        if (titulo.toLowerCase().equals(procura.toLowerCase())){
                            user_cadastroList.clear();
                            tituloList.clear();

                        }
                        else{
                            tituloList.add(titulo);
                            user_cadastroList.add(user_cadastro);
                            busca.removeAllViews();
                        }


                        counter++;
                    }
                    if(counter==15){
                        break;
                    }

                    searchEstrategia=new SearchEstrategia(getActivity(),tituloList,user_cadastroList);
                    busca.setAdapter(searchEstrategia);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void ListarDisciplinas(){
        referenciaFirebase.child("disciplinas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> disciplinasList = new ArrayList<String>();
                disciplinasList.add("SELECIONE");
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String discName = postSnapshot.child("disc").getValue().toString();

                    disciplinasList.add(discName);
                }

                if (getActivity() != null) {
                    ArrayAdapter<String> discAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, disciplinasList);
                    discAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    disciplina.setAdapter(discAdapter);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
    private void cadastrarAplicacao(){
        Query q= referenciaFirebase.child("metodologias").orderByChild("titulo").equalTo(estrategia.getText().toString());
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    Toast.makeText(getActivity(),"Estratégia não encontrada!",Toast.LENGTH_LONG).show();

                }
                else{

                    insereAplicacao();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        btnCadastar.setEnabled(true);
    }

    private boolean insereAplicacao(){
        try{
            referenciaFirebase = ConfiguracaoFirebase.getFirebase().child("aplicacoes");
            referenciaFirebase.push().setValue(aplicacao);
            Toast.makeText(getActivity(),"Aplicação cadastrado com sucesso!",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), PrincipalActivity.class);
            startActivity(intent);
            return true;
        }catch (Exception e){
            Toast.makeText(getActivity(),"Erro ao cadastrar! Verifique sua conexão",Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return false;

        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Registrar Feedback");


    }
    public String pegaData(){
        SimpleDateFormat formataData = new SimpleDateFormat("dd-MM-yyyy");
        Date data = new Date();
        String dataFormatada = formataData.format(data);
        return  dataFormatada;

    }


}
