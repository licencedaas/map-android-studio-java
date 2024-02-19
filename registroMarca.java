package licence.daas.ubicacion;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class registroMarca {

    EditText  marca, nombre, apellido, celular, gmail, pass;
    FirebaseFirestore db ;
    Map<String,Object> user = new HashMap<>();
    contesto dato = new contesto();
    RadioButton r1;

    public registroMarca(Context context){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.activity_registro_marca);

        ImageButton cerrar = (ImageButton) dialog.findViewById(R.id.cerrar_registro);
        r1 = (RadioButton) dialog.findViewById(R.id.terminos_1);//terminos_1
       db =  FirebaseFirestore.getInstance();

          marca =(EditText)dialog.findViewById(R.id.nombre_marca_registro);
          nombre  =(EditText)dialog.findViewById(R.id.nombre);
          apellido  =(EditText)dialog.findViewById(R.id.apellido);
          celular  =(EditText)dialog.findViewById(R.id.celular);
          pass  =(EditText)dialog.findViewById(R.id.pass);
          gmail  =(EditText)dialog.findViewById(R.id.gmail);


        r1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (r1.isChecked()) {


                    if (marca.getText().toString().equals("")&&nombre.getText().toString().equals("")&&apellido.getText().toString().equals("")
                    &&celular.getText().toString().equals("")&&pass.getText().toString().equals("")&&gmail.getText().toString().equals("")) {
                        Toast.makeText(context, "ES OBLIGATORIO LOS CAMOPOS  DE REGISTRO", Toast.LENGTH_SHORT).show();
                    }else{

                        dato.setKeyMarca(marca.getText().toString());
                        dato.setKeynombre(nombre.getText().toString());
                        dato.setKeyapellido(apellido.getText().toString());
                        dato.setKeyCelular(celular.getText().toString());
                        dato.setKeyGmail(gmail.getText().toString());
                        dato.setKeyPass(pass.getText().toString());


                    user.put(dato.getKeyLatitud(), dato.getKeyLogitud());
                    db.collection("cordenadas").document("ubicacion").update(user);

                        user.put("marca", dato.getKeyMarca());
                        user.put("nombre", dato.getKeynombre());
                        user.put("apellido", dato.getKeyapellido());
                        user.put("celular", dato.getKeyCelular());
                        user.put("gamil", dato.getKeyGmail());
                        user.put("pass", dato.getKeyPass());
                        user.put("lat", dato.getKeyLatitud());
                        user.put("log", dato.getKeyLogitud());


                    db.collection("BDmarca").document(dato.getKeyMarca()).update(user);
                    Toast.makeText(context, "DATOS ALMACENADOS", Toast.LENGTH_SHORT).show();

                    marca.setText("");
                }
            }



            }
        });



        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        dialog.show();


    }



    public  void registro(){



    }

}
