package com.example.biank.meuapp.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.biank.meuapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MostrarAplicacao extends AppCompatActivity {
    String id;
    TextView txtCont;
    TextView txtMetodo;
    TextView txtDisc;
    TextView txtPros;
    TextView txtContras;
    TextView txtData;
    TextView txtAutor;
    DatabaseReference referenciaFirebase;
    int cont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_aplicacao);
        setTitle("Ver Aplicação");
        txtCont = findViewById(R.id.retornaCont);
        txtMetodo = findViewById(R.id.retornaMetodo);
        txtDisc = findViewById(R.id.retornaDisc);
        txtPros = findViewById(R.id.retornaPros);
        txtContras = findViewById(R.id.retornaContras);
        txtData = findViewById(R.id.retornaData);
        txtAutor = findViewById(R.id.retornaAutor);

        referenciaFirebase = FirebaseDatabase.getInstance().getReference();

        Bundle mBundle = getIntent().getExtras();

        if(mBundle != null){

            id = mBundle.getString("id");
            cont = mBundle.getInt("pos");

        }
        listaValores(id);
    }
    private void listaValores(String valor) {

        referenciaFirebase.child("aplicacoes").orderByKey().equalTo(valor).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String metodo = null,disc = null,pros = null, contras = null, data = null, autor=null;


                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    metodo = postSnapshot.child("estrategia").getValue().toString();
                    disc = postSnapshot.child("disciplina").getValue().toString();
                    pros = postSnapshot.child("pros").getValue().toString();
                    contras = postSnapshot.child("contras").getValue().toString();
                    autor = postSnapshot.child("user").getValue().toString();
                    data = postSnapshot.child("data").getValue().toString();


                }
                cont+=1;
                txtCont.setText("Aplicação "+ cont);
                txtMetodo.setText(metodo);
                txtDisc.setText(disc);
                txtPros.setText(pros);
                txtContras.setText(contras);
                txtAutor.setText(autor);
                txtData.setText(data);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
