package com.example.thriftpoint_xml;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thriftpoint_xml.models.Product;
import com.example.thriftpoint_xml.recycler_view.ProductAdapter;
import com.example.thriftpoint_xml.utils.GridSpacingItemDecoration;
import com.example.thriftpoint_xml.viewmodels.MainViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CatalogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CatalogFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseFirestore db;
    MainViewModel viewModel;

    ArrayList<Product> productsList = new ArrayList<>();

    public CatalogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CatalogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CatalogFragment newInstance(String param1, String param2) {
        CatalogFragment fragment = new CatalogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_catalog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rvProducts = view.findViewById(R.id.rvProducts);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        db = FirebaseFirestore.getInstance();

        db.collection("products").whereEqualTo("publisher",
                        viewModel.getUserData().getValue().name).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (productsList.isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                Product product = new Product(document.getId(), data.get("category").toString(),
                                        data.get("image_res").toString(),
                                        data.get("name").toString(), Math.toIntExact((Long)data.get("price")),
                                        data.get("description").toString());
                                productsList.add(product);
                            }
                        }
                    } else {
                        Log.d("TAG", "Error getting documents: ", task.getException());
                        return;
                    }
                    ProductAdapter productAdapter = new ProductAdapter(productsList, "catalog");
                    rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
                    rvProducts.addItemDecoration(new GridSpacingItemDecoration(2, 40,
                            false));
                    rvProducts.setAdapter(productAdapter);
                });
    }
}