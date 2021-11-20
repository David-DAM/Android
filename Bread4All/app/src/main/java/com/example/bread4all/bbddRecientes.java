package com.example.bread4all;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class bbddRecientes extends SQLiteOpenHelper {

    public bbddRecientes(Context contexto, String nombre, SQLiteDatabase.CursorFactory factory, int version){
        super(contexto,nombre,factory,version);
    }

    //Creacion de la base de datos
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE recientes(nombre TEXT,precio FLOAT,foto INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS RECIENTES");
        db.execSQL("CREATE TABLE recientes(nombre TEXT,precio FLOAT,foto INTEGER)");
    }
}
