package com.example.biank.meuapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.biank.meuapp.DAO.ConfiguracaoFirebase;
import com.example.biank.meuapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class ResetarSenha extends AppCompatActivity {

    private BootstrapEditText edtEmail;
    private BootstrapButton btnRecSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetar_senha);
        if(!verificaConexao()){
            Toast.makeText(this,"Por favor, verifique sua conexão", Toast.LENGTH_LONG).show();
        }

        edtEmail = (BootstrapEditText) findViewById(R.id.edtEmailRec);
        btnRecSenha = (BootstrapButton) findViewById(R.id.btnRecSenha);

        btnRecSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnRecSenha.setEnabled(false);
                String emailRec = edtEmail.getText().toString();
                if (emailRec.equals("")){
                    Toast.makeText(ResetarSenha.this,"Por favor, digite seu email",Toast.LENGTH_LONG).show();
                }
                else{
                    resetarSenha(emailRec);
                }
            }
        });}

        private void resetarSenha (String email){

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        autenticacao.sendPasswordResetEmail(email).addOnCompleteListener(ResetarSenha.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ResetarSenha.this,"Um email de recuperação foi enviado para a sua conta",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ResetarSenha.this, MainActivity.class);
                            startActivity(intent);
                        } else{
                            String erroExcecao = "";

                            try{
                                throw task.getException();
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                erroExcecao = "E-mail inválido!";
                            }
                            catch (Exception e) {
                                erroExcecao = "Não foi possível recuperar sua conta!!";
                                e.printStackTrace();
                            }
                            Toast.makeText(ResetarSenha.this,erroExcecao,Toast.LENGTH_LONG).show();
                            btnRecSenha.setEnabled(true);

                        }
                    }
                }
        );

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
    @Override
    protected void onResume() {
        super.onResume();
        btnRecSenha.setEnabled(true);
    }

}
