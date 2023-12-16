package com.example.thriftpoint_xml;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thriftpoint_xml.models.Filter;
import com.example.thriftpoint_xml.models.Product;
import com.example.thriftpoint_xml.recycler_view.FilterAdapter;
import com.example.thriftpoint_xml.recycler_view.ProductAdapter;
import com.example.thriftpoint_xml.utils.GridSpacingItemDecoration;
import com.example.thriftpoint_xml.viewmodels.MainViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String filterNameArg = "param1";

    // TODO: Rename and change types of parameters
    private String filterName;

    MainViewModel viewModel;

    RecyclerView rvProducts;

    FirebaseFirestore db;
    ArrayList<Product> productsList = new ArrayList<>();

    public ProductsFragment() {
        // Required empty public constructor
    }

    public static ProductsFragment newInstance(String param1) {
        ProductsFragment fragment = new ProductsFragment();
        Bundle args = new Bundle();
        args.putString(filterNameArg, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filterName = getArguments().getString(filterNameArg);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_products, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        FilterAdapter filterAdapter = new FilterAdapter(Filter.getFilters(), filterName);
        RecyclerView rvFilters = view.findViewById(R.id.rvFilters);
        rvProducts = view.findViewById(R.id.rvProducts);
        rvFilters.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        rvFilters.setAdapter(filterAdapter);

        LinearLayout productDetailsBottomBar = view.getRootView().findViewById(R.id.productDetailsBottomBar);
        productDetailsBottomBar.setVisibility(View.INVISIBLE);
        BottomNavigationView bottomNavigationView = view.getRootView().findViewById(R.id.bottomNavigationView);
        ViewGroup.LayoutParams params = bottomNavigationView.getLayoutParams();
        params.height = 200;
        bottomNavigationView.setLayoutParams(params);
        bottomNavigationView.setVisibility(View.VISIBLE);
        FloatingActionButton fab = view.getRootView().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

        TextInputLayout search = view.findViewById(R.id.search);
        search.getEditText().setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        search.getEditText().setOnEditorActionListener((query, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(query.getText().toString());
                return true;
            }
            return false;
        });

        if (filterName.equals("Semua")) {
            db.collection("products").get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                Product product = new Product(document.getId(), data.get("category").toString(),
                                        data.get("image_res").toString(),
                                        data.get("name").toString(), Math.toIntExact((Long)data.get("price")),
                                        data.get("description").toString());
                                productsList.add(product);
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                            return;
                        }
                        Log.d("TAG", productsList.get(0).getImageRes());
                        ProductAdapter productAdapter = new ProductAdapter(productsList);
                        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
                        rvProducts.addItemDecoration(new GridSpacingItemDecoration(2, 40,
                                false));
                        rvProducts.setAdapter(productAdapter);
                    });
        } else {
            db.collection("products").whereEqualTo("category", filterName).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                Product product = new Product(document.getId(), data.get("category").toString(),
                                        data.get("image_res").toString(),
                                        data.get("name").toString(), Math.toIntExact((Long)data.get("price")),
                                        data.get("description").toString());
                                productsList.add(product);
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                            return;
                        }
                        Log.d("TAG", productsList.get(0).getImageRes());
                        ProductAdapter productAdapter = new ProductAdapter(productsList);
                        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
                        rvProducts.addItemDecoration(new GridSpacingItemDecoration(2, 40,
                                false));
                        rvProducts.setAdapter(productAdapter);
                    });
        }
    }

    private void performSearch(String query) {
        db.collection("products").whereEqualTo("name", query).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        productsList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            Product product = new Product(document.getId(), data.get("category").toString(),
                                    data.get("image_res").toString(),
                                    data.get("name").toString(), Math.toIntExact((Long)data.get("price")),
                                    data.get("description").toString());
                            productsList.add(product);
                        }
                    } else {
                        Log.d("TAG", "Error getting documents: ", task.getException());
                        return;
                    }
                    ProductAdapter productAdapter = new ProductAdapter(productsList);
                    rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
                    rvProducts.addItemDecoration(new GridSpacingItemDecoration(2, 40,
                            false));
                    rvProducts.setAdapter(productAdapter);
                });
    }

}