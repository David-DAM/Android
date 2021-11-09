package com.example.recicleview;

import android.content.Context;
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
    protected List<Puntuacion> puntuaciones;


    //Constructor en el que cargamos los datos a visualizar
    //e inicializamos el inflador en el contexto de la activity
    RVAdapter(Context contexto, List<Puntuacion> puntuaciones) {
        inflador = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.puntuaciones = puntuaciones;
    }


    //Reciclado de vistas
    //Especificamos los elementos que queremos personalizar. Los elementos fijos no se indicarían
    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView personName;
        TextView puntos;
        ImageView personPhoto;

        //Recibimos una vista con lo que contiene
        ViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            personName = (TextView) itemView.findViewById(R.id.person_name);
            puntos = (TextView) itemView.findViewById(R.id.puntos);
            personPhoto = (ImageView) itemView.findViewById(R.id.person_photo);
        }
    }

    //1º CREAMOS LA VISTA SIN PERSONALIZAR CON DATOS
    //Ya devuelve un ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = inflador.inflate(R.layout.elemento_individual, null);

        ViewHolder pvh = new ViewHolder(v);

        return pvh;
    }

    //2º RELLENAR LOS DATOS DEL VIEWHOLDER
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        final int posicion = i;

        viewHolder.personName.setText(puntuaciones.get(posicion).name);
        viewHolder.puntos.setText(String.valueOf(puntuaciones.get(posicion).puntos));
        viewHolder.personPhoto.setImageResource(puntuaciones.get(posicion).photoId);

        //Asignamos un listener
        viewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(view.getContext(), puntuaciones.get(posicion).name, Toast.LENGTH_SHORT).show();

            }
        });

    }

    //3º INDICAMOS EL NÚMERO DE ELEMENTOS A VISUALIZAR
    @Override
    public int getItemCount() {
        return puntuaciones.size();
    }
}