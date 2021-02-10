package com.example.floatingwindow.DAO;
//versão teste

import com.example.floatingwindow.model.Traducao;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class DAOlist {

    //private final static List<Traducao> traducoes = new ArrayList<>();
    private final static HashMap<String, Traducao> map = new HashMap<String, Traducao>(); //Quero add os elementos na hash map e depois passar para o arraylist, de forma que os elementos não se repitam

    public void salva(Traducao aluno) {
        if(map.get(aluno.getIngles()) == null) { // se não existe no mapa, coloca ele
            map.put(aluno.getIngles(), aluno);
        }else{
            aluno.aumentaFreq(map.get(aluno.getIngles()).getFrequencia());
            map.put(aluno.getIngles(),aluno);
        }
    }

    public List<Traducao> todos() {
        Collection<Traducao> values = map.values(); //pega todos os values do mapa
        ArrayList<Traducao> traducoes = new ArrayList<Traducao>(values);
        return traducoes;
    }

    public void deleta(String ingles) {
        map.remove(ingles);
    }


}
