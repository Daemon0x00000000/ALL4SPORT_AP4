package com.example.all4sport;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class PromosAdapter extends RecyclerView.Adapter<PromosAdapter.ViewHolder>
{
    private List<CartItem> articleViews;
    private Context context;
    private ArrayList<Boolean> isProduitInCart = new ArrayList<>();

    public PromosAdapter(List<CartItem> articleViews, Context context) {
        this.articleViews = articleViews;
        this.context = context;

    }


    @Override
    public PromosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.promo_produit, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PromosAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CartItem cartItem = articleViews.get(position);
        byte[] decodedString = Base64.decode(cartItem.getImage(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.itemImage.setImageBitmap(decodedByte);
        holder.itemName.setText(cartItem.getNom());
        String prix = String.valueOf(cartItem.getPrice())+"€";
        String prixDiscount = String.valueOf(cartItem.getPrice() * (1 - cartItem.getDiscount()/100))+"€";
        holder.itemPrice.setText(prix);
        holder.itemDiscount.setText(prixDiscount);
        isProduitInCart.add(position,false);
        checkCart(cartItem.getReference(), holder.itemButton, position);

        holder.itemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isProduitInCart.get(position)) {
                    deleteFromCart(cartItem.getReference(), holder.itemButton, position);
                } else {
                    addToCart(cartItem.getReference(), holder.itemButton, position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return articleViews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView itemImage;
        public TextView itemName;
        public TextView itemPrice;
        public TextView itemDiscount;
        public Button itemButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.image_discount);
            itemName = itemView.findViewById(R.id.text_discount_name);
            itemPrice = itemView.findViewById(R.id.text_price);
            itemDiscount = itemView.findViewById(R.id.text_discount);
            itemButton = itemView.findViewById(R.id.add_item);

            itemPrice.setPaintFlags(itemPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            // Margin for item
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();
            layoutParams.setMargins(20, 20, 20, 20);

            // Elevate item
            itemView.setElevation(10);


        }
    }

    private void addToCart(String referenceProduit, Button buttonAddToCart, int position) {
        String endpoint = "/panier";
        BearerTokenRequest request = new BearerTokenRequest(this.context);
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
                Toast.makeText(context, "Article ajouté au panier", Toast.LENGTH_SHORT).show();
                buttonAddToCart.setEnabled(true);
                buttonAddToCart.setText("Supprimer");
                isProduitInCart.set(position, true);

            }

            @Override
            public void onError(VolleyError error) {
                System.out.println(error.getMessage());
                Toast.makeText(context, "Une erreur est survenue", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkCart(String referenceProduit, Button buttonAddToCart, int position) {
        String endpoint = "/panier";
        BearerTokenRequest request = new BearerTokenRequest(context);
        request.sendRequest(Request.Method.GET, endpoint, null, new BearerTokenRequest.BearerTokenRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray panier = response.getJSONArray("articles");
                    for (int i = 0; i < panier.length(); i++) {
                        JSONObject article = panier.getJSONObject(i);
                        System.out.println(article.getString("reference")+" "+referenceProduit);
                        if (article.getString("reference").equals(referenceProduit)) {
                            System.out.println("checked");
                            isProduitInCart.set(position, true);
                            buttonAddToCart.setText("Supprimer");
                            break;
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
        return isProduitInCart.get(position);
    }

    private void deleteFromCart(String referenceProduit, Button buttonAddToCart, int position) {
        String endpoint = "/panier/" + referenceProduit;
        BearerTokenRequest request = new BearerTokenRequest(context);
        buttonAddToCart.setEnabled(false);
        request.sendRequest(Request.Method.DELETE, endpoint, null, new BearerTokenRequest.BearerTokenRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                System.out.println(response);
                buttonAddToCart.setEnabled(true);
                buttonAddToCart.setText("Ajouter au panier");
                isProduitInCart.set(position, false);
            }

            @Override
            public void onError(VolleyError error) {
                System.out.println(error);
            }
        });
    }

}
