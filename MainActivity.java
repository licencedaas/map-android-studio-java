package licence.daas.ubicacion;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    String info_1,info_2,info_3,info_4;
    double latitude , longitude;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private TextView  l1,l2,l3,l4,l5,l6;
    private int contadorGeo=0;

    private FirebaseFirestore bd;
    Context contexto;
    contesto dato = new contesto();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.activity_main);

        dialog. getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        if (Build.VERSION.SDK_INT > 16) {
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();

        l1 =(TextView) findViewById(R.id.menu_log);
        l2 =(TextView) findViewById(R.id.menu_log2);

        l3 =(TextView) findViewById(R.id.menu_log5);
        l4 =(TextView) findViewById(R.id.menu_log4);
        l5 =(TextView) findViewById(R.id.menu_log3);
        l6 =(TextView) findViewById(R.id.menu_log6);

 bd = FirebaseFirestore.getInstance();

        contexto = this;
        new module1(contexto);


        l1.setText("LATITUD");
        l2.setText("LOGITUD");



        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {return;}
                for (Location location : locationResult.getLocations()) {



                latitude = location.getLatitude();
                longitude = location.getLongitude();


                // Supongamos que tienes una LatLng llamada targetLatLng
                LatLng targetLatLng = new LatLng(latitude, longitude); // Cambia estas coordenadas por las que necesites

                GeoCoderTask geoCoderTask = new GeoCoderTask(MainActivity.this);
                geoCoderTask.execute(targetLatLng);

                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(
                            location.getLatitude(), location.getLongitude(), 1);

                    if (!addresses.isEmpty()) {
                        String cityName = addresses.get(0).getLocality();

                        info_1 = GeoCoderTask.valor1;
                        info_2 = GeoCoderTask.valor2;
                        info_3 = GeoCoderTask.valor3;
                        info_4 = GeoCoderTask.valor4;

                        try {
                            if (info_1.equals("null")) {
                                l3.setText("Calculando...");
                                l4.setText("Calculando...");
                                l5.setText("Calculando..");
                                l6.setText("Calculando..");
                            }else{
                                l3.setText("cid: " + info_1);
                                l4.setText("cid: " + info_2);
                                l5.setText("cid: " + info_3);
                                l6.setText("cid: " + GeoCoderTask.valor4);contadorGeo=1;
                            }
                        }catch (Exception DF){
                            l3.setText("Calculando...");
                            l4.setText("Calculando...");
                            l5.setText("Calculando..");
                            l6.setText("Calculando..");}

                //        Toast.makeText(getApplicationContext(), "latidu: "+GeoCoderTask.valor2 , Toast.LENGTH_SHORT).show();
                    } else {
                        //      Toast.makeText(this, "No se pudo obtener el nombre de la ciudad", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "No se pudo obtener el nombre de la ciudad", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //  Toast.makeText(getApplicationContext(), "latidu: "+lat   +"    " + log, Toast.LENGTH_SHORT).show();
                l1.setText("lat: " + latitude);
                l2.setText("log: " + longitude);

                    dato.setKeyLatitud(String.valueOf(latitude));
                    dato.setKeyLogitud(String.valueOf(longitude));

                }


            }};

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {startLocationUpdates();} else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);}


    }


    private void startLocationUpdates() {
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
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                null);
    }


    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }



    // Source can be CACHE, SERVER, or DEFAULT.
    Source source = Source.CACHE;


    public void id(View fdf){

        Toast.makeText(contexto, "CARGANDO ZONAS....", Toast.LENGTH_SHORT).show();
        try{
            DocumentReference docRef1 =  bd.collection("cordenadas").document("ubicacion");
            docRef1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                           // actualValues = document.getData().keySet();// traspaso el vector de datos a uno que si pueda manegar

                            String data = String.valueOf(document.getData()).replace("{","").replace("}","");
                            String data1= data.replace("=",",");

                            contesto val = new contesto();

                            val.setKeyUbicacionZonas(data1);

                            Toast.makeText(getApplicationContext(), "ZONA: "+ val.getKeyUbicacionZonas(), Toast.LENGTH_SHORT).show();
                            Intent siguiente = new Intent(getApplicationContext(), MapsActivity2.class);
                            siguiente.putExtra("lat", String.valueOf(latitude));
                            siguiente.putExtra("log", String.valueOf(longitude));
                            siguiente.putExtra("zona",val.getKeyUbicacionZonas() );
                            startActivity(siguiente);



                        }}}});}catch (Exception dsfs){}

    }





    public void cargaDatos(View sdfds){

        DocumentReference docRef = bd.collection("cordenadas").document("ubicacion");
        docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                   //     Log.d(TAG, "DocumentSnapshot data: " + document.getData());


                        Toast.makeText(getApplicationContext(), "DATOS BD: " + document.getId(), Toast.LENGTH_SHORT).show();

                    } else {
                     //   Log.d(TAG, "No such document");
                    }
                } else {
                   // Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    public void llamadaVs(View sdfsf){

        contexto = this;
        new registroMarca(contexto);

    }
}


/*

 bdNeutral.collection("BD" + val.getKeyEscaneoGrenio())
                            .whereEqualTo("dimencion", val.getKeygrupoAleatorio_1()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    idV4.add(document.getString("nick"));
                                }
                                Toast.makeText(getApplicationContext(), verdad+ "]CONTENIDO:  " + idV4, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


 */