package com.example.thriftpoint_xml.recycler_view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thriftpoint_xml.ProductsFragment;
import com.example.thriftpoint_xml.R;
import com.example.thriftpoint_xml.WishlistFragment;
import com.example.thriftpoint_xml.models.Filter;
import com.example.thriftpoint_xml.models.Product;

import java.util.ArrayList;
import java.util.Objects;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {
    private final ArrayList<Filter> filtersList;
    private final String activeContent;

    public FilterAdapter(ArrayList<Filter> filtersList, String activeContent) {
        this.filtersList = filtersList;
        this.activeContent = activeContent;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageButton filterButton;
        private final TextView filterName;

        public ViewHolder(View view) {
            super(view);
            filterButton = view.findViewById(R.id.filter_button);
            filterName = view.findViewById(R.id.filter_name);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_item, parent, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.filterButton.setImageResource(filtersList.get(position).getImage());
        viewHolder.filterName.setText(filtersList.get(position).getName());
        if (Objects.equals(filtersList.get(position).getName(), this.activeContent)) {
            viewHolder.filterButton.setBackgroundResource(R.drawable.btn_shape_2);
        }
        if (Objects.equals(filtersList.get(position).getName(), "Semua")) {
            viewHolder.filterButton.setPadding(40, 40, 40, 40);
        }
        viewHolder.filterButton.setOnClickListener(view -> {
            ProductsFragment productsFragment = ProductsFragment.newInstance(filtersList.get(position).getName());
            ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flFragment, productsFragment)
                    .addToBackStack("home")
                    .commit();
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return filtersList.size();
    }
}
