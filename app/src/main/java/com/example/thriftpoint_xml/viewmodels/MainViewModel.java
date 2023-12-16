package com.example.thriftpoint_xml.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.thriftpoint_xml.models.Product;
import com.example.thriftpoint_xml.models.UserData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<UserData> userData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Product>> productsOnCart = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Product>> wishlists = new MutableLiveData<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public void setUserData(UserData userData) {
        this.userData.setValue(userData);
        ArrayList<Product> products_on_cart = userData.products_on_cart.stream().map(Product::fromMap)
                .collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Product> wishlists = userData.fav_products.stream().map(Product::fromMap)
                .collect(Collectors.toCollection(ArrayList::new));
        this.productsOnCart.setValue(products_on_cart);
        this.wishlists.setValue(wishlists);
    }

    public void addProductToCart(Product product) {
        db.collection("users").document(mAuth.getCurrentUser().getUid()).update(
                "products_on_cart", FieldValue.arrayUnion(product.toMap())
        ).addOnSuccessListener(unused -> {
            ArrayList<Product> oldList = new ArrayList<>(this.productsOnCart.getValue());
            oldList.add(product);
            this.productsOnCart.setValue(oldList);
        });
    }

    public void addNewProduct(Map<String, Object> product) {
        db.collection("products").add(product);
    }

    public void addNewUserData(String uid, Map<String, Object> userData) {
        db.collection("users").document(uid).set(userData);
    }

    public void removeProductFromCart(Product product) {
        ArrayList<Product> oldList = new ArrayList<>(this.productsOnCart.getValue());
        oldList.remove(product);
        this.productsOnCart.setValue(oldList);
        db.collection("users").document(mAuth.getCurrentUser().getUid()).update(
                "products_on_cart", FieldValue.arrayRemove(product.toMap())
        );
    }

    public void addProductToWishlist(Product product) {
        db.collection("users").document(mAuth.getCurrentUser().getUid()).update(
                "fav_products", FieldValue.arrayUnion(product.toMap())
        ).addOnSuccessListener(unused -> {
            ArrayList<Product> oldList = new ArrayList<>(this.wishlists.getValue());
            oldList.add(product);
            this.wishlists.setValue(oldList);
        });
        Log.d("TAG", product.getImageRes());
    }

    public void removeProductFromWishlist(Product product) {
        db.collection("users").document(mAuth.getCurrentUser().getUid()).update(
                "fav_products", FieldValue.arrayRemove(product.toMap())
        ).addOnSuccessListener(unused -> {
            ArrayList<Product> oldList = new ArrayList<>(this.wishlists.getValue());
            oldList.remove(product);
            this.wishlists.setValue(oldList);
        });
    }

    public void updateProduct(String id, Map<String, Object> editedProduct) {
        db.collection("products").document(id).update(editedProduct);
    }

    public LiveData<UserData> getUserData() {
        return this.userData;
    }

    public LiveData<ArrayList<Product>> getProductsOnCart() {
        return this.productsOnCart;
    }

    public LiveData<ArrayList<Product>> getWishlists() {
        return this.wishlists;
    }
}
