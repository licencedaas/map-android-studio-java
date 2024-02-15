package licence.daas.ubicacion;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeoCoderTask extends AsyncTask<LatLng, Void, String> {

    private Context context;
    public static  String  direccion="ZONA", valor1,valor2,valor3,valor4;
    private int contador=0;

    public GeoCoderTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(LatLng... latLngs) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        LatLng latLng = latLngs[0];
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                // Obtén la dirección completa
                return address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            direccion = String.valueOf(result);

            String[] valores = direccion.split(",");

            // Imprimir cada valor en una nueva línea
            for (String valor : valores) {
            contador++;
              if(contador==1){
                  valor1 = valor;
              }
                if(contador==2){
                    valor2 = valor;
                }

                if(contador==3){
                    valor3 = valor;
                }

                if(contador==4){
                    valor4 = valor;
                }


            }

        } else {
            Toast.makeText(context, "No se pudo obtener la dirección", Toast.LENGTH_LONG).show();
        }
    }
}

