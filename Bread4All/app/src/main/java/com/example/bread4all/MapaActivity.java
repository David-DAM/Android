package com.example.bread4all;

import androidx.annotation.DrawableRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import com.google.android.libraries.maps.model.BitmapDescriptor;
import com.google.android.libraries.maps.model.BitmapDescriptorFactory;
import com.google.android.libraries.maps.model.CameraPosition;
import com.google.android.libraries.maps.model.LatLng;

import com.google.android.libraries.maps.model.Marker;
import com.google.android.libraries.maps.model.MarkerOptions;
import com.google.android.libraries.maps.model.PolylineOptions;

public class MapaActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private static final String TAG = "Error app mapas";

    LocationListener locListener;
    LocationManager locManager;
    int PETICION_PERMISOS = 0;
    PolylineOptions linea;
    double latitudAntigua, longitudAntigua;
    String nombre,precio;
    boolean activado=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mapa);

        RellenarDatos();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PETICION_PERMISOS);

        }


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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng CiudadReal = new LatLng(38.985909009251955, -3.9273314158285726);

        addMarkers();

        CameraPosition camPar = new CameraPosition.Builder()
                .target(CiudadReal)
                .zoom(15)
                .build();
        CameraUpdate camaraOpciones = CameraUpdateFactory.newCameraPosition(camPar);

        mMap.animateCamera(camaraOpciones);

        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();
                addMarkers();
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng position = marker.getPosition();
                String mostrar = "Posicion: " + position.latitude + "," + position.longitude;
                Toast.makeText(getApplicationContext(), mostrar, Toast.LENGTH_LONG).show();

                return false;
            }
        });


    }

    private void RellenarDatos(){
        if(getIntent().hasExtra("nombre") && getIntent().hasExtra("precio")){

            nombre = getIntent().getStringExtra("nombre");
            precio = getIntent().getStringExtra("precio");
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void addMarkers(){
        LatLng tienda1=new LatLng(38.98341212498277, -3.9268962977584057);
        LatLng tienda2=new LatLng(38.98509574266197, -3.9232123488358983);
        LatLng tienda3=new LatLng(38.985462304277625, -3.927172972369847);
        LatLng tienda4=new LatLng(38.991506447337656, -3.9302606503879125);

        MarkerOptions options = new MarkerOptions()
                .title(nombre+" "+precio+" €")
                .icon(bitmapDescriptorFromVector(this,R.drawable.ic_bread_vector));

        mMap.addMarker((options).position(tienda1));
        mMap.addMarker((options).position(tienda2));
        mMap.addMarker((options).position(tienda3));
        mMap.addMarker((options).position(tienda4));
    }

    public void moverACiudadReal(View view) {
        LatLng CiudadReal = new LatLng(38.985909009251955, -3.9273314158285726);
        CameraPosition camPar = new CameraPosition.Builder()
                .target(CiudadReal)
                .zoom(15)
                .bearing(45)    //rotacion
                .tilt(60)       //inclinacion
                .build();
        CameraUpdate camaraOpciones = CameraUpdateFactory.newCameraPosition(camPar);

        mMap.animateCamera(camaraOpciones);

        Marker marker = mMap.addMarker(new MarkerOptions().position(CiudadReal).title("Marker in Royal City"));

    }

    public void CambiarAHybrid(View view) {
        if (!activado){
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            activado=true;
        }else {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            activado=false;
        }

    }


    private void comenzarLocalizacion() {
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mostrarPosicion(loc);

        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,locListener);
    }

    private void mostrarPosicion(Location loc) {

        if(loc != null) {
            LatLng pos =new LatLng(loc.getLatitude(),loc.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLng(pos));

            linea= new PolylineOptions()
                    .add(new LatLng(loc.getLatitude(),loc.getLongitude()))
                    .add(new LatLng(latitudAntigua,longitudAntigua));
            linea.width(7);
            linea.color(Color.RED);
            if (latitudAntigua!=0 && longitudAntigua!=0){
                mMap.addPolyline(linea);
            }
            latitudAntigua=loc.getLatitude();
            longitudAntigua=loc.getLongitude();

        }

    }

    public void comenzarLocalizacion(View view){
        comenzarLocalizacion();
    }
}
