package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private ValueEventListener eventListener;
    private DatabaseReference dbReference;

    private static String TAGLOG="firebase-db";

    TextView lblFormDat, lblInfDat,lblManDat;
    Button btnEliminarListener,btnArrancarListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbReference= FirebaseDatabase.getInstance("https://fir-prueba-6d741-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("Avisos");

        lblFormDat=findViewById(R.id.lblFormDat);
        lblInfDat=findViewById(R.id.lblInfDat);
        lblManDat=findViewById(R.id.lblMantDat);

        btnEliminarListener=findViewById(R.id.btnEliminarListener);
        btnArrancarListener=findViewById(R.id.btnArrancarListener);

        eventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lblFormDat.setText("");
                lblInfDat.setText("");
                lblManDat.setText("");

                if (dataSnapshot.child("Formación").exists()){
                    lblFormDat.setText(dataSnapshot.child("Formación").getValue().toString());
                }
                if (dataSnapshot.child("Informática").exists()){
                    lblInfDat.setText(dataSnapshot.child("Informática").getValue().toString());
                }
                if (dataSnapshot.child("Mantenimiento").exists()){
                    lblManDat.setText(dataSnapshot.child("Mantenimiento").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAGLOG,"Error",databaseError.toException());
            }
        };

        dbReference.addValueEventListener(eventListener);

        btnArrancarListener.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dbReference.addValueEventListener(eventListener);
                    }
                }
        );

        btnEliminarListener.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dbReference.removeEventListener(eventListener);
                    }
                }
        );
    }

    public void clickBoton(View vista){
        Intent intent = new Intent();

        switch (vista.getId()){
            case R.id.btnUpdate:
                intent = new Intent(this, UpdateActivity.class);
                break;
            case R.id.btnInsertar:
                intent = new Intent(this, InsertActivity.class);
                break;
            case R.id.btnDelete:
                intent = new Intent(this, DeleteActivity.class);
                break;
            case R.id.btnInsertObjeto:
                intent = new Intent(this, InsertVariosCamposActivity.class);
                break;
        }
        startActivity(intent);
    }
}