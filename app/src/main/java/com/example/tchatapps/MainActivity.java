package com.example.tchatapps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.tchatapps.fragment.HomeFragment;
import com.example.tchatapps.fragment.ProfileFragment;
import com.example.tchatapps.fragment.searchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    Button login, registrar;

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth = FirebaseAuth.getInstance();

        if(firebaseUser != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

       // updateUI(currentUser);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = findViewById(R.id.idInicioSesionButton);
        registrar = findViewById(R.id.idRegistrarButton);

//        login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getApplicationContext(), MainActivity.class));
//
//            }
//        });
//
//        registrar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getApplicationContext(), LoginMainActivity.class));
//
//            }
//        });


        Bundle intent = getIntent().getExtras();

        if(intent != null){
            String publisher = intent.getString("publisher");

            SharedPreferences.Editor editor = getSharedPreferences("PREF", MODE_PRIVATE).edit();
            editor.putString("profileid", publisher);
            editor.apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.id_fragment_container, new ProfileFragment()).commit();

        }
        else{
            getSupportFragmentManager().beginTransaction().replace(R.id.id_fragment_container, new HomeFragment()).commit();
        }


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.id_home:
                        seleccionarFragment(new HomeFragment());
                        break;
                    case R.id.id_search:
                        seleccionarFragment(new searchFragment());
                        break;
                    case R.id.id_profile:
                        seleccionarFragment(new ProfileFragment());
                        break;
                }
                return true;
            }

        });



    }
    private void seleccionarFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.id_fragment_container, fragment).
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }
}