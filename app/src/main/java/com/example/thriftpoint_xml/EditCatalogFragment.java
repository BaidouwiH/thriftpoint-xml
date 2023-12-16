package com.example.thriftpoint_xml;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.thriftpoint_xml.models.Product;
import com.example.thriftpoint_xml.viewmodels.MainViewModel;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditCatalogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditCatalogFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "product";

    // TODO: Rename and change types of parameters
    Product product;

    TextInputLayout name;
    TextInputLayout price;
    TextInputLayout description;

    TextInputLayout category;
    MainViewModel viewModel;
    Button submitEdit;

    public EditCatalogFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static EditCatalogFragment newInstance(Product product) {
        EditCatalogFragment fragment = new EditCatalogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, product);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.product = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_catalog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        ExecutorService execEditProduct =
                Executors.newSingleThreadExecutor();
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        name = view.findViewById(R.id.name);
        category = view.findViewById(R.id.category);
        price = view.findViewById(R.id.price);
        description = view.findViewById(R.id.description);
        submitEdit = view.findViewById(R.id.submitEdit);

        name.getEditText().setText(product.getName());
        category.getEditText().setText(product.getCategory());
        price.getEditText().setText("" + product.getPrice());
        description.getEditText().setText(product.getDescription());

        submitEdit.setOnClickListener(v -> {
            Map<String, Object> editedProduct = new HashMap<>();
            editedProduct.put("category", category.getEditText().getText().toString());
            editedProduct.put("name", name.getEditText().getText().toString());
            editedProduct.put("price", Integer.parseInt(price.getEditText().getText().toString()));
            editedProduct.put("description", description.getEditText().getText().toString());
            execEditProduct.submit(() -> {
                viewModel.updateProduct(product.getId(), editedProduct);
            });
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, new AccountFragment())
                    .commit();
        });
    }
}