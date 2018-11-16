package com.example.biank.meuapp.DAO;

import android.content.Context;
import android.net.ConnectivityManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by biank on 02/03/2018.
 */

public class ConfiguracaoFirebase {
    private static DatabaseReference referenciaFirebase;
    private static FirebaseAuth autenticacao;

    public static DatabaseReference getFirebase(){
        if(referenciaFirebase==null){
            referenciaFirebase= FirebaseDatabase.getInstance().getReference();
        }
        return referenciaFirebase;
    }

    public static FirebaseAuth getFirebaseAuth(){
        if(autenticacao==null){
            autenticacao=FirebaseAuth.getInstance();
        }
        return  autenticacao;
    }

}
