package com.example.all4sport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.itemName.setText(cartItem.getNom());
        String prix = String.valueOf(cartItem.getPrice())+"€";
        holder.itemPrice.setText(prix);
        holder.itemQuantity.setText(String.valueOf(cartItem.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName;
        public TextView itemPrice;
        public TextView itemQuantity;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.text_view_item_name);
            itemPrice = itemView.findViewById(R.id.text_view_item_price);
            itemQuantity = itemView.findViewById(R.id.text_view_item_quantity);


        }
    }
}
