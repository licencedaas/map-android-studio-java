package licence.daas.ubicacion;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    String log = "Fail", lat = "ubicacion";
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private TextView  l1,l2,l3,l4,l5,l6;

  //  Context contexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();

        l1 =(TextView) findViewById(R.id.menu_log);
        l2 =(TextView) findViewById(R.id.menu_log2);

        l3 =(TextView) findViewById(R.id.menu_log5);
        l4 =(TextView) findViewById(R.id.menu_log4);
        l5 =(TextView) findViewById(R.id.menu_log3);
        l6 =(TextView) findViewById(R.id.menu_log6);

   //--------- LLAMADO A UNA PUBLICIDAD---
   //     contexto = this;
   //     new module1(contexto);


        l1.setText("LATITUD");
        l2.setText("LOGITUD");

        locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
        if (locationResult == null) {return;}
        for (Location location : locationResult.getLocations()) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();


                    // Supongamos que tienes una LatLng llamada targetLatLng
        LatLng targetLatLng = new LatLng(latitude, longitude); // Cambia estas coordenadas por las que necesites
        
        GeoCoderTask geoCoderTask = new GeoCoderTask(MainActivity.this);
        geoCoderTask.execute(targetLatLng);

                    lat = String.valueOf(latitude);   log = String.valueOf(longitude);// se guarda para otros usos

                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(
                        location.getLatitude(), location.getLongitude(), 1);

                        if (!addresses.isEmpty()) {
                            String cityName = addresses.get(0).getLocality();
                       //------- Se imprime  la ciudad,el departamento, la direccion
                            l3.setText("cid: "+GeoCoderTask.valor1);
                            l4.setText("cid: "+GeoCoderTask.valor2);
                            l5.setText("cid: "+GeoCoderTask.valor3);
                            l6.setText("cid: "+GeoCoderTask.valor4);

                        } else {
                            //      Toast.makeText(this, "No se pudo obtener el nombre de la ciudad", Toast.LENGTH_SHORT).show();
                           // Toast.makeText(getApplicationContext(), "No se pudo obtener el nombre de la ciudad", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                  //  Toast.makeText(getApplicationContext(), "latidu: "+lat   +"    " + log, Toast.LENGTH_SHORT).show();
                        l1.setText("lat: "+latitude);
                        l2.setText("log: "+longitude);

                }}};

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {startLocationUpdates();} else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);}


    }


    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback,null);
    }


    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    public void mapa(View sdfsf){

        Intent siguiente = new Intent(this, MapsActivity2.class);
        siguiente.putExtra("lat", lat); //envio datos de lat y log obtenidos para la siguiente activity map
        siguiente.putExtra("log", log);
        startActivity(siguiente);


    }

      //-------- METODO QUE LLAMA A UN MENU PARA REGISTRARSE
    public void llamadaVs(View sdfsf){

        contexto = this;   
        new registroMarca(contexto);

    }
}