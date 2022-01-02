package com.example.bread4all;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceManager;
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

    private Context mContext;

    //Constructor en el que cargamos los datos a visualizar
    //Inicializamos el inflador en el contexto de la activity
    RVAdapter(Context contexto, List<Producto> productos) {
        inflador = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.productos = productos;
        //Conexion con la base de datos de SQLite
        conexion=new bbddRecientes(contexto,"bbddRecientes",null,1);
        bbdd=conexion.getWritableDatabase();

        mContext=contexto;

    }


    //Reciclado de vistas
    //Especificamos los elementos que queremos personalizar. Los elementos fijos no se indicarían
    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView nombre;
        TextView precio;
        TextView moneda;
        ImageView foto_producto;

        //Recibimos una vista con lo que contiene
        ViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            nombre = (TextView) itemView.findViewById(R.id.nombre_producto);
            precio = (TextView) itemView.findViewById(R.id.precio);
            moneda=itemView.findViewById(R.id.moneda);
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
    public void onBindViewHolder(ViewHolder viewHolder, int i)  {

        final int posicion = i;

        viewHolder.nombre.setText(productos.get(posicion).nombre);
        viewHolder.precio.setText(String.valueOf(productos.get(posicion).precio));
        viewHolder.foto_producto.setImageResource(productos.get(posicion).fotoId);
        viewHolder.moneda.setText(loadPref());

        //Asignamos un listener
        viewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pref=0;

                if (bbdd!=null && !inicializados(posicion)){

                    String sql=("INSERT INTO RECIENTES VALUES(?,?,?)");
                    SQLiteStatement statement =bbdd.compileStatement(sql);
                    statement.clearBindings();
                    /*
                    Cursor c1= bbdd.rawQuery("SELECT count() from recientes",null);

                    if (c1.moveToFirst()){

                        pref=c1.getInt(0);

                    }

                     */

                    statement.bindString(1,productos.get(posicion).nombre);
                    statement.bindDouble(2,productos.get(posicion).precio);
                    statement.bindLong(3,productos.get(posicion).fotoId);
                    //statement.bindLong(4,pref);

                    long rowId= statement.executeInsert();
                }

                Intent producto=new Intent(mContext,ProductoActivity.class);
                producto.putExtra("nombre",productos.get(posicion).nombre);
                producto.putExtra("precio",productos.get(posicion).precio);
                mContext.startActivity(producto);

            }
        });

    }

    //Metodo que comprueba si ya se han insertado los datos previamente
    public boolean inicializados(int posicion){
        boolean res=false;
        String [] camposMostrar= new String[]{"nombre"};

        Cursor c1= bbdd.query("recientes",camposMostrar,null,null,null,null,null);

        if (c1.moveToFirst()){
            do {
                if(c1.getString(0).equals(productos.get(posicion).nombre)){
                    res=true;
                }
            }while (c1.moveToNext());
        }

        return res;
    }
    //Carga las preferencias del tipo de moneda
    public String loadPref(){
        SharedPreferences mySharedPreferences= PreferenceManager.getDefaultSharedPreferences(mContext);

        String monedaPreference;

        monedaPreference= mySharedPreferences.getString("moneda","euros");

        return monedaPreference;
    }

    //INDICAMOS EL NÚMERO DE ELEMENTOS A VISUALIZAR
    @Override
    public int getItemCount() {
        return productos.size();
    }
}
