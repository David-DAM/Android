package com.example.firebase;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InsertActivity extends AppCompatActivity {
    EditText lblInfDat,lblEtiqInfDat;
    Button btnGuardar;
    private DatabaseReference dbReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        dbReference= FirebaseDatabase.getInstance("https://fir-prueba-6d741-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("Avisos");

        lblEtiqInfDat=findViewById(R.id.lblEtiqInfDat);
        lblInfDat=findViewById(R.id.lblInfDat);
        btnGuardar=findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dbReference.child(lblEtiqInfDat.getText().toString()).setValue(lblInfDat.getText().toString());
                    }
                }
        );
    }
}
