package com.example.floatingwindow.model;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import com.example.floatingwindow.Tradutor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

@Entity
public class Traducao {

    String traducao;
    @NonNull    //faz com que ela não possa ser nula
    @PrimaryKey //define quem é a primarykey
    final String ingles;

    int frequencia;

    public Traducao(String ingles, String traducao, int frequencia) {
        this.ingles = ingles;
        this.traducao = traducao;


        this.frequencia = frequencia;
    }

    public String getIngles(){
        return ingles;
    }
    public String getTraducao(){
        return traducao;
    }
    public int getFrequencia() {
        return frequencia;
    }

    public void aumentaFreq(int i){
        frequencia=i+1;
    }

    @NonNull
    @Override
    public String toString() {
        return ingles;
    }

    public String getInformacoes() {
        return ("Original: " + ingles + "\nTradução: " + traducao + "\nTotal De Pesquisas = " + frequencia);
    }
}