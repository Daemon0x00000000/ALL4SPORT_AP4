package com.example.all4sport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

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
        JSONObject jsonCredentials = new JSONObject("{username: \"" + username + "\", password: \"" + password + "\"}");
        String endpoint = "/login";
        BearerTokenRequest request = new BearerTokenRequest(this);
        request.sendRequest(Request.Method.POST, endpoint, jsonCredentials, new BearerTokenRequest.BearerTokenRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                SharedPreferences sharedPreferences = getSharedPreferences("MonApplication", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                try {
                    editor.putString("bearer_token", response.getString("token"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                editor.apply();
                Toast.makeText(MainActivity.this,"Connexion Établie",Toast.LENGTH_SHORT).show();
                ouvreAPP();
            }

            @Override
            public void onError(VolleyError error) {
                System.out.println(error.toString());
                Toast.makeText(MainActivity.this,"identifiant ou mot de passe incorrect",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void ouvreAPP() {
        Intent intent = new Intent(this, app.class);
        startActivity(intent);
    }

}

