package com.example.autofillex.DataBase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {AccountEntity.class},version = 1)
public abstract class AccountDataBase extends RoomDatabase {
    public static final String DB_NAME = "RecordData.db";
    public static volatile AccountDataBase instance;

    public static synchronized AccountDataBase getInstance(Context context){
        if(instance == null){
            instance = create(context);
        }
        return instance;
    }

    private static AccountDataBase create(final Context context){
        return Room.databaseBuilder(context,AccountDataBase.class,DB_NAME).build();
    }
    public abstract AccountDao getDataDao();
}
