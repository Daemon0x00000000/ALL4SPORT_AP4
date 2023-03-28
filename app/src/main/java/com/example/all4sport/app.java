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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;


public class app extends AppCompatActivity {
    private String referenceProduit;
    private LinearLayout layout;
    private FloatingActionButton floatingQrCodeButton;
    private LinearLayout loadingLayout;
    private TextView priceArticle;
    private TextView nomArticle;
    private ImageView imageArticle;
    private TextView descriptionArticle;
    private MaterialButton buttonAddToCart;
    private BottomNavigationView bottomNavatigationView;
    private final boolean[] isProduitInCart = {false};


    private void setActivityView() {
        setContentView(R.layout.activity_app);
        this.layout = findViewById(R.id.content);
        this.loadingLayout = findViewById(R.id.loadingLayout);
        this.nomArticle = findViewById(R.id.nameArticle);
        this.priceArticle = findViewById(R.id.priceArticle);
        this.imageArticle = findViewById(R.id.imageArticle);
        this.descriptionArticle = findViewById(R.id.descriptionArticleText);
        this.buttonAddToCart = findViewById(R.id.addToCart);
        this.loadingLayout.setVisibility(View.VISIBLE);
        qrCodeListener();
        addButtonListener();
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
                referenceProduit = result;
                String endpoint = "/articles?reference=" + referenceProduit;
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

            String tvaPrice = String.valueOf(Double.parseDouble(price) + 0.2 * Double.parseDouble(price));
            String nameArticleText = "Nom : " + name;
            String priceArticleText = "Prix : " + tvaPrice + " €";

            this.nomArticle.setText(nameArticleText);
            this.priceArticle.setText(priceArticleText);
            byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            this.imageArticle.setImageBitmap(decodedByte);

            this.descriptionArticle.setText(description);
            checkCart();
            this.loadingLayout.setVisibility(View.GONE);
            this.layout.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            System.out.println(e);
            Toast.makeText(this, "QR Code invalide", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityView();
        bottomNavatigationView=findViewById(R.id.bottomNavatigationView);
        bottomNavatigationView.setBackground(null);
        bottomNavatigationView.setSelectedItemId(R.id.article);

        bottomNavatigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.panier) {
                    Intent panierIntent = new Intent(app.this, Panier.class);
                    panierIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    panierIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(panierIntent);
                    System.out.println("panier");
                    return true;
                }
                return false;
            }
        });


    }

    private boolean checkCart() {
        String endpoint = "/panier";
        BearerTokenRequest request = new BearerTokenRequest(this);
        request.sendRequest(Request.Method.GET, endpoint, null, new BearerTokenRequest.BearerTokenRequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            JSONArray panier = response.getJSONArray("articles");
                            for (int i = 0; i < panier.length(); i++) {
                                JSONObject article = panier.getJSONObject(i);
                                if (article.getString("reference").equals(referenceProduit)) {
                                    isProduitInCart[0] = true;
                                    buttonAddToCart.setText("Supprimer du panier");
                                }
                            }
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        System.out.println(error);
                    }
                });
        return isProduitInCart[0];
    }

    private void addToCart() {
        String endpoint = "/panier";
        BearerTokenRequest request = new BearerTokenRequest(this);
        JSONObject body = new JSONObject();
        try {
            body.put("reference", referenceProduit);
            body.put("quantite", 1);
        } catch (Exception e) {
            System.out.println(e);
        }
        buttonAddToCart.setEnabled(false);
        request.sendRequest(Request.Method.POST, endpoint, body, new BearerTokenRequest.BearerTokenRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                System.out.println(response);
                buttonAddToCart.setEnabled(true);
                buttonAddToCart.setText("Supprimer du panier");
            }

            @Override
            public void onError(VolleyError error) {
                System.out.println(error);
            }
        });
    }

    private void deleteFromCart() {
        String endpoint = "/panier/" + referenceProduit;
        BearerTokenRequest request = new BearerTokenRequest(this);
        buttonAddToCart.setEnabled(false);
        request.sendRequest(Request.Method.DELETE, endpoint, null, new BearerTokenRequest.BearerTokenRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                System.out.println(response);
                buttonAddToCart.setEnabled(true);
                buttonAddToCart.setText("Ajouter au panier");
            }

            @Override
            public void onError(VolleyError error) {
                System.out.println(error);
            }
        });
    }

    private void addButtonListener() {
        buttonAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isProduitInCart[0]) {
                    deleteFromCart();
                } else {
                    addToCart();
                }
            }
        });
    }

}

