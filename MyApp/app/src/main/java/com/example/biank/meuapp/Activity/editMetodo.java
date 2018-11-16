package com.example.biank.meuapp.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.example.biank.meuapp.Classes.Metodologia;
import com.example.biank.meuapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;


public class editMetodo extends AppCompatActivity {

    private EditText titulo;
    private EditText descricao;
    private EditText competencias;
    private TextView status;
    private EditText sequencia;
    private BootstrapButton btnCadastar;
    private BootstrapButton btnCancelar;
    private BootstrapButton btnUpload;
    private FirebaseAuth autenticacao;
    private FirebaseDatabase database;
    private Metodologia metodologias;
    private FirebaseStorage storage;
    private Uri pdfUri;
   private  String url, user;

    ProgressDialog progressDialog;
    DatabaseReference referenciaFirebase;
    public static String  valor;
    String pdfantigo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_Dialog);
        setContentView(R.layout.activity_edit_metodo);

        referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        metodologias = new Metodologia();
        String email = autenticacao.getInstance().getCurrentUser().getEmail();
        listaValores(getValor());



        referenciaFirebase.child("usuarios").orderByChild("email").equalTo(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String nome = postSnapshot.child("nome_completo").getValue().toString();
                    user = nome;

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        status = (TextView) findViewById(R.id.txt_statusedit);
        titulo = (EditText) findViewById(R.id.txt_tituloedit);
        descricao = (EditText) findViewById(R.id.txt_descricaoedit);
        competencias= (EditText) findViewById(R.id.txt_competenciasedit);
        sequencia = (EditText) findViewById(R.id.txt_sequenciaedit);
        btnCadastar = (BootstrapButton) findViewById(R.id.btnCadastrometodoedit);
        btnCancelar = (BootstrapButton) findViewById(R.id.btnCancelametodoedit);
        btnUpload = (BootstrapButton) findViewById(R.id.btnUploadedit);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnCadastar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnCadastar.setClickable(false);
                if (!titulo.getText().toString().equals("") && !descricao.getText().toString().equals("")
                        && !competencias.getText().toString().equals("") && !sequencia.getText().toString().equals("")
                        ){
                    metodologias.setTitulo(titulo.getText().toString());
                    metodologias.setDescricao(descricao.getText().toString());
                    metodologias.setCompetencias(competencias.getText().toString());
                    metodologias.setSequencia(sequencia.getText().toString());
                    metodologias.setUser(user);


                    if(pdfUri!=null) {
                        uploadFile(pdfUri);

                    }
                    else{
                        metodologias.setArquivo(getPdfantigo());
                        edita(getValor());
                    }

                }
                else{
                    Toast.makeText(editMetodo.this ,"Preencha todos os campos para prosseguir!",Toast.LENGTH_LONG).show();
                }

            }

        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(editMetodo.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    selecionaArquivo();

                }
                else{
                    ActivityCompat.requestPermissions(editMetodo.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);
                }
            }
        });

    }

    private void uploadFile(Uri pdfUri) {

        progressDialog = new ProgressDialog(editMetodo.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading file...");
        progressDialog.setProgress(0);
        progressDialog.show();
        final String fileName = System.currentTimeMillis()+"";
        StorageReference storageReference = storage.getReference();
        storageReference.child("Uploads").child(fileName).putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        url = taskSnapshot.getDownloadUrl().toString();
                        metodologias.setArquivo(url);
                        edita(getValor());


                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(editMetodo.this,"Erro ao enviar arquivo!",Toast.LENGTH_LONG).show();


            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==9 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            selecionaArquivo();
        }
        else {
            Toast.makeText(editMetodo.this,"Por favor, conceda a permissão", Toast.LENGTH_SHORT).show();
        }
    }

    private void selecionaArquivo() {
        Intent i = new Intent();
        i.setType("application/pdf");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,86);

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==86 && resultCode == RESULT_OK && data!=null){
            pdfUri=data.getData();
            status.setText(pdfUri.getLastPathSegment());


        }
        else {
            Toast.makeText(editMetodo.this,"Por favor selecione um arquivo", Toast.LENGTH_SHORT).show();
        }
    }

    private void listaValores(String valor) {

        referenciaFirebase.child("metodologias").orderByChild("titulo").equalTo(valor).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String titulos = null,descricaos = null,sequencias = null,competencia = null, pdfs = null;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    titulos = postSnapshot.child("titulo").getValue().toString();
                    descricaos = postSnapshot.child("descricao").getValue().toString();
                    sequencias = postSnapshot.child("sequencia").getValue().toString();
                    competencia = postSnapshot.child("competencias").getValue().toString();
                    pdfs = postSnapshot.child("arquivo").getValue().toString();

                }
                setPdfantigo(pdfs);
                titulo.setText(titulos);
                descricao.setText(descricaos);
                sequencia.setText(sequencias);
                competencias.setText(competencia);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void edita(String s){
        referenciaFirebase.child("metodologias").orderByChild("titulo").equalTo(s).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    postSnapshot.getRef().setValue(metodologias);

                }
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    public static void setValor(String valor) {
        editMetodo.valor = valor;
    }

    public static String getValor() {
        return valor;
    }

    public String getPdfantigo() {
        return pdfantigo;
    }

    public void setPdfantigo(String pdfantigo) {
        this.pdfantigo = pdfantigo;
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnCadastar.setEnabled(true);
    }
}
