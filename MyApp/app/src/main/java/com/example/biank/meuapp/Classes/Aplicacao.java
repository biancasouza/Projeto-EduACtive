package com.example.biank.meuapp.Classes;

import com.example.biank.meuapp.DAO.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Aplicacao {
    private String id;
    private String estrategia;
    private String disciplina;
    private String pros;
    private String contras;
    private String user;
    private String data;


    public Aplicacao(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEstrategia() {
        return estrategia;
    }

    public void setEstrategia(String estrategia) {
        this.estrategia = estrategia;
    }

    public String getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(String disciplina) {
        this.disciplina = disciplina;
    }

    public String getUser() {
        return user;
    }

    public String getPros() {
        return pros;
    }

    public void setPros(String pros) {
        this.pros = pros;
    }

    public String getContras() {
        return contras;
    }

    public void setContras(String contras) {
        this.contras = contras;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void salvar() {
        DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFirebase();
        referenciaFirebase.child("aplicacoes").child(String.valueOf(getId())).setValue(this);
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> hashMapAplicacao = new HashMap<>();

        hashMapAplicacao.put("id", getId());
        hashMapAplicacao.put("estrategia", getEstrategia());
        hashMapAplicacao.put("disciplina", getDisciplina());
        hashMapAplicacao.put("user", getUser());
        hashMapAplicacao.put("pros",getPros());
        hashMapAplicacao.put("contras",getContras());
        hashMapAplicacao.put("data",getData());


        return hashMapAplicacao;

    }
}
