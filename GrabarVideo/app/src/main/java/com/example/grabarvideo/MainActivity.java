package com.example.grabarvideo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private final static int GRABAR_VIDEO=1;
    private final static int SELECCIONA_DE_GALERIA=2;
    VideoView videoView;
    int PETICION_PERMISO_GRABACION=0;
    MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView=findViewById(R.id.videoView);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO},
                PETICION_PERMISO_GRABACION);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.start();
                videoView.requestFocus();
                ponerControles(videoView);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK){
            if (requestCode==SELECCIONA_DE_GALERIA || requestCode==GRABAR_VIDEO){
                videoView.setVideoURI(data.getData());
            }
        }
    }

    public void ponerControles(VideoView view){
        mediaController=new MediaController(this);
        mediaController.setAnchorView((View) videoView.getParent());
        videoView.setMediaController(mediaController);
    }

    public void SelectImage(View view){
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(intent,SELECCIONA_DE_GALERIA);
    }

    public void comenzarGrabacion(View view){
        Intent intent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,5);

        if (intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,GRABAR_VIDEO);
        }
    }


}