package com.example.thriftpoint_xml;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.thriftpoint_xml.models.Product;
import com.example.thriftpoint_xml.viewmodels.MainViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductDetailsFragment extends Fragment {

    private static final String ARG_PARAM1 = "product";
    private static final String ARG_PARAM2 = "previous route";

    Product product;
    MainViewModel viewModel;
    String previousRoute;

    public ProductDetailsFragment() {

    }

    // TODO: Rename and change types and number of parameters
    public static ProductDetailsFragment newInstance(Product product, String previousRoute) {
        ProductDetailsFragment fragment = new ProductDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, product);
        args.putString(ARG_PARAM2, previousRoute);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.product = getArguments().getParcelable(ARG_PARAM1);
            this.previousRoute = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ExecutorService execGetImage =
                Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        ConstraintLayout mainLayout = view.getRootView().findViewById(R.id.mainLayout);
        ImageView productImage = view.findViewById(R.id.productImage);
        TextView name = view.findViewById(R.id.name);
        TextView price = view.findViewById(R.id.price);
        ImageButton wishlistBtn = view.findViewById(R.id.wishlistBtn);
        BottomNavigationView bottomNavigationView = view.getRootView().findViewById(R.id.bottomNavigationView);
        FloatingActionButton fab = view.getRootView().findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        bottomNavigationView.setVisibility(View.INVISIBLE);
        LinearLayout productDetailsBottomBar = view.getRootView().findViewById(R.id.productDetailsBottomBar);
        productDetailsBottomBar.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams params = bottomNavigationView.getLayoutParams();
        params.height = 50;
        bottomNavigationView.setLayoutParams(params);

        ImageButton edit = view.findViewById(R.id.edit);
        edit.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, EditCatalogFragment.newInstance(product))
                    .addToBackStack("productDetails")
                    .commit();
        });

        if (previousRoute.equals("catalog")) {
            wishlistBtn.setVisibility(View.INVISIBLE);
            edit.setVisibility(View.VISIBLE);
            productDetailsBottomBar.setVisibility(View.INVISIBLE);
        } else {
            wishlistBtn.setVisibility(View.VISIBLE);
            edit.setVisibility(View.INVISIBLE);
        }

        if (viewModel.getWishlists().getValue().contains(product)) {
            wishlistBtn.setImageResource(R.drawable.favorite);
        }

        name.setText(product.getName());
        price.setText("Rp " + product.getPrice());
        execGetImage.execute(() -> handler.post(() -> Glide.with(view.getContext())
                .load("https://guspascad.blob.core.windows.net/democontainer/" +
                       product.getImageRes())
                .into(productImage)));

        Button addToCart = view.getRootView().findViewById(R.id.addToCart);
        addToCart.setOnClickListener(v -> {
            viewModel.addProductToCart(product);
            Snackbar snackbar = Snackbar.make(mainLayout, "Berhasil menambahkan produk ke keranjang",
                    Snackbar.LENGTH_LONG);
            snackbar.show();
        });

        wishlistBtn.setOnClickListener(v -> {
            if (viewModel.getWishlists().getValue().contains(product)) {
                viewModel.removeProductFromWishlist(product);
                wishlistBtn.setImageResource(R.drawable.heart);
                Snackbar snackbar = Snackbar.make(mainLayout, "Berhasil menghapus produk dari wishlist",
                        Snackbar.LENGTH_LONG);
                snackbar.show();
            } else {
                viewModel.addProductToWishlist(product);
                wishlistBtn.setImageResource(R.drawable.favorite);
                Snackbar snackbar = Snackbar.make(mainLayout, "Berhasil menambahkan produk ke wishlist",
                        Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }
}