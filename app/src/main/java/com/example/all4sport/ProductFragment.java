package com.example.all4sport;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;


public class ProductFragment extends Fragment {

    private Context context;
    private String referenceProduit;
    private LinearLayout layout;
    private LinearLayout loadingLayout;
    private TextView priceArticle;
    private TextView nomArticle;
    private ImageView imageArticle;
    private TextView descriptionArticle;
    private MaterialButton buttonAddToCart;
    private MyViewModel model;

    public ProductFragment(Context context) {
        this.context = context;
    }


    public static ProductFragment newInstance(Context context) {
        ProductFragment fragment = new ProductFragment(context);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Récupère une référence à l'objet ViewModel à l'aide d'un objet ViewModelProvider
        this.model = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        this.layout = view.findViewById(R.id.content);
        this.loadingLayout = view.findViewById(R.id.loadingLayout);
        this.nomArticle = view.findViewById(R.id.nameArticle);
        this.priceArticle = view.findViewById(R.id.priceArticle);
        this.imageArticle = view.findViewById(R.id.imageArticle);
        this.descriptionArticle = view.findViewById(R.id.descriptionArticleText);
        this.buttonAddToCart = view.findViewById(R.id.addToCart);
        this.loadingLayout.setVisibility(View.VISIBLE);
        addButtonListener();

        System.out.println("Code executed");
        this.model.getCartItem().observe(getViewLifecycleOwner(), cartItem1 -> {
            if (cartItem1 != null) {
                referenceProduit = cartItem1.getReference();
                nomArticle.setText(cartItem1.getNom());
                String price = cartItem1.getPrice() + "€";
                priceArticle.setText(price);
                byte[] decodedString = Base64.decode(cartItem1.getImage(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageArticle.setImageBitmap(decodedByte);
                loadingLayout.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);
                if (model.getIsProduitInCartLiveData().getValue()) {
                    buttonAddToCart.setText("Supprimer du panier");
                }
                checkCart();
            }
        });

        return view;
    }

    private boolean checkCart() {
        String endpoint = "/panier";
        BearerTokenRequest request = new BearerTokenRequest(context);
        request.sendRequest(Request.Method.GET, endpoint, null, new BearerTokenRequest.BearerTokenRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray panier = response.getJSONArray("articles");
                    for (int i = 0; i < panier.length(); i++) {
                        JSONObject article = panier.getJSONObject(i);
                        if (article.getString("reference").equals(referenceProduit)) {
                            model.setIsProduitInCartLiveData(true);
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
        return model.getIsProduitInCartLiveData().getValue();
    }

    private void addToCart() {
        String endpoint = "/panier";
        BearerTokenRequest request = new BearerTokenRequest(context);
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
        BearerTokenRequest request = new BearerTokenRequest(context);
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
                if (model.getIsProduitInCartLiveData().getValue()) {
                    deleteFromCart();
                } else {
                    addToCart();
                }
            }
        });
    }

}