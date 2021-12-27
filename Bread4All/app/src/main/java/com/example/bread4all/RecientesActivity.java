package com.example.bread4all;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class RecientesActivity extends AppCompatActivity {

    SQLiteDatabase bbdd;
    bbddRecientes conexion;

    private RecyclerView rv;
    private List<Producto> productos;
    private RecyclerView.LayoutManager llm;
    private RVAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recientes);


        //Conexion con la base de datos de SQLite
        conexion=new bbddRecientes(this,"bbddRecientes",null,1);

        bbdd=conexion.getWritableDatabase();

        rv = findViewById(R.id.rv);

        rv.setHasFixedSize(true);

        llm = new LinearLayoutManager(this);

        rv.setLayoutManager(llm);

        inicializar();

        adapter = new RVAdapter(this, productos);

        rv.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                // this method is called
                // when the item is moved.
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // this method is called when we swipe our item to right direction.
                // on below line we are getting the item at a particular position.
                Producto deletedCourse = productos.get(viewHolder.getAdapterPosition());

                // below line is to get the position
                // of the item at that position.
                int position = viewHolder.getAdapterPosition();

                // this method is called when item is swiped.
                // below line is to remove item from our array list.
                productos.remove(viewHolder.getAdapterPosition());

                String sql=("DELETE FROM RECIENTES WHERE NOMBRE=?");
                SQLiteStatement statement =bbdd.compileStatement(sql);

                statement.clearBindings();

                statement.bindString(1,deletedCourse.nombre);

                long rowId= statement.executeInsert();

                // below line is to notify our item is removed from adapter.
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());

                // below line is to display our snackbar with action.
                Snackbar.make(rv, deletedCourse.nombre, Snackbar.LENGTH_LONG).setAction("DESHACER", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // adding on click listener to our action of snack bar.
                        // below line is to add our item to array list with a position.
                        productos.add(position, deletedCourse);

                        String sql=("INSERT INTO RECIENTES VALUES(?,?,?)");
                        SQLiteStatement statement =bbdd.compileStatement(sql);

                        statement.clearBindings();

                        statement.bindString(1,deletedCourse.nombre);
                        statement.bindDouble(2,deletedCourse.precio);
                        statement.bindLong(3,R.drawable.ic_historial);

                        long rowId= statement.executeInsert();

                        // below line is to notify item is
                        // added to our adapter class.
                        adapter.notifyItemInserted(position);
                    }
                }).show();
            }
            // at last we are adding this
            // to our recycler view.
        }).attachToRecyclerView(rv);

    }

    public void inicializar(){
        productos=new ArrayList<>();
        String [] camposMostrar= new String[]{"nombre","precio"};

        if (bbdd!=null){
            Cursor c1= bbdd.query("recientes",camposMostrar,null,null,null,null,null);

            if (c1.moveToFirst()){
                do {
                    productos.add(new Producto(c1.getString(0),c1.getDouble(1),R.drawable.ic_historial));

                }while (c1.moveToNext());
            }
        }



    }
}
