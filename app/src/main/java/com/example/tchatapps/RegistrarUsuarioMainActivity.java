package com.example.tchatapps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegistrarUsuarioMainActivity extends AppCompatActivity {

    EditText nombre, apellido, correoElectronico, sexo, password,password2;
    Button buttonRegistrar;
    RadioGroup radioGroup;
    RadioButton radioButtonHombre, radioButtonMujer;

    private FirebaseAuth mAuth;
    static DatabaseReference reference;
    ProgressDialog progressD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario_main);

        nombre = findViewById(R.id.id_nombre);
        apellido = findViewById(R.id.id_apellido);
        correoElectronico = findViewById(R.id.idCorreo);
        radioGroup = findViewById(R.id.id_radiobuttonData);
        radioButtonHombre = findViewById(R.id.id_sexo_hombre);
        radioButtonMujer = findViewById(R.id.id_sexo_mujer);
        password = findViewById(R.id.id_password);
        password2 = findViewById(R.id.idPasswordS);
        buttonRegistrar = findViewById(R.id.buttonRegistrarUsuario);


        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        buttonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressD = new ProgressDialog(RegistrarUsuarioMainActivity.this);
                progressD.setMessage("Procesando el registro...");
                progressD.show();
                String sexo = "";
                String Nombre = nombre.getText().toString();
                String Apellido = apellido.getText().toString();
                String correo = correoElectronico.getText().toString();
                String Password = password.getText().toString();
                String Password2 = password2.getText().toString();

//                if (!TextUtils.isEmpty(Nombre) || !TextUtils.isEmpty(Apellido) || !TextUtils.isEmpty(correo) || !TextUtils.isEmpty(Password)) {
//                  progressD.dismiss();
//                    Toast.makeText(RegistrarUsuarioMainActivity.this, "Todos los campos son requeridos. ", Toast.LENGTH_SHORT).show();
//
//                }
                if ( Password.length() < 6) {
                    progressD.dismiss();
                    Toast.makeText(getApplicationContext(), "La contraseña debe ser mayor de 6 caractereres", Toast.LENGTH_SHORT).show();
                }



                if(radioButtonHombre.isChecked())
                    sexo = "H";
                else
                    sexo = "M";

                if ( Password.equals(Password2) && Password.length() >= 6) {

                    registrarUsuario(Nombre, Apellido, correo, sexo, Password);
                }
            }

        });
    }


    private void registrarUsuario(final String nombre, final String apellido, final String correo, final String sexo, final String password){
        mAuth.createUserWithEmailAndPassword(correo, password)
                .addOnCompleteListener(RegistrarUsuarioMainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            String userId = firebaseUser.getUid();

                            reference  = FirebaseDatabase.getInstance().getReference().child("Users")
                                    .child(userId);

                            Map<String,Object> map = new HashMap<>();
                            map.put("id", userId);
                            map.put("nombre", nombre);
                            map.put("apellido", apellido);
                            map.put("correo", correo);
                            map.put("sexo", sexo);
                            map.put("password",password);
                           // map.put("bio","");
                            map.put("imageURL","gs://tchat-bb4c3.appspot.com/image_prueba.jpg");
                            reference.child("Users").child(userId).setValue(map)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task2) {

                                    if(task2.isSuccessful()){
                                        progressD.dismiss();
                                        Intent intent = new Intent(RegistrarUsuarioMainActivity.this, LoginMainActivity.class);
                                        finish();

                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                        else{
                            progressD.dismiss();
                            Toast.makeText(getApplicationContext(),"No puedes registrarte con este correo o contraseña", Toast.LENGTH_SHORT);
                        }
                    }
                });
    }
}