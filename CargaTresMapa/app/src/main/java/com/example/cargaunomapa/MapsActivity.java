package com.example.cargaunomapa;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.libraries.maps.CameraUpdate;
import com.google.android.libraries.maps.CameraUpdateFactory;
import com.google.android.libraries.maps.GoogleMap;
import com.google.android.libraries.maps.OnMapReadyCallback;
import com.google.android.libraries.maps.SupportMapFragment;
import com.google.android.libraries.maps.model.BitmapDescriptorFactory;
import com.google.android.libraries.maps.model.CameraPosition;
import com.google.android.libraries.maps.model.LatLng;

import com.google.android.libraries.maps.model.Marker;
import com.google.android.libraries.maps.model.MarkerOptions;
import com.google.android.libraries.maps.model.PolylineOptions;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG="Error app mapas";

    LocationListener locListener;
    LocationManager locManager;
    int PETICION_PERMISOS=0;
    PolylineOptions linea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        locListener = new LocationListener() {

            public void onLocationChanged(Location location) {

                mostrarPosicion(location);

            }

            public void onProviderDisabled(String provider) {


            }

            public void onProviderEnabled(String provider) {


            }

            public void onStatusChanged(String provider, int status, Bundle extras) {



            }

        };
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng));
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                MarkerOptions options= new MarkerOptions().position(latLng)
                        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_sv_error_icon));         //Cambiar icono
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));  //Cambiar color
                mMap.addMarker(options);

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng position= marker.getPosition();
                String mostrar="Posicion: "+position.latitude+","+position.longitude;
                Toast.makeText(getApplicationContext(),mostrar,Toast.LENGTH_LONG).show();

                return false;
            }
        });
    }

    public void moverACiudadReal(View view){
        LatLng CiudadReal =new LatLng(38.985909009251955, -3.9273314158285726);
        CameraPosition camPar = new CameraPosition.Builder()
                .target(CiudadReal)
                .zoom(15)
                .bearing(45)    //rotacion
                .tilt(60)       //inclinacion
                .build();
        CameraUpdate camaraOpciones = CameraUpdateFactory.newCameraPosition(camPar);

        mMap.animateCamera(camaraOpciones);

        Marker marker= mMap.addMarker(new MarkerOptions().position(CiudadReal).title("Marker in Royal City"));

    }

    public void moverAMadrid(View view){
        LatLng Madrid =new LatLng(40.42637129872075, -3.702891515536429);
        CameraUpdate cameraOpciones= CameraUpdateFactory.newLatLng(Madrid);
        mMap.moveCamera(cameraOpciones);
        mMap.addMarker(new MarkerOptions().position(Madrid).title("Marcardor en Madrid"));

        dibujarRuta();
    }

    public void CambiarAHybrid(View view){
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

    public void dibujarRuta(){
        PolylineOptions linea= new PolylineOptions()
                .add(new LatLng(38.985909009251955, -3.9273314158285726))
                .add(new LatLng(40.42637129872075, -3.702891515536429));
        linea.width(7);
        linea.color(Color.RED);

        mMap.addPolyline(linea);


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

            linea= new PolylineOptions()
                    .add(new LatLng(loc.getLatitude(),loc.getLongitude()));
            linea.width(7);
            linea.color(Color.RED);

            mMap.addPolyline(linea);


        } else {

        }

    }

    public void comenzarPintar(View view){
        comenzarLocalizacion();
    }
}