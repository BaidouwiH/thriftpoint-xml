package com.example.thriftpoint_xml.recycler_view;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.thriftpoint_xml.ProductDetailsFragment;
import com.example.thriftpoint_xml.R;
import com.example.thriftpoint_xml.models.Filter;
import com.example.thriftpoint_xml.models.Product;
import com.example.thriftpoint_xml.models.UserData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private final ArrayList<Product> productsList;
    private String previousRoute = "";

    public ProductAdapter(ArrayList<Product> productsList) {
        this.productsList = productsList;
    }

    public ProductAdapter(ArrayList<Product> productsList, String previousRoute) {
        this.productsList = productsList;
        this.previousRoute = previousRoute;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImage;
        private final TextView name;
        private final TextView price;
        private final CardView productCard;


        public ViewHolder(View view) {
            super(view);
            productImage = view.findViewById(R.id.productImage);
            name = view.findViewById(R.id.name);
            price = view.findViewById(R.id.price);
            productCard = view.findViewById(R.id.productCard);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_card, parent, false);
        return new ProductAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        ExecutorService execGetImage =
                Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        String backStack;
        if (this.previousRoute.equals("catalog")) {
            backStack = "catalog";
        } else {
            backStack = "products";
        }
        execGetImage.execute(() -> handler.post(() -> Glide.with(viewHolder.itemView.getContext())
                .load("https://guspascad.blob.core.windows.net/democontainer/" +
                        productsList.get(position).getImageRes())
                .centerCrop()
                .into(viewHolder.productImage)));

        viewHolder.name.setText(productsList.get(position).getName());
        viewHolder.price.setText("Rp " + productsList.get(position).getPrice());
        viewHolder.productCard.setOnClickListener(view -> {
            ProductDetailsFragment productDetailsFragment = ProductDetailsFragment.newInstance(
                    productsList.get(position), previousRoute
            );
            ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flFragment, productDetailsFragment)
                    .addToBackStack(backStack)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }
}
