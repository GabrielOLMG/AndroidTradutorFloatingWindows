package com.example.floatingwindow.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.floatingwindow.Database.dao.RoomTraducao;
import com.example.floatingwindow.model.Traducao;

@Database(entities = {Traducao.class}, version = 1, exportSchema = false)
public abstract class FloatingDatabase extends RoomDatabase {
    public abstract RoomTraducao getRoomTradutorDAO();
}
