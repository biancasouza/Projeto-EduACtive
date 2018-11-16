package com.example.biank.meuapp.Activity;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.RequestOptions;
import com.example.biank.meuapp.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.biank.meuapp.Classes.MyAppGlideModule;

import de.hdodenhof.circleimageview.CircleImageView;

//import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class PrincipalActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private FirebaseAuth autenticacao;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    NavigationView navigationView;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_principal);
        if(!verificaConexao()){
            Toast.makeText(this,"Conexão perdida!",Toast.LENGTH_LONG).show();
        }
        autenticacao= FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

         navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        this.getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        android.support.v4.app.Fragment current =getCurrentFragment();
                        if (current instanceof Inicial) {
                            navigationView.setCheckedItem(R.id.nav_initial);
                        } else  if (current instanceof CadastroMetodologia) {
                            navigationView.setCheckedItem(R.id.nav_segunda);
                        }else  if (current instanceof PesquisarEstrategia) {
                            navigationView.setCheckedItem(R.id.nav_pesquisaestrategia);
                        }else  if (current instanceof CadastrarAplicacao) {
                            navigationView.setCheckedItem(R.id.nav_cadaplicacao);
                        }
                        else{
                            navigationView.setCheckedItem(R.id.nav_help);
                        }
                    }
                });

        final TextView txtnome = (TextView) headerView.findViewById(R.id.textViewNome);
        TextView txtemail = (TextView) headerView.findViewById(R.id.textViewEmail);
        final CircleImageView imagem = (CircleImageView) headerView.findViewById(R.id.imageView);


        FirebaseAuth autenticacao;
        autenticacao = FirebaseAuth.getInstance();
        String email = autenticacao.getCurrentUser().getEmail();

        DatabaseReference referenciaFirebase;
        referenciaFirebase = FirebaseDatabase.getInstance().getReference();


        referenciaFirebase.child("usuarios").orderByChild("email").equalTo(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String nome = postSnapshot.child("nome_completo").getValue().toString();
                    String urlImage = postSnapshot.child("perfil").getValue().toString();
                    txtnome.setText(nome);
                    if(!urlImage.equals("")){
                    storageReference = storage.getReferenceFromUrl(urlImage);


                    /**/
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                               @Override
                                                                               public void onSuccess(Uri uri) {
                                                                                   Glide.with(PrincipalActivity.this)
                                                                                           .using(new FirebaseImageLoader())
                                                                                                   .load(storageReference)
                                                                                           .override(150,150)
                                                                                                   .centerCrop()
                                                                                           .into(imagem);

                                                                               }
                                                                           }
                    );}

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        txtemail.setText(email);
        //navigationView.setCheckedItem(R.id.nav_initial);
        displaySelectedScreen(R.id.nav_initial);


    }


    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                getSupportFragmentManager().popBackStack();



            } else {

                moveTaskToBack(true);
            }

        }

   }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Intent intent = new Intent(PrincipalActivity.this, MainActivity.class);
       // startActivity(intent);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {

        displaySelectedScreen(item.getItemId());
        return true;

    }
    private void logoutUser(){
        autenticacao.signOut();
        finish();


    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    private void displaySelectedScreen(final int id){
        android.support.v4.app.Fragment fm = null;

        switch (id){
            case R.id.nav_initial:

                fm = new Inicial();

                break;
            case R.id.nav_segunda:
                fm = new CadastroMetodologia();
                break;
            case R.id.nav_pesquisaestrategia:
                fm = new PesquisarEstrategia();


                break;
            case R.id.nav_cadaplicacao:
                fm = new CadastrarAplicacao();

                break;
            case R.id.nav_help:
                fm = new help();

                break;
            case R.id.sair:
                logoutUser();
                break;
        }
       if(fm != null) {

           android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
           ft.replace(R.id.content_frame, fm).addToBackStack(null);
           ft.commit();


       }

       DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START); }
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


    }
    public android.support.v4.app.Fragment getCurrentFragment() {
        return this.getSupportFragmentManager().findFragmentById(R.id.content_frame);
    }
}


