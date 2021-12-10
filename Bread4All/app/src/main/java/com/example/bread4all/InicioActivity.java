package com.example.bread4all;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Telephony;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class InicioActivity extends AppCompatActivity {

    private List<Producto> productos;
    private RecyclerView.LayoutManager llm;
    private RVAdapter adapter;

    TextView textViewMoneda,textViewCantidad,textViewProductoTop;
    ActivityResultLauncher<Intent> activityResultLauncher;

    TextView nombreTop;
    private ValueEventListener eventListener;
    private DatabaseReference dbReference;

    private static String TAGLOG="firebase-db";

    String telefono,mensaje;
    int PETICION_PERMISOS_LLAMADAS=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        Toolbar topAppBar=  findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);
        //Verificamos los permisos
        verificarPermisos();
        //Obetenemos referencia a la base de datos de FireBase
        dbReference= FirebaseDatabase.getInstance("https://bread4all-14e0d-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("Productos");

        nombreTop=findViewById(R.id.textViewNombre);
        textViewCantidad=findViewById(R.id.textViewCantidad);
        textViewProductoTop=findViewById(R.id.textViewProductoTop);

        textViewProductoTop.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Snackbar.make(findViewById(R.id.topAppBar),"Segun nuestra opinion",Snackbar.LENGTH_SHORT).show();
                return false;
            }
        });

        //Registramos el Textview para el menu contextual
        registerForContextMenu(textViewCantidad);

        //Eventlistener para obtener el mejor producto de la base de datos de FireBase y mostratlo en el TextView
        eventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nombreTop.setText("");

                if (dataSnapshot.child("Best").exists()){
                    nombreTop.setText(dataSnapshot.child("Best").getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAGLOG,"Error",databaseError.toException());
            }
        };
        //Listener que comprueba si cambian los datos en FireBase
        dbReference.addValueEventListener(eventListener);

        textViewMoneda=findViewById(R.id.textViewMoneda);
        //Cargar preferencias al iniciar
        loadPref();
        //Cargar preferencias cuando cambien
        activityResultLauncher= registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        loadPref();
                    }
                }
        );

        RecyclerView rv = findViewById(R.id.rv);

        rv.setHasFixedSize(true);

        llm = new LinearLayoutManager(this);

        rv.setLayoutManager(llm);
        //Obtener registros de SQLite para almacenarlos en el array de productos y poder llenar el recyclerview
        inicializarProductos();
        //Pasamos la lista de productos al adapter para llenar el recyclerview
        adapter = new RVAdapter(this, productos);

        rv.setAdapter(adapter);
    }

    //Metodo para crear el menu superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_superior,menu);
        return true;
    }

    //Metodo para dar funcionalidad al pulsar los distintos botones del menu superior
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id=item.getItemId();

        switch (id){
            case R.id.historial:
                Intent recientes=new Intent(this,RecientesActivity.class);
                startActivity(recientes);
                return true;
            case R.id.acerca_de:
                try {
                    mostrar();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.ayuda:
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT,"DUDA");
                intent.putExtra(Intent.EXTRA_TEXT,"Introduzca su duda");
                intent.putExtra(Intent.EXTRA_EMAIL,new String[]{"soporteBread4All@gamil.com"});
                startActivity(intent);
                return true;
            case R.id.pgweb:
                Intent web=new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.pansandcompany.com/"));
                startActivity(web);
                return true;
            case R.id.Preferencias:
                Intent pref=new Intent(this,PreferencesActivity.class);
                activityResultLauncher.launch(pref);
                return  true;
            case R.id.llamadas:
                String text=llamadas();
                Snackbar.make(findViewById(R.id.topAppBar),text,Snackbar.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String llamadas(){

        StringBuilder texto=new StringBuilder();
        String text;

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)== PackageManager.PERMISSION_DENIED ||
                ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_CALL_LOG)== PackageManager.PERMISSION_DENIED){

        }

        String [] projection= new String[]{CallLog.Calls._ID,CallLog.Calls.TYPE,CallLog.Calls.NUMBER};

        Uri llamadasUri= CallLog.Calls.CONTENT_URI;

        Cursor cur;

        ContentResolver cr=getContentResolver();

        cur=cr.query(llamadasUri,projection,null,null,null);

        if (cur.getCount()!=0){
            cur.moveToFirst();

            int tipo,id,colId,colTipo,colTelefono;
            String tipoLlamada="",telefono;

            colId=cur.getColumnIndex(CallLog.Calls._ID);
            colTipo=cur.getColumnIndex(CallLog.Calls.TYPE);
            colTelefono=cur.getColumnIndex(CallLog.Calls.NUMBER);

            do {
                id=cur.getInt(colId);
                tipo=cur.getInt(colTipo);
                telefono=cur.getString(colTelefono);

                if (tipo==CallLog.Calls.INCOMING_TYPE){
                    tipoLlamada="Entrada";
                }else if (tipo==CallLog.Calls.OUTGOING_TYPE){
                    tipoLlamada="Salida";
                }else if (tipo==CallLog.Calls.MISSED_TYPE){
                    tipoLlamada="Perdida";
                }

                texto.append(id+" - "+tipoLlamada+" - "+telefono+"\n");
            }while (cur.moveToNext());

            cur.close();
        }

        text=texto.toString();

        return text;


    }

    //Metodo para crear el menu contextual
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater=getMenuInflater();
        int id=v.getId();

        switch (id){
            case R.id.textViewCantidad:
                inflater.inflate(R.menu.menu_contextual,menu);
                break;
        }
    }
    //Metodo para dar funcion a las opciones del menu contextual
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        switch (item.getItemId()){
            case R.id.Añadir:
                Snackbar.make(findViewById(R.id.topAppBar),"Estamos trabajando en aumentar el saldo",Snackbar.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    //Metodo que accede al txt de la carpeta raw en recursos y lo muestra con un snackbar
    public void mostrar() throws IOException {
        String linea;
        InputStream inputStream=this.getResources().openRawResource(R.raw.acercade);
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));

        if (inputStream!=null){
            while((linea= bufferedReader.readLine())!=null){
                Snackbar.make(findViewById(R.id.topAppBar),linea,Snackbar.LENGTH_SHORT).show();
            }
        }
    }
    //Metodo que  obtener registros de SQLite para almacenarlos en el array de productos y poder llenar el recyclerview
    private void inicializarProductos(){
        productos = new ArrayList<>();

        productos.add(new Producto("Bimbo",2.50,R.drawable.ic_historial));
        productos.add(new Producto("Artesano",4.50,R.drawable.ic_historial));
        productos.add(new Producto("Viena",3.00,R.drawable.ic_historial));
        productos.add(new Producto("Molde",1.00,R.drawable.ic_historial));
        productos.add(new Producto("Italiano",5.00,R.drawable.ic_historial));
        productos.add(new Producto("Tostado",3.50,R.drawable.ic_historial));


    }


    //Metodo que carga las preferencias que haya seleccionado el usuario y modifica el TextView para mostrar los cambios
    public void loadPref(){
        SharedPreferences mySharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);

        String monedaPreference;

        monedaPreference= mySharedPreferences.getString("moneda","euros");

        textViewMoneda.setText(monedaPreference);
    }
    //Metodo que verifica si hemos otorgado los permisos solicitados, en caso negativo se nos solicitaran por pantalla
    public void verificarPermisos(){
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_CALL_LOG)== PackageManager.PERMISSION_DENIED ||
                ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)== PackageManager.PERMISSION_DENIED ||
                ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_CALL_LOG)== PackageManager.PERMISSION_DENIED){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.READ_CALL_LOG},
                    PETICION_PERMISOS_LLAMADAS);
        }
    }

    public String displaySmsLog() {
        Uri sms= Telephony.Sms.CONTENT_URI;
        String [] projection= new String[]{Telephony.Sms._ID, Telephony.Sms.TYPE, Telephony.Sms._COUNT};
        ContentResolver cr=getContentResolver();
        Cursor cursor = cr.query(sms, projection, null, null, null);
        String msgData = "";
        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {

                for(int idx=0;idx<cursor.getColumnCount();idx++)
                {
                    msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
                }
                // use msgData
            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
        }

        return msgData;

    }




}
