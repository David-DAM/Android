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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    JsonObjectRequest jsonObjectRequest;
    RequestQueue request;

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
        request= Volley.newRequestQueue(getApplicationContext());

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
                String latitudOrigen=String.valueOf(latitudAntigua);
                String longitudOrigen=String.valueOf(longitudAntigua);
                String latitudFinal=String.valueOf(position.latitude);
                String longitudFinal=String.valueOf(position.longitude);

                Utilidades.coordenadas.setLatitudInicial(Double.valueOf(latitudOrigen));
                Utilidades.coordenadas.setLongitudInicial(Double.valueOf(longitudOrigen));
                Utilidades.coordenadas.setLatitudFinal(Double.valueOf(latitudFinal));
                Utilidades.coordenadas.setLongitudFinal(Double.valueOf(longitudFinal));

                webServiceObtenerRuta(latitudOrigen,longitudOrigen,latitudFinal,longitudFinal);

                LatLng center = null;
                ArrayList<LatLng> points = null;
                PolylineOptions lineOptions = null;

                // recorriendo todas las rutas
                for(int i=0;i<Utilidades.routes.size();i++){
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Obteniendo el detalle de la ruta
                    List<HashMap<String, String>> path = Utilidades.routes.get(i);

                    // Obteniendo todos los puntos y/o coordenadas de la ruta
                    for(int j=0;j<path.size();j++){
                        HashMap<String,String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng pos = new LatLng(lat, lng);

                        if (center == null) {
                            //Obtengo la 1ra coordenada para centrar el mapa en la misma.
                            center = new LatLng(lat, lng);
                        }
                        points.add(pos);
                    }

                    // Agregamos todos los puntos en la ruta al objeto LineOptions
                    lineOptions.addAll(points);
                    //Definimos el grosor de las Polilíneas
                    lineOptions.width(2);
                    //Definimos el color de la Polilíneas
                    lineOptions.color(Color.BLUE);
                }

                // Dibujamos las Polilineas en el Google Map para cada ruta
                mMap.addPolyline(lineOptions);

                LatLng origen = new LatLng(Utilidades.coordenadas.getLatitudInicial(), Utilidades.coordenadas.getLongitudInicial());
                mMap.addMarker(new MarkerOptions().position(origen));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 15));

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
        latitudAntigua=loc.getLatitude();
        longitudAntigua=loc.getLongitude();

        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,locListener);
    }

    private void mostrarPosicion(Location loc) {

        if(loc != null) {
            LatLng pos =new LatLng(loc.getLatitude(),loc.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLng(pos));
            /*
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

             */
        }

    }

    public void comenzarLocalizacion(View view){
        comenzarLocalizacion();
    }

    private void webServiceObtenerRuta(String latitudInicial, String longitudInicial, String latitudFinal, String longitudFinal) {

        String url="https://maps.googleapis.com/maps/api/directions/json?origin="+latitudInicial+","+longitudInicial
                +"&destination="+latitudFinal+","+longitudFinal;

        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Este método PARSEA el JSONObject que retorna del API de Rutas de Google devolviendo
                //una lista del lista de HashMap Strings con el listado de Coordenadas de Lat y Long,
                //con la cual se podrá dibujar pollinas que describan la ruta entre 2 puntos.
                JSONArray jRoutes = null;
                JSONArray jLegs = null;
                JSONArray jSteps = null;

                try {

                    jRoutes = response.getJSONArray("routes");

                    /** Traversing all routes */
                    for(int i=0;i<jRoutes.length();i++){
                        jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                        List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                        /** Traversing all legs */
                        for(int j=0;j<jLegs.length();j++){
                            jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                            /** Traversing all steps */
                            for(int k=0;k<jSteps.length();k++){
                                String polyline = "";
                                polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                                List<LatLng> list = decodePoly(polyline);

                                /** Traversing all points */
                                for(int l=0;l<list.size();l++){
                                    HashMap<String, String> hm = new HashMap<String, String>();
                                    hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                                    hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                                    path.add(hm);
                                }
                            }
                            Utilidades.routes.add(path);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (Exception e){
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "No se puede conectar "+error.toString(), Toast.LENGTH_LONG).show();
                System.out.println();
                Log.d("ERROR: ", error.toString());
            }
        }
        );

        request.add(jsonObjectRequest);
    }

    public List<List<HashMap<String,String>>> parse(JSONObject jObject){
        //Este método PARSEA el JSONObject que retorna del API de Rutas de Google devolviendo
        //una lista del lista de HashMap Strings con el listado de Coordenadas de Lat y Long,
        //con la cual se podrá dibujar pollinas que describan la ruta entre 2 puntos.
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    Utilidades.routes.add(path);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }
        return Utilidades.routes;
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}
