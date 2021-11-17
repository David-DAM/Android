package com.example.bread4all;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InicioActivity extends AppCompatActivity {

    private List<Producto> productos;
    private RecyclerView.LayoutManager llm;
    private RVAdapter adapter;

    private ValueEventListener eventListener;
    private DatabaseReference dbReference;

    private static String TAGLOG="firebase-db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        Toolbar topAppBar=  findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);

        dbReference= FirebaseDatabase.getInstance("https://bread4all-14e0d-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("Productos");

        RecyclerView rv = findViewById(R.id.rv);

        rv.setHasFixedSize(true);

        llm = new LinearLayoutManager(this);

        rv.setLayoutManager(llm);

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
            case R.id.metodo_pago:
                Snackbar.make(findViewById(R.id.topAppBar),"Has elegido metodo pago",Snackbar.LENGTH_SHORT).show();
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void inicializarProductos(){
        productos = new ArrayList<>();

        eventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int pos=0;

                while (dataSnapshot.child("Producto"+pos).exists()){
                    productos.add(new Producto(dataSnapshot.child("Nombre").getValue().toString(),(double)dataSnapshot.child("Precio").getValue(),(int)dataSnapshot.child("fotoId").getValue()));
                    pos++;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAGLOG,"Error",databaseError.toException());
            }
        };




    }

}
