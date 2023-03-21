package com.example.all4sport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;




public class MainActivity extends AppCompatActivity {

    private static final String url = "http://172.20.10.8:8000/api/login";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView identifiant =(TextView) findViewById(R.id.identifiant);
        TextView password =(TextView) findViewById(R.id.password);

        MaterialButton connexion = (MaterialButton) findViewById(R.id.connexion);
        // Send request to 172.16.107.41:8080/api/login
        connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                try {
                    checkLogin(identifiant.getText().toString(), password.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void checkLogin(String username, String password) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject jsonCredentials = new JSONObject("{username: \"" + username + "\", password: \"" + password + "\"}");
        System.out.println(jsonCredentials.get("username"));
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonCredentials, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(MainActivity.this,"Connexion Établie",Toast.LENGTH_SHORT).show();
                ouvreAPP();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
                Toast.makeText(MainActivity.this,"identifiant ou mot de passe incorrect",Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);
    }

    public void ouvreAPP() {
        Intent intent = new Intent(this, app.class);
        startActivity(intent);
    }

}

