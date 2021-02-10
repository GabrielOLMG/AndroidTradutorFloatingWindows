package com.example.floatingwindow.Database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.floatingwindow.model.Traducao;

import java.util.List;

@Dao
public interface RoomTraducao {

    @Insert
    void salva(Traducao palavra);

    @Query("SELECT * FROM traducao ORDER BY frequencia DESC")
    List<Traducao> todos();

    @Delete
    void deleta(Traducao palavra); //TENHO QUE ALTERAR A FORMA QUE EU PASSO O DELETA

    @Query("SELECT ingles FROM traducao")
    List<String> listaIngles();

    @Query("SELECT frequencia FROM traducao WHERE ingles = :ing")
    List<Integer> pegaFrequencia(String ing);

    @Query("UPDATE traducao SET frequencia = :nvFreq WHERE ingles = :ing")
    void update(int nvFreq, String ing);
}
