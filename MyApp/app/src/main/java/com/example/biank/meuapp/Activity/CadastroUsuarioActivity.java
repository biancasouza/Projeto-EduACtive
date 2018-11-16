package com.example.biank.meuapp.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.biank.meuapp.Classes.Usuario;
import com.example.biank.meuapp.DAO.ConfiguracaoFirebase;
import com.example.biank.meuapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private TextView status;
    private BootstrapButton btnUpload;
    private FirebaseStorage storage;
    private Uri imgUri;
    ProgressDialog progressDialog;
    String url;
    private BootstrapEditText nome;
    private BootstrapEditText formacao;
    private BootstrapEditText email;
    private BootstrapEditText senha;
    private BootstrapEditText senha2;
    private BootstrapButton btnCadastar;
    private CircleImageView imagem;
    private FirebaseAuth autenticacao;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private Usuario usuario;
    DatabaseReference referenciaFirebase;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);
        if(!verificaConexao()){
            Toast.makeText(this,"Por favor, verifique sua conexão", Toast.LENGTH_LONG).show();
        }
        referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        usuario = new Usuario();
        imagem = (CircleImageView) findViewById(R.id.imperfil);
        nome = (BootstrapEditText) findViewById(R.id.cadNome);
        formacao = (BootstrapEditText) findViewById(R.id.cadFormacao);
        email = (BootstrapEditText) findViewById(R.id.cadEmail);
        senha = (BootstrapEditText) findViewById(R.id.cadSenha);
        senha2 = (BootstrapEditText) findViewById(R.id.cadSenha2);
        btnCadastar = (BootstrapButton) findViewById(R.id.btnCadastrar);

        status = (TextView) findViewById(R.id.txt_statusPerfil);
        btnUpload = (BootstrapButton) findViewById(R.id.btnUploadPerfil);

        btnCadastar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnCadastar.setEnabled(false);
                if (!nome.getText().toString().equals("") && !formacao.getText().toString().equals("")
                        && !email.getText().toString().equals("") && !senha.getText().toString().equals("")
                        && !senha2.getText().toString().equals("") ){
                    if (senha.getText().toString().equals(senha2.getText().toString())){

                        usuario.setNome_completo(nome.getText().toString());
                        usuario.setFormacao(formacao.getText().toString());
                        usuario.setEmail(email.getText().toString());
                        usuario.setSenha(senha.getText().toString());

                        if(imgUri!=null) {

                            uploadImg(imgUri);

                        }
                        else {
                            usuario.setPerfil("");
                            cadastrarUsuario();
                        }
                    }
                    else{
                        Toast.makeText(CadastroUsuarioActivity.this,"As senhas não correspondem",Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(CadastroUsuarioActivity.this,"Preencha todos os campos para prosseguir!",Toast.LENGTH_LONG).show();
                }

            }

        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(CadastroUsuarioActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    selecionaArquivo();

                }
                else{
                    ActivityCompat.requestPermissions(CadastroUsuarioActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
//        progressDialog.dismiss();
        btnCadastar.setEnabled(true);
    }
    private void uploadImg(Uri imgUri) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Gravando dados...");
        progressDialog.setProgress(0);
        progressDialog.show();
        final String fileName = System.currentTimeMillis()+"";
        StorageReference storageReference = storage.getReference();
        storageReference.child("Perfil").child(fileName).putFile(imgUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        url = taskSnapshot.getDownloadUrl().toString();
                        usuario.setPerfil(url);
                        cadastrarUsuario();

                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CadastroUsuarioActivity.this,"Erro ao enviar arquivo!",Toast.LENGTH_LONG).show();
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
        if(requestCode==9 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            selecionaArquivo();
        }
        else {
            Toast.makeText(CadastroUsuarioActivity.this,"Por favor, conceda a permissão", Toast.LENGTH_SHORT).show();
        }
    }

    private void selecionaArquivo() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(i,86);

    }

    //@RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==86 && resultCode == RESULT_OK && data!=null){
            imgUri=data.getData();
            status.setText(imgUri.getLastPathSegment());
            String colunas[] = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            cursor = getContentResolver().query(imgUri, colunas, null,null,null);

            cursor.moveToFirst();

            int indexColuna = cursor.getColumnIndex(colunas[0]);
            String pathimage = cursor.getString(indexColuna);
            cursor.close();

            Bitmap bitmap = BitmapFactory.decodeFile(pathimage);
            imagem.setImageBitmap(bitmap);

        
        }
        else {
            Toast.makeText(CadastroUsuarioActivity.this,"Por favor selecione um arquivo", Toast.LENGTH_SHORT).show();
        }
    }
    private void cadastrarUsuario (){

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.createUserWithEmailAndPassword(
          usuario.getEmail(),
          usuario.getSenha()
        ).addOnCompleteListener(CadastroUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    insereUsuario(usuario);
                    finish();

                } else{

                    String erroExcecao = "";

                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        erroExcecao = "Digite uma senha que contenha no mínimo 6 caracteres";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        erroExcecao = "E-mail inválido!";
                    }catch (FirebaseAuthUserCollisionException e){
                        erroExcecao = "E-mail já cadastrado!";
                    } catch (Exception e) {
                        erroExcecao = "Erro ao efetuar o cadastro! Verifique sua conexão";
                        e.printStackTrace();
                    }
                    Toast.makeText(CadastroUsuarioActivity.this,erroExcecao,Toast.LENGTH_LONG).show();
                    btnCadastar.setEnabled(true);
                }
            }
        });
    }
    private boolean insereUsuario (Usuario usuario){
        try{
            reference = ConfiguracaoFirebase.getFirebase().child("usuarios");
            reference.push().setValue(usuario);
            Toast.makeText(CadastroUsuarioActivity.this,"Usuário cadastrado com sucesso!",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(CadastroUsuarioActivity.this, PrincipalActivity.class);
            startActivity(intent);
            return true;
        }catch (Exception e){
            Toast.makeText(CadastroUsuarioActivity.this,"Erro ao cadastrar usuário!",Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return false;
        }
    }
    public  boolean verificaConexao() {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }

}
