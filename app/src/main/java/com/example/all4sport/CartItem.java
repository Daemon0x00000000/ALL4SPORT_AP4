package com.example.all4sport;

public class CartItem {
    private String reference;
    private String image;
    private String nom;
    private int quantity;
    private double price;

    public CartItem(String reference, String image, String nom, int quantity, double price) {
        this.reference = reference;
        this.image = image;
        this.nom = nom;
        this.quantity = quantity;
        this.price = price;
    }

    public String getReference() { return reference; }

    public String getImage() { return image; }

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

    public String toString() {
        return "CartItem{" +
                "image='" + image + '\'' +
                ", nom='" + nom + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }



}
