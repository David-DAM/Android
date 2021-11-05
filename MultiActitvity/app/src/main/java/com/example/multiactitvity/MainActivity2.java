package com.example.multiactitvity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity2 extends AppCompatActivity {

    EditText editTextPersonName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        editTextPersonName = findViewById(R.id.editTextPersonName);
    }

    public void volver (View view){

        Intent intent = new Intent();
        intent.putExtra("MESSAGE", editTextPersonName.getText().toString());
        setResult(123, intent);
        finish();

    }
}