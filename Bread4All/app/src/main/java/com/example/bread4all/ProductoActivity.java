package com.example.bread4all;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProductoActivity extends AppCompatActivity implements MediaController.MediaPlayerControl{
    ImageView imageView,imageViewComentarios;
    VideoView miVideoView;
    MediaController mediaController;
    TextView nombre,precio,moneda;
    RadioButton izq,der;
    Button subir;

    SoundPool soundPool;
    private int idSonido1,idSonido2;
    int posVideo=0;

    private final static int PETICION_PERMISO_GRABACION=6,GRABAR_VIDEO=5,CARGAR_IMAGEN_GALERIA=4,CARGAR_AUDIO_GALERIA=3,CARGAR_VIDEO_GALERIA=2,CAPTURA_IMAGEN_GUARDAR_GALERIA=1,PETICION_PERMISOS=0;
    private int permissionCheck,permissionCheck2,permissionCheck3;
    private String fotoPath="";

    MediaController mediaControllerAudio;
    private MediaPlayer mediaPlayer;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);

        imageView=findViewById(R.id.imageView);
        imageViewComentarios=findViewById(R.id.imageViewComentarios);
        miVideoView=findViewById(R.id.videoView);
        nombre=findViewById(R.id.txtNombre);
        precio=findViewById(R.id.txtPrecio);
        moneda=findViewById(R.id.txtMoneda);
        izq=findViewById(R.id.radioButtonIzq);
        der=findViewById(R.id.radioButtonDerecha);
        subir=findViewById(R.id.buttonSubir);

        imageView.setImageURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.drawable.pan));
        RellenarDatos();
        cargarSonidos();
        loadPref();

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO},
                PETICION_PERMISO_GRABACION);

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

        mediaPlayer=new MediaPlayer();
        mediaControllerAudio=new MediaController(this);

        mediaControllerAudio.setMediaPlayer(this);
        mediaControllerAudio.setAnchorView(findViewById(R.id.videoView));
        mediaControllerAudio.setVisibility(View.INVISIBLE);
        handler=new Handler();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //mediaControllerAudio.show(20000);
                        mediaPlayer.start();
                    }
                });
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle estadoGuardado){
        super.onSaveInstanceState(estadoGuardado);

        if (miVideoView!=null){
            posVideo=miVideoView.getCurrentPosition();
            estadoGuardado.putInt("posicion",posVideo);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle estadoGuardado) {
        super.onRestoreInstanceState(estadoGuardado);

        if (estadoGuardado!=null && miVideoView!=null){
            posVideo=estadoGuardado.getInt("posicion");
        }

    }

    public void botonDerecho(View view){
        playSonido2();
        imageView.setImageURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.drawable.ic_historial));
        izq.setChecked(false);

    }
    public void botonIzquierdo(View view){
        playSonido2();
        imageView.setImageURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.drawable.pan));
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
            precio.setText(precioI.toString());
        }
    }

    public void SubirImagen(View view){
        playSonido1();
        cargarImagen();
    }
    public void SubirVideo(View view){
        playSonido1();
        cargarVideo();
    }
    public void SubirAudio(View view){
        playSonido1();
        cargarAudio();
    }

    private void cargarImagen(){
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent,CARGAR_IMAGEN_GALERIA);
    }
    private void cargarVideo(){
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/");
        startActivityForResult(intent,CARGAR_VIDEO_GALERIA);
    }
    private void cargarAudio(){
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,CARGAR_AUDIO_GALERIA);
    }

    public void TomarFoto(View view){
        playSonido1();
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            tomarCapturaIntent(CAPTURA_IMAGEN_GUARDAR_GALERIA);
        }
    }

    public void tomarCapturaIntent(int code){
        permissionCheck= ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionCheck2= ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionCheck3= ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);

        if (permissionCheck==PackageManager.PERMISSION_GRANTED &&
                permissionCheck2==PackageManager.PERMISSION_GRANTED &&
                permissionCheck3==PackageManager.PERMISSION_GRANTED){

            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
                Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (intent.resolveActivity(getPackageManager())!=null){
                    File photoFile=createImageFile();

                    if (photoFile!=null){
                        fotoPath= photoFile.getPath();

                        Uri photoUri= FileProvider.getUriForFile(this,"com.example.bread4all",photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);

                        startActivityForResult(intent,code);
                    }
                }
            }

        }else {
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE

            },PETICION_PERMISOS);
        }
    }

    private File createImageFile(){
        File image=null;

        String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName="JPG_"+timeStamp+"_";

        File storageDir=null;
        storageDir=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"/CamaraClase/");

        if (!storageDir.exists()){
            storageDir.mkdirs();
        }

        try {
            image=File.createTempFile(imageFileName,".jpg",storageDir);
        }catch (IOException e){

        }
        return image;
    }

    private void galleryAddPic(){
        Intent mediaScanIntent= new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f=new File(fotoPath);
        Uri contentUri=Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public void comenzarGrabacion(View view){
        playSonido1();
        Intent intent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10);

        if (intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,GRABAR_VIDEO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PETICION_PERMISOS:
                if (grantResults.length == 3 &&
                        grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                        grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                        grantResults[2] != PackageManager.PERMISSION_GRANTED) {


                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            switch (requestCode){
                case CARGAR_IMAGEN_GALERIA:
                    Uri path=data.getData();
                    imageViewComentarios.setImageURI(path);
                    imageViewComentarios.setVisibility(View.VISIBLE);
                    miVideoView.setVisibility(View.INVISIBLE);
                    subir.setEnabled(true);
                break;
                case CARGAR_VIDEO_GALERIA:
                    Uri path2=data.getData();
                    miVideoView.setVideoURI(path2);
                    miVideoView.setVisibility(View.VISIBLE);
                    imageViewComentarios.setVisibility(View.INVISIBLE);
                    subir.setEnabled(true);
                break;
                case CAPTURA_IMAGEN_GUARDAR_GALERIA:
                    galleryAddPic();
                    miVideoView.setVisibility(View.INVISIBLE);
                    imageViewComentarios.setImageURI(Uri.fromFile(new File(fotoPath)));
                    subir.setEnabled(true);
                break;
                case GRABAR_VIDEO:
                    miVideoView.setVideoURI(data.getData());
                    miVideoView.setVisibility(View.VISIBLE);
                    imageViewComentarios.setVisibility(View.INVISIBLE);
                    subir.setEnabled(true);
                break;
                case CARGAR_AUDIO_GALERIA:
                    Uri path3=data.getData();
                    try {
                        mediaPlayer.setDataSource(this, path3);
                        mediaPlayer.prepare();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    miVideoView.setVisibility(View.VISIBLE);
                    mediaControllerAudio.setVisibility(View.VISIBLE);
                    subir.setEnabled(true);
                break;
            }
        }
    }

    public void cargarSonidos(){
        AudioAttributes audioAttributes=new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        soundPool=new SoundPool.Builder()
                .setMaxStreams(6)
                .setAudioAttributes(audioAttributes)
                .build();

        idSonido1=soundPool.load(this,R.raw.click,0);
        idSonido2=soundPool.load(this,R.raw.zas,0);
    }
    public void playSonido1(){
        soundPool.play(idSonido1,1,1,1,0,1);
    }

    public void playSonido2()
    {
        soundPool.play(idSonido2,1,1,1,0,1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundPool!=null){
            soundPool.release();
            soundPool=null;
        }else if (mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }


    }

    public void Subir(View view){
        playSonido1();
        imageViewComentarios.setImageURI(null);
        miVideoView.setVisibility(View.INVISIBLE);
        mediaPlayer.reset();
        mediaControllerAudio.setVisibility(View.INVISIBLE);
        Toast.makeText(this, "Comentario subido", Toast.LENGTH_SHORT).show();
        subir.setEnabled(false);
    }

    public void loadPref(){
        SharedPreferences mySharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);

        String monedaPreference;

        monedaPreference= mySharedPreferences.getString("moneda","euros");

        moneda.setText(monedaPreference);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mediaControllerAudio.show();
        return false;
    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

}
