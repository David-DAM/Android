package com.example.bread4all;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//Tenemos que extender de RecyclerView.Adapter e implementar la clase RVAdapter.ViewHolder
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    //Inflador para inflar las vistas individualmente
    private LayoutInflater inflador;
    //Estructura para almacenar los datos a mostrar
    protected List<Producto> productos;

    SQLiteDatabase bbdd;
    bbddRecientes conexion;




    //Constructor en el que cargamos los datos a visualizar
    //Inicializamos el inflador en el contexto de la activity
    RVAdapter(Context contexto, List<Producto> productos) {
        inflador = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.productos = productos;
        //Conexion con la base de datos de SQLite
        conexion=new bbddRecientes(contexto,"bbddRecientes",null,1);

        bbdd=conexion.getWritableDatabase();

    }


    //Reciclado de vistas
    //Especificamos los elementos que queremos personalizar. Los elementos fijos no se indicarían
    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView nombre;
        TextView precio;
        ImageView foto_producto;

        //Recibimos una vista con lo que contiene
        ViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            nombre = (TextView) itemView.findViewById(R.id.nombre_producto);
            precio = (TextView) itemView.findViewById(R.id.precio);
            foto_producto = (ImageView) itemView.findViewById(R.id.foto_producto);
        }

    }

    //CREAMOS LA VISTA SIN PERSONALIZAR CON DATOS
    //Ya devuelve un ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = inflador.inflate(R.layout.producto_individual, null);

        ViewHolder pvh = new ViewHolder(v);

        return pvh;
    }

    //RELLENAR LOS DATOS DEL VIEWHOLDER
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        final int posicion = i;

        viewHolder.nombre.setText(productos.get(posicion).nombre);
        viewHolder.precio.setText(String.valueOf(productos.get(posicion).precio));
        viewHolder.foto_producto.setImageResource(productos.get(posicion).fotoId);

        //Asignamos un listener
        viewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bbdd!=null){

                    String sql=("INSERT INTO RECIENTES VALUES(?,?,?)");
                    SQLiteStatement statement =bbdd.compileStatement(sql);

                    statement.clearBindings();

                    statement.bindString(1,productos.get(posicion).nombre);
                    statement.bindDouble(2,productos.get(posicion).precio);
                    statement.bindLong(3,productos.get(posicion).fotoId);

                    long rowId= statement.executeInsert();

                }
            }
        });

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

    //INDICAMOS EL NÚMERO DE ELEMENTOS A VISUALIZAR
    @Override
    public int getItemCount() {
        return productos.size();
    }
}
