package com.example.preferencias;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView textViewMoneda;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewMoneda=findViewById(R.id.textViewMoneda);

        loadPref();

        activityResultLauncher= registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        loadPref();
                    }
                }
        );
    }

    public void loadPref(){
        SharedPreferences mySharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);

        String monedaPreference;

        monedaPreference= mySharedPreferences.getString("moneda","euro");

        textViewMoneda.setText(monedaPreference);
    }

    public void abrirPreferencias(View view){
        Intent intent=new Intent(this,SettingsActivity.class);
        activityResultLauncher.launch(intent);
    }
}