package com.example.thriftpoint_xml.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class Product implements Parcelable {
    private String id;
    private String category;
    private String image_res;
    private String name;
    private String description;
    private int price;

    public Product(String id, String category, String image_res, String name, int price, String description) {
        this.id = id;
        this.category = category;
        this.image_res = image_res;
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public Product() {

    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getCategory() {
        return this.name;
    }

    public String getImageRes() {
        return this.image_res;
    }

    public int getPrice() {
        return this.price;
    }

    public String getDescription() {
        return this.description;
    }

    public Product(Parcel in) {
        this.category = in.readString();
        this.image_res = in.readString();
        this.name = in.readString();
        this.price = in.readInt();
        this.description = in.readString();
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(category);
        dest.writeString(image_res);
        dest.writeString(name);
        dest.writeInt(price);
        dest.writeString(description);
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public Map<String, Object> toMap() {
        Map<String, Object> productMap = new HashMap<>();
        productMap.put("id", this.id);
        productMap.put("category", this.category);
        productMap.put("image_res", this.image_res);
        productMap.put("name", this.name);
        productMap.put("price", this.price);
        productMap.put("description", this.description);

        return productMap;
    }

    public static Product fromMap(Map<String, Object> productMap) {
        return new Product(
                productMap.get("id").toString(),
                productMap.get("category").toString(),
                productMap.get("image_res").toString(),
                productMap.get("name").toString(),
                Math.toIntExact((Long) (productMap.get("price"))),
                productMap.get("description").toString()
        );
    }
}
