package com.example.bread4all;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

public class InicioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        Toolbar topAppBar= (Toolbar) findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.navigation_drawer,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id=item.getItemId();

        switch (id){
            case R.id.historial:
                Snackbar.make(findViewById(R.id.topAppBar),"Has elegido historial",Snackbar.LENGTH_SHORT).show();
                return true;
            case R.id.metodo_pago:
                Snackbar.make(findViewById(R.id.topAppBar),"Has elegido metodo pago",Snackbar.LENGTH_SHORT).show();
                return true;
            case R.id.ayuda:
                Snackbar.make(findViewById(R.id.topAppBar),"Has elegido ayuda",Snackbar.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
