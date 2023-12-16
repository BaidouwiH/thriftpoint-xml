package com.example.thriftpoint_xml.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class UserData {
    public String name;
    public String email;
    public ArrayList<Map<String, Object>> fav_products;
    public ArrayList<Map<String, Object>> products_on_cart;

    public UserData(String name, String email, ArrayList<Map<String, Object>> fav_products,
                    ArrayList<Map<String, Object>> products_on_cart) {
        this.name = name;
        this.email = email;
        this.fav_products = fav_products;
        this.products_on_cart = products_on_cart;
    }

    public static UserData fromMap(Map<String, Object> documentSnapshot) {
        return new UserData(
                documentSnapshot.get("name").toString(),
                documentSnapshot.get("email").toString(),
                (ArrayList<Map<String, Object>>) documentSnapshot.get("fav_products"),
                (ArrayList<Map<String, Object>>) documentSnapshot.get("products_on_cart")
        );
    }

    public ArrayList<Map<String, Object>> getFavProducts() {
        return this.fav_products;
    }


    public ArrayList<Map<String, Object>> getProductsOnCart() {
        return this.products_on_cart;
    }


    public String getEmail() {
        return this.email;
    }

    public String getName() {
        return this.name;
    }

    public UserData() {

    }
}
