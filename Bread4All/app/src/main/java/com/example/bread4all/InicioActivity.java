package com.example.bread4all;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.core.content.ContextCompat;
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

    SQLiteDatabase bbdd;
    bbddRecientes conexion;

    TextView textViewMoneda,textViewCantidad,textViewProductoTop;
    ActivityResultLauncher<Intent> activityResultLauncher;

    TextView nombreTop;
    private ValueEventListener eventListener;
    private DatabaseReference dbReference;

    private static String TAGLOG="firebase-db";

    String telefono,mensaje;
    int PETICION_PERMISOS_SMS=0;

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
        //Conexion con la base de datos de SQLite
        conexion=new bbddRecientes(this,"bbddRecientes",null,1);

        bbdd=conexion.getWritableDatabase();

        RecyclerView rv = findViewById(R.id.rv);

        rv.setHasFixedSize(true);

        llm = new LinearLayoutManager(this);

        rv.setLayoutManager(llm);
        //Insertar registros en SQLite
        insertarRegistros();
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
                Snackbar.make(findViewById(R.id.topAppBar),"Estamos trabajando en el historial",Snackbar.LENGTH_SHORT).show();
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
            case R.id.SMS:
                telefono = "658233695";
                mensaje = "Introduzca su mensaje";

                Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + telefono));
                smsIntent.putExtra("sms_body", mensaje);
                startActivity(smsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

        String [] camposMostrar= new String[]{"nombre","precio","foto"};

        if (bbdd!=null){
            Cursor c1= bbdd.query("recientes",camposMostrar,null,null,null,null,null);

            if (c1.moveToFirst()){
                do {
                    productos.add(new Producto(c1.getString(0),c1.getDouble(1),c1.getInt(2) ));
                }while (c1.moveToNext());
            }
        }


    }
    //Insertar registros en SQLite al inicializar la Activity, en el caso de que ya se hayan inicializado previamente no volvera a introducir los datos
    public void insertarRegistros(){
        String nombres[]={"Bimbo","Artesano","Viena"};
        double precios[]={2.50,4.50,3.00};

        if (bbdd!=null && !inicializados()){
            for (int i = 0; i < nombres.length; i++) {
                String sql=("INSERT INTO RECIENTES VALUES(?,?,?)");
                SQLiteStatement statement =bbdd.compileStatement(sql);

                statement.clearBindings();

                statement.bindString(1,nombres[i]);
                statement.bindDouble(2,precios[i]);
                statement.bindLong(3,R.drawable.ic_historial);

                long rowId= statement.executeInsert();
            }
        }



    }
    //Metodo que comprueba si ya se han insertado los datos previamente
    public boolean inicializados(){
        boolean res=false;
        String [] camposMostrar= new String[]{"nombre"};

        Cursor c1= bbdd.query("recientes",camposMostrar,null,null,null,null,null);

        if (c1.moveToFirst()){
            do {
                if(c1.getString(0)!=null){
                    res=true;
                }
            }while (c1.moveToNext());
        }

        return res;
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
        int permisos= ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);

        if (permisos== PackageManager.PERMISSION_GRANTED){
            Snackbar.make(findViewById(R.id.topAppBar),"Permisos concedidos",Snackbar.LENGTH_SHORT).show();
        }else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    PETICION_PERMISOS_SMS);
        }
    }




}
