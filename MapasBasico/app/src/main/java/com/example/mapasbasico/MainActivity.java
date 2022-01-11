package com.example.mapasbasico;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button btnDesactivar, btnArrancar;
    TextView lblEstado, lblLatitud, lblLongitud, lblPrecision;
    LocationListener locListener;
    LocationManager locManager;
    int PETICION_PERMISOS=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDesactivar=findViewById(R.id.btnDesactivar);
        btnArrancar=findViewById(R.id.btnArrancar);
        lblEstado=findViewById(R.id.lblEstado);
        lblLatitud=findViewById(R.id.lblLatitud);
        lblLongitud=findViewById(R.id.lblLongitud);
        lblPrecision=findViewById(R.id.lblPrecision);

        locListener = new LocationListener() {

            public void onLocationChanged(Location location) {

                mostrarPosicion(location);

            }

            public void onProviderDisabled(String provider) {
                lblEstado.setText("Provider OFF");

            }

            public void onProviderEnabled(String provider) {
                lblEstado.setText("Provider ON");

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {

                lblEstado.setText("Provider Status: " + status);

            }

        };

        btnArrancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comenzarLocalizacion();
            }
        });

        btnDesactivar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                locManager.removeUpdates(locListener);

            }

        });
    }

    private void comenzarLocalizacion() {
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    PETICION_PERMISOS);

        }
        Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mostrarPosicion(loc);

        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,locListener);

    }

    private void mostrarPosicion(Location loc) {

        if(loc != null) {

            lblLatitud.setText("Latitud: " + String.valueOf(loc.getLatitude()));
            lblLongitud.setText("Longitud:" + String.valueOf(loc.getLongitude()));
            lblPrecision.setText("Precision: " + String.valueOf(loc.getAccuracy()));

        } else {

            lblLatitud.setText("Latitud: (sin_datos)");
            lblLongitud.setText("Longitud: (sin_datos)");
            lblPrecision.setText("Precision: (sin_datos)");

        }

    };
}