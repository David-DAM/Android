package com.example.bread4all;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class ProductoActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    MediaPlayer mediaPlayer=null;
    SurfaceView superficie=null;
    int pos=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);

        inicializarSuperficieReproductor();

        mediaPlayer=new MediaPlayer();

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
            mediaPlayer.setDataSource(this, Uri.parse("android.resource://"+getPackageName()+"/"));
            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void reproducir(View view){
        mediaPlayer.start();
    }

    public void paraReproduccion(View view){

        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
    }

    public void pausarReproduccion(View view){

        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();

        }else {
            mediaPlayer.start();

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
