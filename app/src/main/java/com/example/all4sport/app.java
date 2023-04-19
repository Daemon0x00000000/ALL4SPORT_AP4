package com.example.all4sport;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONArray;
import org.json.JSONObject;


public class app extends AppCompatActivity {
    private FloatingActionButton floatingQrCodeButton;
    private BottomNavigationView bottomNavatigationView;
    private FrameLayout frameLayout;
    private MyViewModel mViewModel;


    private void setActivityView() {
        setContentView(R.layout.activity_app);
        qrCodeListener();
    }

    private void qrCodeListener() {
        floatingQrCodeButton = findViewById(R.id.qrCode);
        floatingQrCodeButton.setOnClickListener(view -> {
            // Pour la permission de la camera
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions((Activity) this,
                        new String[] { Manifest.permission.CAMERA },
                        100);
            } else {
                Intent intent = new Intent(this, Scan.class);
                startActivityForResult(intent, 1);

            }


        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                // Faire quelque chose avec le résultat
                String endpoint = "/articles?reference=" + result;
                BearerTokenRequest bearerTokenRequest = new BearerTokenRequest(this);
                bearerTokenRequest.sendRequest(Request.Method.GET, endpoint, null, new BearerTokenRequest.BearerTokenRequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        setArticleInfos(response);
                    }
                    @Override
                    public void onError(VolleyError error) {
                        System.out.println("Il y'a une erreur" + error);
                    }
                });

            }
        }
    }

    private void setArticleInfos(JSONObject articleInfos) {
        try {
            String reference = articleInfos.getString("reference");
            String name = articleInfos.getString("nom");
            String price = articleInfos.getString("prix");
            String description = articleInfos.getString("description");
            String image = articleInfos.getString("photos");

            Double tvaPrice = Double.parseDouble(price) + 0.2 * Double.parseDouble(price);

            CartItem cartItem = new CartItem(reference, image, name, 1, tvaPrice);
            mViewModel.setCartItem(cartItem);

        } catch (Exception e) {
            System.out.println(e);
            Toast.makeText(this, "QR Code invalide", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityView();
        frameLayout = findViewById(R.id.fragment_container);
        mViewModel = new ViewModelProvider(this).get(MyViewModel.class);
        bottomNavatigationView=findViewById(R.id.bottomNavatigationView);
        bottomNavatigationView.setBackground(null);
        Fragment fragment = new ProductFragment(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        bottomNavatigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.article:
                        selectedFragment = new ProductFragment(app.this);
                        break;
                    case R.id.panier:
                        selectedFragment = new PanierFragment(app.this);
                        break;
                    case R.id.promos:
                        selectedFragment = new PromoFragment(app.this);
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }
        });
    }






}

