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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.thriftpoint_xml.R;
import com.example.thriftpoint_xml.models.OnAdapterItemClickListener;
import com.example.thriftpoint_xml.models.Product;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductOnCartAdapter extends RecyclerView.Adapter<ProductOnCartAdapter.ViewHolder> {

    private final ArrayList<Product> productsList;
    private final OnAdapterItemClickListener onAdapterItemClickListener;

    public ProductOnCartAdapter(ArrayList<Product> productsList, OnAdapterItemClickListener onAdapterItemClickListener) {
        this.productsList = productsList;
        this.onAdapterItemClickListener = onAdapterItemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImage;
        private final TextView name;
        private final TextView price;

        private final ImageButton decrementQty;

        private final ImageButton incrementQty;

        private final ImageButton deleteBtn;

        private final TextView count;

        public ViewHolder(View view) {
            super(view);
            productImage = view.findViewById(R.id.productImage);
            name = view.findViewById(R.id.name);
            price = view.findViewById(R.id.price);
            decrementQty = view.findViewById(R.id.decrementQty);
            incrementQty = view.findViewById(R.id.incrementQty);
            deleteBtn = view.findViewById(R.id.deleteBtn);
            count = view.findViewById(R.id.count);
        }
    }

    @NonNull
    @Override
    public ProductOnCartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_on_cart_card, parent, false);
        return new ProductOnCartAdapter.ViewHolder(view);
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

        viewHolder.incrementQty.setOnClickListener(v -> {
            int initialCount = Integer.parseInt((String) viewHolder.count.getText());
            initialCount++;
            viewHolder.count.setText("" + initialCount);
        });

        viewHolder.decrementQty.setOnClickListener(v -> {
            int initialCount = Integer.parseInt((String) viewHolder.count.getText());
            if (initialCount > 1) {
                initialCount--;
                viewHolder.count.setText("" + initialCount);
            }
        });

        viewHolder.deleteBtn.setOnClickListener(v -> {
            onAdapterItemClickListener.onDeleteButtonTapped(productsList.get(position));
        });

    }


    @Override
    public int getItemCount() {
        return productsList.size();
    }
}
