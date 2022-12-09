package com.example.all4sport;

public class CartItem {
    private String nom;
    private int quantity;
    private double price;

    public CartItem(String nom, int quantity, double price) {
        this.nom = nom;
        this.quantity = quantity;
        this.price = price;
    }


    public String getNom() {
        return nom;
    }


    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

}
