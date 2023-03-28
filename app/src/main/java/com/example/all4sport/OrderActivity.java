package com.example.all4sport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderActivity extends AppCompatActivity {

    private EditText address;
    private EditText city;
    private EditText postalCode;
    private MaterialButton validerOrder;
    private double longitude;
    private double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        address = findViewById(R.id.adresse);
        city = findViewById(R.id.ville);
        postalCode = findViewById(R.id.codePostal);
        validerOrder = findViewById(R.id.validerOrder);
        validerOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order();
            }
        });
        askLocationPermission();

    }

    private void order() {
        String endpoint = "/order";
        BearerTokenRequest request = new BearerTokenRequest(this);
        JSONObject body = new JSONObject();
        try {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(OrderActivity.this, "Veuillez activer le GPS", Toast.LENGTH_LONG).show();
                return;
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                body.put("latitude", latitude);
                body.put("longitude", longitude);
                System.out.println("Latitude: " + latitude);
                System.out.println("Longitude: " + longitude);
            } else {
                Toast.makeText(OrderActivity.this, "Impossible de récupérer la position", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request.sendRequest(Request.Method.POST, endpoint, body, new BearerTokenRequest.BearerTokenRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Toast.makeText(OrderActivity.this, "Commande effectuée", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(OrderActivity.this, "Une erreur est survenue", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void askLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }


}