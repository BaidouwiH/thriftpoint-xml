package com.example.thriftpoint_xml.recycler_view;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.thriftpoint_xml.R;
import com.example.thriftpoint_xml.models.Product;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductOnCheckoutAdapter extends RecyclerView.Adapter<ProductOnCheckoutAdapter.ViewHolder> {

    private final ArrayList<Product> productsList;

    public ProductOnCheckoutAdapter(ArrayList<Product> productsList) {
        this.productsList = productsList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImage;
        private final TextView name;
        private final TextView price;

        public ViewHolder(View view) {
            super(view);
            productImage = view.findViewById(R.id.productImage);
            name = view.findViewById(R.id.name);
            price = view.findViewById(R.id.price);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_on_checkout_card, parent, false);
        return new ProductOnCheckoutAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        ExecutorService execGetImage =
                Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        execGetImage.execute(() -> handler.post(() -> Glide.with(viewHolder.itemView.getContext())
                .load("https://guspascad.blob.core.windows.net/democontainer/" +
                        productsList.get(position).getImageRes())
                .centerCrop()
                .into(viewHolder.productImage)));
        viewHolder.name.setText(productsList.get(position).getName());
        viewHolder.price.setText("Rp " + productsList.get(position).getPrice());
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }


}
