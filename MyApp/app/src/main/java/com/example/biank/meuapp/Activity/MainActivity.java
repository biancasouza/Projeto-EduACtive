package com.example.biank.meuapp.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth autenticacao;
    private BootstrapEditText edtEmailLogin;
    private BootstrapEditText edtSenhaLogin;
    private TextView txt_Cadastrar;
    private TextView txt_ResgatarSenha;
    private BootstrapButton btnLogin;
    private Usuario usuario;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!verificaConexao()){
            Toast.makeText(this,"Por favor, verifique sua conexão", Toast.LENGTH_LONG).show();
        }
        edtEmailLogin = (BootstrapEditText)findViewById(R.id.edtEmail);
        edtSenhaLogin = (BootstrapEditText)findViewById(R.id.edtSenha);
        btnLogin = (BootstrapButton) findViewById(R.id.btnLogin);
        txt_Cadastrar = (TextView) findViewById(R.id.txt_Cadastrar);
        txt_ResgatarSenha = (TextView) findViewById(R.id.txt_RegastarSenha);

        progressDialog = new ProgressDialog(MainActivity.this );
        progressDialog.setMessage("Validando dados...");
        progressDialog.setCancelable(false);

        if(usuarioLogado()){
            Intent intentMinhaConta = new Intent(MainActivity.this, PrincipalActivity.class);
            startActivity(intentMinhaConta);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                btnLogin.setEnabled(false);
                if(!edtEmailLogin.getText().toString().equals("") && !edtSenhaLogin.getText().toString().equals("")){

                    progressDialog.show();
                    usuario = new Usuario();
                    usuario.setEmail(edtEmailLogin.getText().toString());
                    usuario.setSenha(edtSenhaLogin.getText().toString());
                    validarLogin();


                }else {
                    progressDialog.dismiss();
                    if(edtEmailLogin.getText().toString().equals("") && edtSenhaLogin.getText().toString().equals("")){
                        Toast.makeText(MainActivity.this, "Por favor, preencha seu login e senha!", Toast.LENGTH_LONG).show();
                    }
                    else if(!edtEmailLogin.getText().toString().equals("") && edtSenhaLogin.getText().toString().equals("")){
                        Toast.makeText(MainActivity.this, "Por favor, preencha sua senha!", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Por favor, preencha seu login!", Toast.LENGTH_LONG).show();
                    }
                }
            }

        });
        txt_Cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CadastroUsuarioActivity.class);
                startActivity(intent);
            }

        });

        txt_ResgatarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ResetarSenha.class);
                startActivity(intent);
            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        edtSenhaLogin.setText("");
        edtEmailLogin.setText("");
        progressDialog.dismiss();
        btnLogin.setEnabled(true);
    }

    private void validarLogin(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.signInWithEmailAndPassword(usuario.getEmail().toString(),usuario.getSenha().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    logar();
                    Toast.makeText(MainActivity.this, "Login efetuado com sucesso!", Toast.LENGTH_LONG).show();

                }else{
                    progressDialog.dismiss();
                    btnLogin.setEnabled(true);
                    Toast.makeText(MainActivity.this, "Usuário/Senha inválido(s)!", Toast.LENGTH_LONG).show();
                }
            }
        });

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
    private void logar(){
        Intent intent = new Intent(MainActivity.this, PrincipalActivity.class);
        startActivity(intent);
    }
    public Boolean usuarioLogado(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            return true;
        }
        else{
            return false;
        }
    }
}
