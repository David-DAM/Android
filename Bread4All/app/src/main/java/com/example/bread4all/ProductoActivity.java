package com.example.bread4all;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class ProductoActivity extends AppCompatActivity{
    ImageView imageView;
    VideoView miVideoView;
    MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);

        imageView=findViewById(R.id.imageView);
        miVideoView=findViewById(R.id.videoView);

        miVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                miVideoView.start();
                miVideoView.requestFocus();
                ponerControles(miVideoView);
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
        imageView.setVisibility(View.INVISIBLE);
        cargarMultimedia();
    }
    public void botonIzquierdo(View view){
        miVideoView.stopPlayback();
        miVideoView.setVisibility(View.INVISIBLE);
    }

    public void ponerControles(VideoView videoView){
        mediaController=new MediaController(this);
        mediaController.setAnchorView((View) videoView.getParent());
        videoView.setMediaController(mediaController);
    }

}
