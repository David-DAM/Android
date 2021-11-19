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
import android.view.Menu;
import android.view.MenuItem;
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

    TextView textViewMoneda;
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

        verificarPermisos();

        dbReference= FirebaseDatabase.getInstance("https://bread4all-14e0d-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("Productos");

        nombreTop=findViewById(R.id.textViewNombre);

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

        dbReference.addValueEventListener(eventListener);

        textViewMoneda=findViewById(R.id.textViewMoneda);

        loadPref();

        activityResultLauncher= registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        loadPref();
                    }
                }
        );

        conexion=new bbddRecientes(this,"bbddRecientes",null,1);

        bbdd=conexion.getWritableDatabase();

        RecyclerView rv = findViewById(R.id.rv);

        rv.setHasFixedSize(true);

        llm = new LinearLayoutManager(this);

        rv.setLayoutManager(llm);

        insertarRegistros();

        inicializarProductos();

        adapter = new RVAdapter(this, productos);

        rv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.navigation_drawer,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id=item.getItemId();

        switch (id){
            case R.id.historial:
                Snackbar.make(findViewById(R.id.topAppBar),"Has elegido historial",Snackbar.LENGTH_SHORT).show();
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

    public void loadPref(){
        SharedPreferences mySharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);

        String monedaPreference;

        monedaPreference= mySharedPreferences.getString("moneda","euros");

        textViewMoneda.setText(monedaPreference);
    }

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
