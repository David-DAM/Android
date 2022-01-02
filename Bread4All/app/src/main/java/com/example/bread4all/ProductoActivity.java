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

    private SoundPool soundPool;
    private int idSonido1,idSonido2;
    int pos=0;

    private final static int PETICION_PERMISO_GRABACION=6,CARGAR_IMAGEN_GALERIA=4,CARGAR_AUDIO_GALERIA=3,CARGAR_VIDEO_GALERIA=2,CAPTURA_IMAGEN_GUARDAR_GALERIA=1,PETICION_PERMISOS=0;
    private int permissionCheck,permissionCheck2,permissionCheck3;
    private String fotoPath="",archivoSalida="",subido;
    Uri path3,path2;

    MediaController mediaControllerAudio;
    MediaPlayer mediaPlayer;
    Handler handler;

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
        //Llamada la metodo par arellenar datos
        RellenarDatos();
        //Llamada al metodo para cargar sonidos
        cargarSonidos();
        //Llamada al metodo para cargar preferencias
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
                        mediaPlayer.start();
                    }
                });
            }
        });

    }
    //Cambia a la segunda imagen
    public void botonDerecho(View view){
        playSonido2();
        imageView.setImageURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.drawable.pan2));
        izq.setChecked(false);

    }
    //Cambia a la primera imagen
    public void botonIzquierdo(View view){
        playSonido2();
        imageView.setImageURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.drawable.pan));
        der.setChecked(false);

    }
    //Pone lo controles de reproduccion del mediaplayer
    public void ponerControles(VideoView videoView){
        mediaController=new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

    }
    //Rellena los datos referentes al producto
    private void RellenarDatos(){
        if(getIntent().hasExtra("nombre") && getIntent().hasExtra("precio")){

            String nombreI = getIntent().getStringExtra("nombre");
            Double precioI = getIntent().getDoubleExtra("precio",0.0);

            nombre.setText(nombreI);
            precio.setText(precioI.toString());
        }
    }
    //Reproduce un sonido y llama al intent para seleccionar una imagen
    public void SubirImagen(View view){
        playSonido1();
        cargarImagen();
    }
    //Reproduce un sonido y llama al intent para seleccionar un video
    public void SubirVideo(View view){
        playSonido1();
        cargarVideo();
    }
    //Reproduce un sonido y llama al intent para seleccionar un audio
    public void SubirAudio(View view){
        playSonido1();
        cargarAudio();
    }
    //Selecciona el archivo y envia el resultado al metodo onActivityResult
    private void cargarImagen(){
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent,CARGAR_IMAGEN_GALERIA);
    }
    //Selecciona el archivo y envia el resultado al metodo onActivityResult
    private void cargarVideo(){
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/");
        startActivityForResult(intent,CARGAR_VIDEO_GALERIA);
    }
    //Selecciona el archivo y envia el resultado al metodo onActivityResult
    private void cargarAudio(){
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,CARGAR_AUDIO_GALERIA);
    }
    //Reproduce un sonido y llama al metodo para capturar una imagen
    public void TomarFoto(View view){
        playSonido1();
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            tomarCapturaIntent(CAPTURA_IMAGEN_GUARDAR_GALERIA);
        }
    }
    //Comprueba los permisos, llama a la camara para tomar una foto, crea la foto y envia el resultado al metodo onActivityResult
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
    //Crea una imagen
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
    //Guarda la imagen creada en la galeria
    private void galleryAddPic(){
        Intent mediaScanIntent= new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f=new File(fotoPath);
        Uri contentUri=Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
    //Reproduce un sonido y llama a la camara para grabar un video, luego envia el resultado al metodo onActivityResult
    public void comenzarGrabacion(View view){
        playSonido1();
        Intent intent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10);

        if (intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,CARGAR_VIDEO_GALERIA);
        }
    }
    //Toma las medidas necesarias si no se tienen todos los permisos concedidos
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
    //Realiza las acciones correspondientes segun el resultado obtenido en el requestCode
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
                    path2=data.getData();
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
                case CARGAR_AUDIO_GALERIA:
                    path3=data.getData();
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
    //Carga los sonidos
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
    //Reproduce el primer sonido
    public void playSonido1(){
        soundPool.play(idSonido1,1,1,1,0,1);
    }
    //Reproduce el segundo sonido
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
    //Limpia la pantalla para simular la subida de un comentario
    public void Subir(View view){
        playSonido1();
        subido=getString(R.string.subido);
        imageViewComentarios.setImageURI(null);
        miVideoView.setVisibility(View.INVISIBLE);
        mediaPlayer.reset();
        mediaControllerAudio.setVisibility(View.INVISIBLE);
        Toast.makeText(this, subido, Toast.LENGTH_SHORT).show();
        subir.setEnabled(false);
    }
    //Carga las preferencias
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
    //Guarda el estado del mediaplayer
    @Override
    protected void onSaveInstanceState(Bundle estadoGuardado){
        super.onSaveInstanceState(estadoGuardado);

        if (mediaPlayer!=null && path3!=null){
            pos=mediaPlayer.getCurrentPosition();
            archivoSalida=path3.toString();
            estadoGuardado.putInt("posicion",pos);
            estadoGuardado.putString("archivo",archivoSalida);
            mediaPlayer.stop();
            mediaPlayer.release();

        }else if (miVideoView!=null && path2!=null){

            pos=miVideoView.getCurrentPosition();
            archivoSalida=path2.toString();
            estadoGuardado.putInt("posicion",pos);
            estadoGuardado.putString("archivo",archivoSalida);
            miVideoView.stopPlayback();
        }



    }
    //Restaura el estado del mediaplayer
    @Override
    protected void onRestoreInstanceState(Bundle estadoGuardado) {
        super.onRestoreInstanceState(estadoGuardado);

        if (estadoGuardado!=null && mediaPlayer!=null){
            pos=estadoGuardado.getInt("posicion");
            archivoSalida=estadoGuardado.getString("archivo");
            path3=Uri.parse(archivoSalida);

            try {
                mediaPlayer.setDataSource(this,path3);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }


            mediaPlayer.seekTo(pos);
            mediaControllerAudio.setVisibility(View.VISIBLE);
            miVideoView.setVisibility(View.VISIBLE);
            subir.setEnabled(true);

        }else if (estadoGuardado!=null && miVideoView!=null){
            pos=estadoGuardado.getInt("posicion");
            archivoSalida=estadoGuardado.getString("archivo");
            path2=Uri.parse(archivoSalida);

            miVideoView.setVideoURI(path2);
            miVideoView.seekTo(pos);
            mediaController.setVisibility(View.VISIBLE);
            miVideoView.setVisibility(View.VISIBLE);
            subir.setEnabled(true);
        }

    }

}
