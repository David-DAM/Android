package com.example.prueba2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class VerificaActivity extends AppCompatActivity {
    private TextView pregunta;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifica);

        Bundle extras=getIntent().getExtras();
        String s="Hola "+extras.getString("usuario")+" ¿Aceptas las condiciones?";

        pregunta=findViewById(R.id.textViewPregunta);
        pregunta.setText(s);
    }

    public void Aceptar(View view){
        Intent i=new Intent();
        i.putExtra("resultado","ok");
        setResult(RESULT_OK,i);
        finish();
    }

    public void Rechazar(View view){
        Intent i=new Intent();
        i.putExtra("resultado","No aceptadas");
        setResult(RESULT_OK,i);
        finish();
    }

    public void Cancelar(View view){
        Intent i=new Intent();
        setResult(RESULT_CANCELED,i);
        finish();
    }
}