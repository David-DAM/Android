package com.example.prueba;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText precio;
    private EditText iva;
    private TextView resultado;
    private Button bCalcularIvaV2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        precio=(EditText) findViewById(R.id.editTextPrecio);
        iva=(EditText) findViewById(R.id.editTextIVA);
        resultado=(TextView) findViewById(R.id.textViewIvaCalcular);
        bCalcularIvaV2=findViewById(R.id.button2);

        bCalcularIvaV2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                calcular();
            }
        });

        resultado=findViewById(R.id.textViewIvaCalcular);

        resultado.setOnLongClickListener(new View.OnLongClickListener() {

            public boolean onLongClick(View view) {
                calcular();
                return false;
            }
        });
    }

    public void sePulsa(View view){
        calcular();
    }

    private void calcular(){
        double resultadoTemp=0;

        resultadoTemp=Double.parseDouble(precio.getText().toString())*((Double.parseDouble(iva.getText().toString())/100)+1 );

        resultado.setText(String.valueOf(resultadoTemp));
    }
}