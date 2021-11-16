package com.example.firebase;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class InsertVariosCamposActivity extends AppCompatActivity {

    EditText lblInfDat,lblFormDat,lblManDat;
    Button btnGuardar;

    private DatabaseReference dbReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_varios_campos);

        dbReference= FirebaseDatabase.getInstance("https://fir-prueba-6d741-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        lblFormDat=findViewById(R.id.lblFormDat);
        lblInfDat=findViewById(R.id.lblInfDat);
        lblManDat=findViewById(R.id.lblMantDat);

        btnGuardar=findViewById(R.id.btnGuardar);

    }

    public void GuardarConObjeto(View view){
        Noticias noticias=new Noticias(
                lblFormDat.getText().toString(),
                lblInfDat.getText().toString(),
                lblManDat.getText().toString()
        );

        dbReference.push().setValue(noticias,new DatabaseReference.CompletionListener(){
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        Toast toast;
                        String mensaje;

                        if (databaseError==null){
                            String uniqueKey=databaseReference.getKey();
                            mensaje="Insertado registro con key "+uniqueKey;
                        }else {
                            mensaje="Error al insertar: "+databaseError.getMessage();
                        }

                        toast=Toast.makeText(getApplicationContext(),mensaje,Toast.LENGTH_SHORT);
                        toast.show();
                    }

        });
    }

    public void GuardarConHashMap (View vista) {

        Map<String, Object> VariosAvisos = new HashMap<String,Object>();
        VariosAvisos.put("/Avisos/Aviso2/Formación", lblFormDat.getText().toString());
        VariosAvisos.put("/Avisos/Aviso2/Informática", lblInfDat.getText().toString());
        VariosAvisos.put("/Avisos/Aviso2/Mantenimiento", lblManDat.getText().toString());

        dbReference.updateChildren(VariosAvisos);
    }

    public void GuardarConHashMap2 (View vista) {

        Map<String, String> VariosAvisos = new HashMap<>();
        VariosAvisos.put("Informática", lblInfDat.getText().toString());
        VariosAvisos.put("Formación", lblFormDat.getText().toString());
        VariosAvisos.put("Mantenimiento", lblManDat.getText().toString());

        dbReference.child("-KzzH9KGSwc-uTcGj-cv").setValue(VariosAvisos);
    }
}
