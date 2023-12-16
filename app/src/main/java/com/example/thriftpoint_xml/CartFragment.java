package com.example.thriftpoint_xml;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.thriftpoint_xml.models.OnAdapterItemClickListener;
import com.example.thriftpoint_xml.models.Product;
import com.example.thriftpoint_xml.recycler_view.ProductOnCartAdapter;
import com.example.thriftpoint_xml.viewmodels.MainViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment implements OnAdapterItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    int totalPrice;
    MainViewModel viewModel;

    RecyclerView rvProducts;


    public CartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
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
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        ProductOnCartAdapter productOnCartAdapter = new ProductOnCartAdapter(
                viewModel.getProductsOnCart().getValue(), this
        );
        rvProducts = view.findViewById(R.id.rvProducts);
        rvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvProducts.setAdapter(productOnCartAdapter);
        BottomNavigationView bottomNavigationView = view.getRootView().findViewById(R.id.bottomNavigationView);
        FloatingActionButton fab = view.getRootView().findViewById(R.id.fab);
        ConstraintLayout cartBottomBar = view.getRootView().findViewById(R.id.cartBottomBar);
        TextView totalPriceTV = view.getRootView().findViewById(R.id.totalPrice);
        if (!viewModel.getProductsOnCart().getValue().isEmpty()) {
            cartBottomBar.setVisibility(View.VISIBLE);
            totalPrice = viewModel.getProductsOnCart().getValue().stream().mapToInt(Product::getPrice).sum();
            totalPriceTV.setText("" + totalPrice);
        }

        Button buyNow = view.getRootView().findViewById(R.id.buyNow);
        buyNow.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, new ConfirmOrderFragment())
                    .addToBackStack("cart")
                    .commit();
        });

        fab.setVisibility(View.INVISIBLE);
        bottomNavigationView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDeleteButtonTapped(Product product) {
        viewModel.removeProductFromCart(product);
        ProductOnCartAdapter productOnCartAdapter = new ProductOnCartAdapter(
                viewModel.getProductsOnCart().getValue(), this
        );
        rvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvProducts.setAdapter(productOnCartAdapter);
    }
}