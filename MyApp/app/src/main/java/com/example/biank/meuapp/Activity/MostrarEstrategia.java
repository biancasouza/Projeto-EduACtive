package com.example.biank.meuapp.Activity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.example.biank.meuapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MostrarEstrategia extends AppCompatActivity{
    public static String valor;
    TextView txttitulo;
    TextView txtDesc;
    TextView txtSequencia;
    TextView txtComp;
    TextView qtd;
    TextView txtPdf;
    DatabaseReference referenciaFirebase;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    ProgressDialog progressDialog;
    private DownloadManager downloadManeger;

    @Nullable


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Mostrar Estratégia");
        setContentView(R.layout.activity_mostrar_estrategia);
        txttitulo = findViewById(R.id.retornaTitulo);
        txtDesc = findViewById(R.id.retornaDesc);
        txtSequencia = findViewById(R.id.retornaSequencia);
        txtComp = findViewById(R.id.retornaCompetencia);
        qtd = findViewById(R.id.qtd_av);
        txtPdf = findViewById(R.id.txtPdf);
        referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        listaValores(getValor());

        Mostraqtd(getValor());


        qtd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MostrarEstrategia.this,ListarAplicacoes.class);
                i.putExtra("titulo", txttitulo.getText().toString());
                startActivity(i);

            }
        });

    }

    private void listaValores(String valor) {

        referenciaFirebase.child("metodologias").orderByChild("titulo").equalTo(valor).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String titulo = null,descricao = null,sequencia = null,competencia = null, pdf = null;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    titulo = postSnapshot.child("titulo").getValue().toString();
                    descricao = postSnapshot.child("descricao").getValue().toString();
                    sequencia = postSnapshot.child("sequencia").getValue().toString();
                    competencia = postSnapshot.child("competencias").getValue().toString();
                    pdf = postSnapshot.child("arquivo").getValue().toString();

                }


                txttitulo.setText(titulo);
                txtDesc.setText(descricao);
                txtSequencia.setText(sequencia);
                txtComp.setText(competencia);

                if(!pdf.equals("")){
                txtPdf.setTextColor(Color.rgb(30,144,255));
                txtPdf.setText("pdf");
                    final String finalPdf = pdf;
                    txtPdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                downloadFile(finalPdf);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                }
                else{
                    txtPdf.setEnabled(false);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void downloadFile(String url) throws IOException {

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Downloading file...");
        progressDialog.setProgress(0);
        progressDialog.show();
        downloadManeger = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);


        Uri uri = Uri.parse(url);

        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        final long id = downloadManeger.enqueue(request);
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                DownloadManager.Query q = new DownloadManager.Query();
                q.setFilterById(id);
                Cursor cursor = downloadManeger.query(q);
                cursor.moveToFirst();
                int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                cursor.close();
                final int dl_progress = (bytes_downloaded * 100 / bytes_total);
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        progressDialog.setProgress(dl_progress);
                    }
                });

            }

        }, 0, 10);

    }

    private void Mostraqtd(String valor) {

        referenciaFirebase.child("aplicacoes").orderByChild("estrategia").equalTo(valor).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int cont =0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot.exists()) {
                        cont++;

                    }
                }
                if(cont>0){
                    if(cont==1){
                        qtd.setText(cont+" aplicação");
                    }
                    else{
                        qtd.setText(cont+" aplicações");
                    }

                qtd.setTextColor(Color.rgb(30,144,255));}

                else{
                    qtd.setText("Sem feeddbacks registrados");
                    qtd.setEnabled(false);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void setValor(String valor) {
        MostrarEstrategia.valor = valor;
    }

    public static String getValor() {
        return valor;
    }


}
