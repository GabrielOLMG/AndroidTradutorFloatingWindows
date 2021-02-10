package com.example.floatingwindow;

import android.app.AlertDialog;
import android.app.Service;

import androidx.annotation.NonNull;
import androidx.room.Room;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.floatingwindow.Database.FloatingDatabase;
import com.example.floatingwindow.Database.dao.RoomTraducao;
import com.example.floatingwindow.model.Traducao;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.List;

public class FloatingViewService extends Service implements View.OnClickListener { //usando implements View.OnClickListener, vc faz pode criar uma unica função OnClick para agrupar todos os comportamentos d click

    public FirebaseTranslatorOptions options;
    public FirebaseTranslator translator;
    public FirebaseModelDownloadConditions conditions;
    private WindowManager mWindowManager;
    WindowManager.LayoutParams params;
    private View mFloatingView;
    private View collapsedView;
    private View expandedView;
    private EditText texto;
    final Context context = this;
    String traducao;
    int flag = -1;

    public FloatingViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //getting the widget layout from xml using layout inflater
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null); //cria uma view com o xml layout_floating_widget

        //setting the layout parameters
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,  // tenho que mudar, ja qu não permite usar outros app com teclado ao msm tempo ; FLAG_NOT_TOUCH_MODAL -> n permite tocar fora ; FLAG_NOT_FOCUSABLE -> permite tocar fora do app
                PixelFormat.TRANSLUCENT);

        //getting windows services and adding the floating view to it
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        //getting the collapsed and expanded view from the floating view
        collapsedView = mFloatingView.findViewById(R.id.layoutCollapsed);
        expandedView = mFloatingView.findViewById(R.id.layoutExpanded);

        //adding click listener to close button and expanded view
        mFloatingView.findViewById(R.id.buttonClose).setOnClickListener(this);
        expandedView.setOnClickListener(this);

        //
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
        //

        //adding an touchlistener to make drag movement of the floating widget
        mFloatingView.findViewById(R.id.relativeLayoutParent).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;


                    case MotionEvent.ACTION_UP:
                        //when the drag is ended switching the state of the widget
                        collapsedView.setVisibility(View.GONE);
                        expandedView.setVisibility(View.VISIBLE);
                        if(flag == -1) {
                            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                            mWindowManager.updateViewLayout(mFloatingView, params);
                            flag*=-1;
                        }else{
                            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                            mWindowManager.updateViewLayout(mFloatingView, params);
                            flag*=-1;
                        }
                        return true;


                    case MotionEvent.ACTION_MOVE:
                        //this code is helping the widget to move around the screen with fingers
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });

        //add clique para o botão de enviar e fechar (talvez de para substituir por um alarm);
        //Tenho que descobrir como fazer os botões ficarem um do lado do outro
        mFloatingView.findViewById(R.id.enviar).setOnClickListener(this);
        mFloatingView.findViewById(R.id.cancelar).setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }

    @Override
    public void onClick(View v) { //Função geral para setOnClickListener
        switch (v.getId()) { //para cada id, o click vai fazer algo diferente
            case R.id.layoutExpanded:
                //switching views
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
                if(flag == -1) {
                    params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                    mWindowManager.updateViewLayout(mFloatingView, params);
                    flag*=-1;
                }else{
                    params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                    mWindowManager.updateViewLayout(mFloatingView, params);
                    flag*=-1;
                }
                break;

            case R.id.buttonClose:
                //closing the widget
                stopSelf();
                break;
            case R.id.enviar:
                //Cria um toat com a informação digitada e dps limpa o input
                //tenho que descobrir como enviar a informação do input para a view principal, provavelmente  usando DAO
                texto = mFloatingView.findViewById(R.id.input);
                download(texto.getText().toString().toLowerCase());


                break;
            case R.id.cancelar:
                //fecha a caixa d input e limpa o input
                params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                mWindowManager.updateViewLayout(mFloatingView, params);


                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
                texto = mFloatingView.findViewById(R.id.input);
                texto.getText().clear();
                flag = -1;
                break;
        }
    }


    private void download(String ingles) {
        Task<Void> dw =  translator.downloadModelIfNeeded(conditions); // task de fazer o download
        Task<String> tw = translator.translate(ingles); // task de traduzir
        Tasks.whenAll(dw,tw).addOnSuccessListener(new OnSuccessListener<Void>() { // faz com que so continue ao terminar d fazer as tasks entre ().
            @Override
            public void onSuccess(Void aVoid) {
                traducao = tw.getResult();

                Traducao novo = new Traducao(ingles,tw.getResult(), 1);
                RoomTraducao save = Room
                        .databaseBuilder(context,FloatingDatabase.class,"traducao.db")
                        .allowMainThreadQueries()
                        .build()
                        .getRoomTradutorDAO();
                //final DAOlist save = new DAOlist();
                List<String> lista = save.listaIngles();

                if(!lista.contains(novo.getIngles())){
                    save.salva(novo);
                }else{
                    List<Integer> valor = save.pegaFrequencia(novo.getIngles());
                    save.update(valor.get(0) + 1,novo.getIngles());
                }
                texto.getText().clear();
                TextView a = (TextView)expandedView.findViewById(R.id.traduzido);
                a.setText(ingles + "->" + traducao);

            }
        });
    }


}