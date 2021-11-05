package com.example.recicleview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    private LayoutInflater inflador;
    protected List<Puntuacion> puntuaciones;

    public RVAdapter(Context contexto, List<Puntuacion> puntuaciones) {
        this.inflador = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.puntuaciones = puntuaciones;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        TextView personName;
        TextView puntos;
        ImageView personPhoto;

        ViewHolder(View itemView){
            super(itemView);
            cv=(CardView) itemView.findViewById(R.id.cv);
            personName=(TextView) itemView.findViewById(R.id.puntos);
            puntos=(TextView) itemView.findViewById(R.id.puntos);
            personPhoto=(ImageView) itemView.findViewById(R.id.person_photo);
        }
    }
}
