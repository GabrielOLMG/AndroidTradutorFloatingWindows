package com.example.floatingwindow;

import android.app.AlertDialog;
import androidx.room.Room;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.floatingwindow.Database.FloatingDatabase;
import com.example.floatingwindow.Database.dao.RoomTraducao;
import com.example.floatingwindow.model.Traducao;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{ //usando implements View.OnClickListener, vc faz pode criar uma unica função OnClick para agrupar todos os comportamentos d click
    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 2084;
    private TextView detectando;
    private String texto;
    public FirebaseTranslatorOptions options;
    public FirebaseTranslator translator;
    public FirebaseModelDownloadConditions conditions;
    final Context context = this;
    int flag = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //database
        setTitle("Floating Translator");
        FloatingDatabase database = Room
                .databaseBuilder(this,FloatingDatabase.class,"traducao.db")
                .allowMainThreadQueries()
                .build();
        RoomTraducao save = database.getRoomTradutorDAO();
        //
        ListView lista_t = findViewById(R.id.list_traducao);
        lista_t.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                save.todos()));

        //

        if(save.todos().isEmpty()) {//Se estiver com algo na lista, então quer dizer que tudo ja foi instalado
            options = new FirebaseTranslatorOptions.Builder()
                    //from language
                    .setSourceLanguage(FirebaseTranslateLanguage.EN)
                    // to language
                    .setTargetLanguage(FirebaseTranslateLanguage.PT)
                    .build();

            translator = FirebaseNaturalLanguage.getInstance()
                    .getTranslator(options);

            conditions = new FirebaseModelDownloadConditions.Builder()
                    .requireWifi()
                    .build();
            ProgressDialog dialog = ProgressDialog.show(context, "Fazendo Download Dos Arquivos", "Aguarde", true);
            if (flag == 0) {
                Task<Void> dw = translator.downloadModelIfNeeded(conditions); // task de fazer o download
                Tasks.whenAll(dw, dw).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();
                    }
                });
            }
        }
        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            askPermission();
        }



        //Inicialização d interações
        findViewById(R.id.fabCreateWidget).setOnClickListener(this); //floating button que ao clicar inicia a floating windows;
        findViewById(R.id.refresh).setOnClickListener(this); //floating button que ao clicar inicia a floating windows;
        lista_t.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Traducao itemValue = (Traducao) lista_t.getItemAtPosition(position);//pega o valor do elemento da posição clicada
                //cria um alert mostrando as informacoes
                AlertDialog.Builder alert = new AlertDialog.Builder(context); //cria uma Builder para um alerta;
                alert.setTitle("Informações");
                alert
                        .setMessage("Original: " + itemValue.getIngles() + "\nTradução: " + itemValue.getTraducao() + "\nTotal De Pesquisas = " + itemValue.getFrequencia())
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .setNegativeButton("Deletar Palavra", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //save.deleta(itemValue.getIngles()); // TIRAR O getIngles() PARA QUE FUNCIONE COM O BANCO DE DADOS
                                save.deleta(itemValue);
                                refresh();
                            }
                        });
                AlertDialog alertD = alert.create();
                alertD.show();
            }
        });

    }


    private void askPermission() { //função que ira fazer a transferencia para as configurações do android, para poder fazer a permissão
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
    }

    @Override
    public void onClick(View v) {//Função geral para setOnClickListener; neste caso ja que so temos um botão, então n precisamos verificar qual é o id, basta ja fazer a função que inicializa o floating
        switch (v.getId()) {
            case R.id.fabCreateWidget:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    startService(new Intent(MainActivity.this, FloatingViewService.class));
                } else if (Settings.canDrawOverlays(this)) {
                    startService(new Intent(MainActivity.this, FloatingViewService.class));
                } else {
                    askPermission();
                    Toast.makeText(this, "You need System Alert Window Permission to do this", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.refresh:
                refresh();

        }
    }
    public void refresh(){ //função responsavel para dar refresh na activity
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }



}