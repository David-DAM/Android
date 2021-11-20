package com.example.bread4all;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LogearActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logear);


    }
    //Metodo para abrir la Activity de registro
    public void LanzarRegistrarse(View view){
        Intent i=new Intent(this,RegistrarseActivity.class);
        startActivity(i);
    }
    //Metodo para abrir la Activity de inicio
    public void LanzarInicio(View view){
        Intent i=new Intent(this,InicioActivity.class);
        startActivity(i);
    }


}