package com.example.all4sport;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

public class MyViewModel extends ViewModel {
    private MutableLiveData<CartItem> cartItem;
    // Convert above to mutable live data
    private MutableLiveData<Boolean> isProduitInCartLiveData;

    public MyViewModel() {
        this.cartItem = new MutableLiveData<>();
        this.isProduitInCartLiveData = new MutableLiveData<>(false);

    }

    public MutableLiveData<CartItem> getCartItem() {
        return cartItem;
    }

    public void setCartItem(CartItem cartItem) {
        this.cartItem.setValue(cartItem);
    }

    public MutableLiveData<Boolean> getIsProduitInCartLiveData() {
        return isProduitInCartLiveData;
    }

    public void setIsProduitInCartLiveData(Boolean isProduitInCart) {
        this.isProduitInCartLiveData.setValue(isProduitInCart);
    }



}
