package com.example.all4sport;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class PanierFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<CartItem> cartItems;
    private MaterialButton validateButton;
    private Context context;

    public PanierFragment(Context context) {
        this.context = context;
    }

    public static PanierFragment newInstance(Context context) {
        PanierFragment fragment = new PanierFragment(context);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        BearerTokenRequest request = new BearerTokenRequest(context);
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
                Intent validateIntent = new Intent(context, OrderActivity.class);
                validateIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                validateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(validateIntent);
            }
        });

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_panier, container, false);
        this.cartItems = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new PanierAdapter(cartItems, context);
        recyclerView.setAdapter(adapter);

        this.validateButton = view.findViewById(R.id.valider);
        validateListener();

        fetchCartItems();
        return view;
    }
}