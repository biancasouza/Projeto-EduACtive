package com.example.biank.meuapp.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.biank.meuapp.Classes.Metodologia;
import com.example.biank.meuapp.DAO.ConfiguracaoFirebase;
import com.example.biank.meuapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.PolicySpi;

import static android.app.Activity.RESULT_OK;

public class CadastroMetodologia extends android.support.v4.app.Fragment {

    private BootstrapEditText titulo;
    private BootstrapEditText descricao;
    private BootstrapEditText competencias;
    private TextView status;
    private BootstrapEditText sequencia;
    private BootstrapButton btnCadastar;
    private BootstrapButton btnCancelar;
    private BootstrapButton btnUpload;
    private FirebaseAuth autenticacao;
    private FirebaseDatabase database;
    private Metodologia metodologias;
    private String user;
    private FirebaseStorage storage;
    private Uri pdfUri;
    private String endereco;
    ProgressDialog progressDialog;
    String url;


    DatabaseReference referenciaFirebase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_cadmetodo,container,false);

        referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        metodologias = new Metodologia();
        String email = autenticacao.getInstance().getCurrentUser().getEmail();

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

        status = (TextView) v.findViewById(R.id.txt_status);
        titulo = (BootstrapEditText) v.findViewById(R.id.txt_titulo);
        descricao = (BootstrapEditText) v.findViewById(R.id.txt_descricao);
        competencias= (BootstrapEditText) v.findViewById(R.id.txt_competencias);
        sequencia = (BootstrapEditText) v.findViewById(R.id.txt_sequencia);
        btnCadastar = (BootstrapButton) v.findViewById(R.id.btnCadastrometodo);

        btnUpload = (BootstrapButton) v.findViewById(R.id.btnUpload);


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
                        metodologias.setArquivo("");
                        cadastrarMetodologia();
                    }









                }
                else{
                    Toast.makeText(getActivity(),"Preencha todos os campos para prosseguir!",Toast.LENGTH_LONG).show();
                }

            }

        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    selecionaArquivo();

                }
                else{
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);
                }
            }
        });
        return v;
    }

    private void uploadFile(Uri pdfUri) {

        progressDialog = new ProgressDialog(getActivity());
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
                cadastrarMetodologia();

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),"Erro ao enviar arquivo!",Toast.LENGTH_LONG).show();
                btnCadastar.setEnabled(true);

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
            Toast.makeText(getActivity(),"Por favor, conceda a permissão", Toast.LENGTH_SHORT).show();
        }
    }

    private void selecionaArquivo() {
        Intent i = new Intent();
        i.setType("application/pdf");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,86);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==86 && resultCode == RESULT_OK && data!=null){
            pdfUri=data.getData();
            status.setText(pdfUri.getLastPathSegment());

        }
        else {
            Toast.makeText(getActivity(),"Por favor selecione um arquivo", Toast.LENGTH_SHORT).show();
        }
    }

    private void cadastrarMetodologia (){
       Query q= referenciaFirebase.child("metodologias").orderByChild("titulo").equalTo(titulo.getText().toString());
       q.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               if (dataSnapshot.exists()){
                   Toast.makeText(getActivity(),"Esta estratégia já foi cadastrada no sistema!",Toast.LENGTH_LONG).show();
                   btnCadastar.setEnabled(true);
                   getActivity().finish();

               }
               else{
                   insereMetodologia();
               }
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });

      // insereMetodologia();


    }
    private boolean insereMetodologia(){
        try{
            referenciaFirebase = ConfiguracaoFirebase.getFirebase().child("metodologias");
            referenciaFirebase.push().setValue(metodologias);
            Toast.makeText(getActivity(),"Estratégia cadastrado com sucesso!",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), PrincipalActivity.class);
            startActivity(intent);
            return true;
        }catch (Exception e){
            Toast.makeText(getActivity(),"Erro ao cadastrar! Verifique sua conexão",Toast.LENGTH_LONG).show();
            e.printStackTrace();
            btnCadastar.setEnabled(true);
            return false;

        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Compartilhar ideia");
    }

    @Override
    public void onResume() {
        super.onResume();
        btnCadastar.setEnabled(true);
    }
}
