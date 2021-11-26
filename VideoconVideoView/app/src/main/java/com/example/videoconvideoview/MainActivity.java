package com.example.videoconvideoview;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    Button botonReproducirVideo;
    VideoView miVideoView;
    MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botonReproducirVideo=findViewById(R.id.botonReproducirVideo);
        miVideoView=findViewById(R.id.video_view);

        miVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                miVideoView.start();
                miVideoView.requestFocus();
                ponerControles(miVideoView);
                botonReproducirVideo.setEnabled(false);
            }
        });

        miVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                botonReproducirVideo.setEnabled(true);
            }
        });
    }

    public void cargarMultimedia(View view){
        miVideoView.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.video));
    }

    public void ponerControles(VideoView videoView){
        mediaController=new MediaController(this);
        mediaController.setAnchorView((View) videoView.getParent());
        videoView.setMediaController(mediaController);
    }
}