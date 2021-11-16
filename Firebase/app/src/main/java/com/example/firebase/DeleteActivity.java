package com.example.firebase;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteActivity extends AppCompatActivity {
    Button btnEliminar,btnNulo;
    private DatabaseReference dbReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        dbReference= FirebaseDatabase.getInstance("https://fir-prueba-6d741-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("Avisos");

        btnNulo=findViewById(R.id.btnNulo);
        btnEliminar=findViewById(R.id.btnEliminar);

        btnEliminar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dbReference.child("Informática").removeValue();
                    }
                }
        );

        btnNulo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dbReference.child("Informática").setValue(null);
                    }
                }
        );
    }
}
