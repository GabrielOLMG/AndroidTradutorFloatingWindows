package com.example.floatingwindow;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

public class Tradutor {
    public FirebaseTranslatorOptions options;
    public FirebaseTranslator translator;
    public FirebaseModelDownloadConditions conditions;
    int sinal;
    public Tradutor(){
        options = new FirebaseTranslatorOptions.Builder()
                //from language
                .setSourceLanguage(FirebaseTranslateLanguage.EN)
                // to language
                .setTargetLanguage(FirebaseTranslateLanguage.PT)
                .build();

        translator = FirebaseNaturalLanguage.getInstance()
                .getTranslator(options);

        conditions = new FirebaseModelDownloadConditions.Builder()
                .build();

    }

    public String preparandoParaDownload(String ingles){
        String[] traducao = new String[0];
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                traducao[0] = "traduzir(ingles)";
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        traducao[0] = "erro";
                    }
                });
        return traducao[0];
    }

    private String traduzir(String ingles) {
        String[] traducao = new String[0];
        translator.translate(ingles)
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        traducao[0] = s;
                    }
                });
        return traducao[0];
    }

}
