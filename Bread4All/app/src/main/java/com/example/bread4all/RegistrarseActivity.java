package com.example.bread4all;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class RegistrarseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

    }
    //Metodo para abrir la Activity de inicio
    public void LanzarInicio(View view){
        Intent i=new Intent(this,InicioActivity.class);
        startActivity(i);
    }
}
