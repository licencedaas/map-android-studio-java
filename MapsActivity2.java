package licence.daas.ubicacion;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import licence.daas.ubicacion.databinding.ActivityMaps2Binding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback,   GoogleMap.OnMapClickListener{

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private ActivityMaps2Binding binding;
    private String log = "", lat = "";
    private  String destino_lat = "", destino_log = "";
    private static ApiService apiService;
    private static final String BASE_URL = "https://api.openrouteservice.org/v2/directions/driving-car?api_key=5b3ce3597851110001cf6248002e5e7dd85a4bef9d7318a4e93d4bd0&start=8.681495,49.41461&end=8.687872,49.420318"; // Reemplaza esto con la URL de tu servidor

    private  int contadorZona =0;
    private static Retrofit retrofit;
    private LatLng myLocation;
    private LatLng destination;
    contesto val = new contesto();

    Polyline polyline=null;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMaps2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        lat = getIntent().getStringExtra("lat");
        log = getIntent().getStringExtra("log");
        val.setKeyUbicacionZonas(getIntent().getStringExtra("zona"));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        this.mMap.setOnMapClickListener(this);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(Double.parseDouble(lat),Double.parseDouble(log));
        mMap.addMarker(new MarkerOptions().position(sydney).title("UBICACION"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        //------------------------------------------------

        // Verificar permisos de ubicación
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Habilitar botón para mostrar la ubicación actual del usuario
        mMap.setMyLocationEnabled(true);
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
                    }
                });


        //-------------------------------




        contesto val = new contesto();

        String[] valores = val.getKeyUbicacionZonas().split( ",");
        for (String valor : valores) {


            contadorZona++;

            if(contadorZona==1){
                Toast.makeText(getApplicationContext(), contadorZona+" - ALTITUD_1: " + valor, Toast.LENGTH_SHORT).show();
             destino_lat = valor;

            }

            if(contadorZona==2){
                contadorZona=0;
                destino_log = valor;
                Toast.makeText(getApplicationContext(), contadorZona + " - LONGITUD_2: " + valor, Toast.LENGTH_SHORT).show();

                LatLng sydney1 = new LatLng(Double.parseDouble(destino_lat),Double.parseDouble(destino_log));

                mMap.addMarker(new MarkerOptions().position(sydney1).title("UBICACION"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney1));
            }
        }

    }




    public void  starMap() {

            try {
                ApiService apiService = ApiClient.getApiService();
                Call<RutaResponse> call =
 apiService.obtenerRuta("5b3ce3597851110001cf6248002e5e7dd85a4bef9d7318a4e93d4bd0",lat,log,destino_lat,destino_log);
         call.enqueue(new Callback<RutaResponse>() {
                    @Override
                    public void onResponse(Call<RutaResponse> call, Response<RutaResponse> response) {
                        if (response.isSuccessful()) {
                            RutaResponse rutaResponse = response.body();
                            // Utiliza la información de la ruta para trazarla en tu mapa

                            PolygonOptions polygonOptions;

                            // Verificamos si hay al menos una característica en la respuesta de la ruta
                            if (rutaResponse.getFeatures() != null && !rutaResponse.getFeatures().isEmpty()) {
                                // Accedemos al primer elemento de la lista de características (Feature) de rutaResponse
                                Feature firstFeature = rutaResponse.getFeatures().get(0);

                                // Verificamos si la geometría (geometry) está presente en el primer feature
                                if (firstFeature.getGeometries() != null && !firstFeature.getGeometries().isEmpty()) {
                                    // Obtenemos las coordenadas (coordinates) de la geometría del primer feature
                                    List<List<Double>> coordinates = firstFeature.getGeometries().get(0).getCoordinates();

                                    // Creamos un objeto PolylineOptions para almacenar los puntos del polígono
                                    PolylineOptions polyLineOptions = new PolylineOptions();

                                    // Iteramos sobre las coordenadas de la geometría del primer feature
                                    for (List<Double> coordinate : coordinates) {
                                        // Extraemos las coordenadas de latitud y longitud
                                        double latitude = coordinate.get(1);
                                        double longitude = coordinate.get(0);

                                        // Creamos un objeto LatLng con las coordenadas y lo agregamos al PolylineOptions
                                        polyLineOptions.add(new LatLng(latitude, longitude));
                                    }

                                    // Ejecutamos la operación para agregar la polilínea en el hilo principal (UI thread)
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Agregamos la polilínea al mapa

                                      polyline = mMap.addPolyline(polyLineOptions);
                                        }
                                    });
                                }else{
                                    Toast.makeText(MapsActivity2.this, "NI HAY RESULTADOS 2", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(MapsActivity2.this, "NO HAY RESULTADOS 1", Toast.LENGTH_SHORT).show();
                            }





                        } else {
                            // Manejar la respuesta de error aquí
                        }
                    }

                    @Override
                    public void onFailure(Call<RutaResponse> call, Throwable t) {
                        // Manejar el fallo de la llamada aquí
                    }
                });



            }catch (Exception sdfs){
                Toast.makeText(this, "ERRRO: "+ sdfs, Toast.LENGTH_SHORT).show();
            }


    }


    @Override
    public void onMapClick(@NonNull LatLng latLng) {

        LatLng sydney1 = new LatLng(latLng.latitude,latLng.longitude);
        mMap.addMarker(new MarkerOptions().position(sydney1).title("DESTINO"));


        Toast.makeText(this, "EJECUTANDO  MAP", Toast.LENGTH_SHORT).show();
      //  starMap();
    }
}



