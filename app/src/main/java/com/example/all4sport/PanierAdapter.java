package com.example.all4sport;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import org.json.JSONObject;

import java.util.List;


public class PanierAdapter extends RecyclerView.Adapter<PanierAdapter.ViewHolder>
{
    private List<CartItem> cartItems;
    private Context context;

    public PanierAdapter(List<CartItem> cartItems, Context context) {
        this.cartItems = cartItems;
        this.context = context;
    }


    @Override
    public PanierAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PanierAdapter.ViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        byte[] decodedString = Base64.decode(cartItem.getImage(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.itemImage.setImageBitmap(decodedByte);
        holder.itemName.setText(cartItem.getNom());
        String prix = String.valueOf(cartItem.getPrice())+"€";
        holder.itemPrice.setText(prix);
        holder.itemQuantity.setText(String.valueOf(cartItem.getQuantity()));
        holder.itemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCartItem(cartItem.getReference(), position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView itemImage;
        public TextView itemName;
        public TextView itemPrice;
        public TextView itemQuantity;
        private Button itemButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.image_view_item);
            itemName = itemView.findViewById(R.id.text_view_item_name);
            itemPrice = itemView.findViewById(R.id.text_view_item_price);
            itemQuantity = itemView.findViewById(R.id.text_view_item_quantity);
            itemButton = itemView.findViewById(R.id.button_remove_item);


        }
    }

    private void deleteCartItem(String reference,int position) {
        String endpoint = "/panier/" + reference;
        BearerTokenRequest request = new BearerTokenRequest(this.context);
        request.sendRequest(Request.Method.DELETE, endpoint, null, new BearerTokenRequest.BearerTokenRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                cartItems.remove(position);
                notifyDataSetChanged();

            }

            @Override
            public void onError(VolleyError error) {
                System.out.println(error.getMessage());
                Toast.makeText(context, "Une erreur est survenue", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
