package com.example.all4sport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Panier extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<CartItem> cartItems;
    private BottomNavigationView bottomNavatigationView;
    private MaterialButton validateButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panier);
        this.cartItems = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PanierAdapter(cartItems, this);
        recyclerView.setAdapter(adapter);

        this.validateButton = findViewById(R.id.valider);
        validateListener();

        bottomNavatigationView=findViewById(R.id.bottomNavatigationView);
        bottomNavatigationView.setBackground(null);
        bottomNavatigationView.setSelectedItemId(R.id.panier);
        bottomNavatigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.article) {
                    Intent articleIntent = new Intent(Panier.this, app.class);
                    articleIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    articleIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(articleIntent);
                    System.out.println("article");
                    return true;
                }
                return false;
            }
        });
        fetchCartItems();
    }

    private void addCartItem(CartItem cartItem) {
        this.cartItems.add(cartItem);
        adapter.notifyDataSetChanged();
    }

    private void removeCartItem(CartItem cartItem) {
        this.cartItems.remove(cartItem);
        adapter.notifyDataSetChanged();
    }

    private void fetchCartItems() {
        String endpoint = "/panier";
        BearerTokenRequest request = new BearerTokenRequest(this);
        request.sendRequest(Request.Method.GET, endpoint, null, new BearerTokenRequest.BearerTokenRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if (cartItems != null) for (CartItem cartItem : cartItems) {
                    removeCartItem(cartItem);
                }

                try {
                    System.out.println(response);
                    for (int i = 0; i < response.getJSONArray("articles").length(); i++) {
                        JSONObject article = response.getJSONArray("articles").getJSONObject(i);
                        String reference = article.getString("reference");
                        String name = article.getString("nom");
                        String price = article.getString("prix");
                        String image = article.getString("photo");

                        String tvaPrice = String.valueOf(Double.parseDouble(price) + 0.2 * Double.parseDouble(price));

                        CartItem cartItem = new CartItem(reference,image, name, 1, Double.parseDouble(tvaPrice));
                        System.out.println("test"+cartItem);
                        addCartItem(cartItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(VolleyError error) {
                System.out.println("Il y'a une erreur" + error);
            }
        });
    }

    private void validateListener() {
        this.validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent validateIntent = new Intent(Panier.this, OrderActivity.class);
                validateIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                validateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(validateIntent);
            }
        });

    }
}