package com.example.all4sport;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class PromoFragment extends Fragment {

    // Context
    private Context context;
    private RecyclerView recyclerPromos;
    private RecyclerView.Adapter promosAdapter;
    private List<CartItem> articleViews;

    public static PromoFragment newInstance(Context context) {
        PromoFragment fragment = new PromoFragment(context);
        return fragment;
    }

    public PromoFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_promo, container, false);
        this.articleViews = new ArrayList<>();
        this.recyclerPromos = view.findViewById(R.id.recycler_view_produits);
        this.recyclerPromos.setHasFixedSize(true);
        this.recyclerPromos.setLayoutManager(new LinearLayoutManager(context));
        this.promosAdapter = new PromosAdapter(this.articleViews, context);
        this.recyclerPromos.setAdapter(this.promosAdapter);
        fetchArticles();
        return view;
    }

    private void addArticle(CartItem cartItem) {
        this.articleViews.add(cartItem);
        this.promosAdapter.notifyDataSetChanged();
    }

    private void fetchArticles() {
        String endpoint = "/articles/promo";
        BearerTokenRequest request = new BearerTokenRequest(context);
        request.sendRequest(Request.Method.GET, endpoint, null, new BearerTokenRequest.BearerTokenRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {

                try {
                    System.out.println(response);
                    for (int i = 0; i < response.getJSONArray("articles").length(); i++) {
                        JSONObject article = response.getJSONArray("articles").getJSONObject(i);
                        String reference = article.getString("reference");
                        String name = article.getString("nom");
                        String price = article.getString("prix");
                        String image = article.getString("photo");
                        String discount = article.getString("discount");

                        String tvaPrice = String.valueOf(Double.parseDouble(price) + 0.2 * Double.parseDouble(price));

                        CartItem cartItem = new CartItem(reference,image, name, 1, Double.parseDouble(tvaPrice), Double.parseDouble(discount));
                        System.out.println("test"+cartItem);
                        addArticle(cartItem);
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
}