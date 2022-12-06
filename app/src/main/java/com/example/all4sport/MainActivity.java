package com.example.all4sport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView identifiant =(TextView) findViewById(R.id.identifiant);
        TextView password =(TextView) findViewById(R.id.password);

        MaterialButton connexion = (MaterialButton) findViewById(R.id.connexion);

        connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(identifiant.getText().toString().equals("test") && password.getText().toString().equals("test")) {
                    Toast.makeText(MainActivity.this,"Connexion Établie",Toast.LENGTH_SHORT).show();
                    ouvreAPP();
                }else
                    Toast.makeText(MainActivity.this,"identifiant ou mot de passe incorrect",Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void ouvreAPP() {
        Intent intent = new Intent(this, app.class);
        startActivity(intent);
    }

}