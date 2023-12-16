package com.example.thriftpoint_xml.models;

import com.example.thriftpoint_xml.R;

import java.util.ArrayList;

public class Filter {
    private final String name;
    private final int image;

    public Filter(String name, int image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public static ArrayList<Filter> getFilters() {
        ArrayList<Filter> filtersList = new ArrayList<>();
        filtersList.add(new Filter("Semua", R.drawable.group_19133));
        filtersList.add(new Filter("Wanita", R.drawable.rectangle_344));
        filtersList.add(new Filter("Pria", R.drawable.hoodie));
        filtersList.add(new Filter("Anak", R.drawable.baju_anak));
        filtersList.add(new Filter("Topi", R.drawable.topi));
        filtersList.add(new Filter("Sepatu", R.drawable.sepatu));
        filtersList.add(new Filter("Tas", R.drawable.tas));
        return filtersList;
    }
}
