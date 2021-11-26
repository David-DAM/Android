package com.example.multimediacrearsonidos;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    SoundPool soundPool;
    Button botonReproducir=null;
    private int idSonido1,idSonido2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botonReproducir=findViewById(R.id.botonReproducirVideo);
    }

    public void cargarMultimedia(View view){
        AudioAttributes audioAttributes=new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        soundPool=new SoundPool.Builder()
                .setMaxStreams(6)
                .setAudioAttributes(audioAttributes)
                .build();

        idSonido1=soundPool.load(this,R.raw.sonido1,0);
        idSonido2=soundPool.load(this,R.raw.sonido2,0);
    }
    public void playSonido1(View view){
        soundPool.play(idSonido1,1,1,1,0,1);
    }

    public void playSonido2(View view){
        soundPool.play(idSonido2,1,1,1,0,1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool=null;
    }
}