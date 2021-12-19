package com.example.bread4all;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class ProductoActivity extends AppCompatActivity{
    ImageView imageView;
    VideoView miVideoView;
    MediaController mediaController;
    TextView nombre,precio;
    RadioButton izq,der;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);

        imageView=findViewById(R.id.imageView);
        miVideoView=findViewById(R.id.videoView);
        nombre=findViewById(R.id.txtNombre);
        precio=findViewById(R.id.txtPrecio);
        izq=findViewById(R.id.radioButtonIzq);
        der=findViewById(R.id.radioButtonDerecha);

        RellenarDatos();
        cargarMultimedia();

        miVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                miVideoView.start();
                miVideoView.requestFocus();
                ponerControles(miVideoView);
                miVideoView.pause();
            }
        });

        miVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

            }
        });

    }

    public void cargarMultimedia(){
        miVideoView.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.video));
    }

    public void botonDerecho(View view){

        miVideoView.stopPlayback();
        miVideoView.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.VISIBLE);
        izq.setChecked(false);

    }
    public void botonIzquierdo(View view){
        imageView.setVisibility(View.INVISIBLE);
        miVideoView.setVisibility(View.VISIBLE);
        miVideoView.start();
        der.setChecked(false);

    }

    public void ponerControles(VideoView videoView){
        mediaController=new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

    }

    private void RellenarDatos(){
        if(getIntent().hasExtra("nombre") && getIntent().hasExtra("precio")){

            String nombreI = getIntent().getStringExtra("nombre");
            Double precioI = getIntent().getDoubleExtra("precio",0.0);

            nombre.setText(nombreI);
            precio.setText(precioI.toString()+" euros");
        }
    }

}
