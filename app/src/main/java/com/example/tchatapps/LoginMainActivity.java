package com.example.tchatapps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tchatapps.fragment.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginMainActivity extends AppCompatActivity  {

    ImageView image;
    EditText usuario, password;
    Button buttonLogin, buttonRegistrar;
    private FirebaseAuth mAuth;
    DatabaseReference reference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);

        mAuth = FirebaseAuth.getInstance();

        image = (ImageView) findViewById(R.id.idImage);
        image.setImageResource(R.drawable.image_login);

        usuario = (EditText)findViewById(R.id.idCorreoElectronico);
        password = (EditText)findViewById(R.id.idPassword);

        buttonLogin = (Button)findViewById(R.id.idInicioSesionButton);
        buttonRegistrar = (Button)findViewById(R.id.idRegistrarButton);


//        buttonLogin.setOnClickListener(this);
//        buttonRegistrar.setOnClickListener(this);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog   pd = new ProgressDialog(LoginMainActivity.this);
                pd.setMessage("Iniciando Sesion");
                pd.show();

               try {

                   String usuarioUser = usuario.getText().toString();
                   String passwordUser = password.getText().toString();

                   if(TextUtils.isEmpty(usuarioUser) || TextUtils.isEmpty(passwordUser)){
                       Toast.makeText(getApplicationContext(), "todos los campos son requeridos",Toast.LENGTH_SHORT).show();
                   }
                   else{
                       mAuth.signInWithEmailAndPassword(usuarioUser, passwordUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {
                               if(task.isSuccessful()){
                                   reference= FirebaseDatabase.getInstance().getReference().child("Users")
                                           .child(mAuth.getCurrentUser().getUid());

                                   reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        pd.dismiss();
                                        Intent intent = new Intent(LoginMainActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                            pd.dismiss();
                                    }
                                });
                               }
                               else{
                                   pd.dismiss();
                                   Toast.makeText(LoginMainActivity.this,"Inicio de sesion fallido ", Toast.LENGTH_SHORT).show();
                               }
                           }
                       });


                   }


                iniciarSesion(view);


               }catch(Exception error){
                   pd.dismiss();
                   Toast.makeText(getApplicationContext(), "Ha ocurrido un problema consulte al desarrollador. ", Toast.LENGTH_LONG).show();
            }
           }
        });

        buttonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginMainActivity.this, RegistrarUsuarioMainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void iniciarSesion(View view){
        mAuth.signInWithEmailAndPassword(usuario.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            //Toast.makeText(getBaseContext(), "Authentication failed.",
                              //      Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                    }
                });
    }

//    private void updateUI(FirebaseUser user) {
//        hideProgressBar();
//
//        TextView idView = findViewById(R.id.anonymousStatusId);
//        TextView emailView = findViewById(R.id.anonymousStatusEmail);
//        boolean isSignedIn = (user != null);
//
//        // Status text
//        if (isSignedIn) {
//            idView.setText(getString(R.string.id_fmt, user.getUid()));
//            emailView.setText(getString(R.string.email_fmt, user.getEmail()));
//        } else {
//            idView.setText(R.string.signed_out);
//            emailView.setText(null);
//        }
//
//        // Button visibility
//        findViewById(R.id.buttonAnonymousSignIn).setEnabled(!isSignedIn);
//        findViewById(R.id.buttonAnonymousSignOut).setEnabled(isSignedIn);
//        findViewById(R.id.buttonLinkAccount).setEnabled(isSignedIn);
//    }


}