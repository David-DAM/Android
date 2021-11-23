package com.example.multimedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    MediaPlayer mediaPlayer=null;
    SurfaceView superficie=null;
    Button botonCargar,botonPausar,botonParar,botonReproducir;
    int pos=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botonCargar=findViewById(R.id.botonCargar);
        botonPausar=findViewById(R.id.botonPausar);
        botonParar=findViewById(R.id.botonParar);
        botonReproducir=findViewById(R.id.botonReproducir);

        botonCargar.setEnabled(true);
        botonPausar.setEnabled(false);
        botonParar.setEnabled(false);
        botonReproducir.setEnabled(false);

        inicializarSuperficieReproductor();

        mediaPlayer=new MediaPlayer();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                botonCargar.setEnabled(false);
                botonPausar.setEnabled(false);
                botonParar.setEnabled(false);
                botonReproducir.setEnabled(true);

            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();

                botonCargar.setEnabled(true);
                botonPausar.setEnabled(false);
                botonParar.setEnabled(false);
                botonReproducir.setEnabled(false);
            }
        });


    }

    @Override
    protected void onPause(){
        super.onPause();

        if (mediaPlayer!=null){
            pos=mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if (mediaPlayer!=null){
            mediaPlayer.release();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle estadoGuardado){
        super.onSaveInstanceState(estadoGuardado);

        if (mediaPlayer!=null){
            estadoGuardado.putInt("posicion",pos);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle estadoGuardado) {
        super.onRestoreInstanceState(estadoGuardado);

        if (estadoGuardado!=null && mediaPlayer!=null){
            pos=estadoGuardado.getInt("posicion");
        }

    }

    public void cargarMultimedia(View view){
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(this, Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.video));
            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void reproducir(View view){
        mediaPlayer.start();

        botonCargar.setEnabled(false);
        botonPausar.setEnabled(true);
        botonParar.setEnabled(true);
        botonReproducir.setEnabled(false);
    }

    public void paraReproduccion(View view){
        botonCargar.setEnabled(true);
        botonPausar.setEnabled(false);
        botonParar.setEnabled(false);
        botonReproducir.setEnabled(false);
        
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
    }

    public void pausarReproduccion(View view){

        botonCargar.setEnabled(false);

        botonParar.setEnabled(true);
        botonReproducir.setEnabled(true);

        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            botonPausar.setEnabled(false);
        }else {
            mediaPlayer.start();
            botonPausar.setEnabled(true);
        }
    }

    protected void inicializarSuperficieReproductor(){
        superficie=findViewById(R.id.superficie);

        SurfaceHolder holder= superficie.getHolder();

        holder.addCallback(this);
    }

    public void surfaceDestroyed(SurfaceHolder holder){
        mediaPlayer.release();
    }

    public void surfaceChanged(SurfaceHolder holder,int format,int with,int height){

    }

    public void surfaceCreated(SurfaceHolder holder){
        mediaPlayer.setDisplay(holder);
    }


}