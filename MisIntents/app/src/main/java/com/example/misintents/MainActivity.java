package com.example.misintents;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void pgWeb(View view){
        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.break4learning.weebly.com"));
        startActivity(intent);
    }

    public void llamadaTelefono(View view){
        Intent intent=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:658134507"));
        startActivity(intent);
    }

    public void googleMaps(View view){
        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("geo:41.656313, -0.8777351"));
        startActivity(intent);
    }

    public void mandarCorreo(View view){
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Prueba de correo android");
        intent.putExtra(Intent.EXTRA_TEXT,"Esto es una prueba de envio de mensaje");
        intent.putExtra(Intent.EXTRA_EMAIL,new String[]{"david11jv@gmail.com"});
        startActivity(intent);
    }


}