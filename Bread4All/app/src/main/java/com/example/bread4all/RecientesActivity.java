package com.example.bread4all;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecientesActivity extends AppCompatActivity {

    SQLiteDatabase bbdd;
    bbddRecientes conexion;
    TextView recientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recientes);

        recientes=findViewById(R.id.textViewRecientes);

        //Conexion con la base de datos de SQLite
        conexion=new bbddRecientes(this,"bbddRecientes",null,1);

        bbdd=conexion.getWritableDatabase();
        inicializar();
    }

    public void inicializar(){
        StringBuilder productos=new StringBuilder();
        String [] camposMostrar= new String[]{"nombre","precio"};

        if (bbdd!=null){
            Cursor c1= bbdd.query("recientes",camposMostrar,null,null,null,null,null);

            if (c1.moveToFirst()){
                do {
                    productos.append("Nombre: "+c1.getString(0)+" Precio: "+c1.getDouble(1)+"\n");
                }while (c1.moveToNext());
            }
        }

        recientes.setText(productos);

    }
}
