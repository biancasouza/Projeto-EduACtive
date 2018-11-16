package com.example.biank.meuapp.Classes;

import android.net.Uri;

import com.example.biank.meuapp.DAO.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class Metodologia {
    private String id;
    private String titulo;
    private String descricao;
    private String competencias;
    private String sequencia;
    private String user;
    private String arquivo;

    public Metodologia(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCompetencias() {
        return competencias;
    }

    public void setCompetencias(String competencias) {
        this.competencias = competencias;
    }

    public String getSequencia() {
        return sequencia;
    }

    public void setSequencia(String sequencia) {
        this.sequencia = sequencia;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    public void salvar() {
        DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFirebase();
        referenciaFirebase.child("metodologias").child(String.valueOf(getId())).setValue(this);
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> hashMapMetodologia = new HashMap<>();

        hashMapMetodologia.put("id", getId());
        hashMapMetodologia.put("titulo", getTitulo());
        hashMapMetodologia.put("descricao", getDescricao());
        hashMapMetodologia.put("competencias", getCompetencias());
        hashMapMetodologia.put("sequencia", getSequencia());
        hashMapMetodologia.put("user", getUser());
        hashMapMetodologia.put("arquivo", getArquivo());

        return hashMapMetodologia;

    }
}
