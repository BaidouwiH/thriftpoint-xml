package com.example.thriftpoint_xml;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thriftpoint_xml.viewmodels.MainViewModel;
import com.google.android.material.textfield.TextInputLayout;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddProductFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ImageButton imageButton;
    Button addProduct;

    Uri imageUri;

    TextInputLayout name;
    TextInputLayout price;
    TextInputLayout description;

    TextInputLayout category;
    String picturePath;
    MainViewModel viewModel;
    ActivityResultLauncher<Intent> resultLauncher;

    public AddProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddProductFragment newInstance(String param1, String param2) {
        AddProductFragment fragment = new AddProductFragment();
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
        return inflater.inflate(R.layout.fragment_add_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ExecutorService execUploadImage =
                Executors.newSingleThreadExecutor();
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        imageButton = view.findViewById(R.id.imageButton);
        addProduct = view.findViewById(R.id.addProduct);
        name = view.findViewById(R.id.name);
        category = view.findViewById(R.id.category);
        price = view.findViewById(R.id.price);
        description = view.findViewById(R.id.description);

        registerResult();
        imageButton.setOnClickListener(v -> pickImage());
        addProduct.setOnClickListener(v -> {
            execUploadImage.submit(() -> {
                try {
                    uploadProduct(viewModel);
                    uploadImage(picturePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, new HomeFragment())
                        .commit();
            });
        });
    }

    private void pickImage() {
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        resultLauncher.launch(intent);
    }

    private void registerResult() {
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    try {
                        imageUri = result.getData().getData();
                        picturePath = getPath(getActivity().getApplicationContext(), imageUri);
                        imageButton.setImageURI(imageUri);
                        imageButton.setPadding(0, 0, 0, 0);
                        ViewGroup.LayoutParams params = imageButton.getLayoutParams();
                        params.height = 450;
                        params.width = 600;
                        imageButton.setLayoutParams(params);
                        imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "No Image selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void uploadProduct(MainViewModel viewModel) {
        Map<String, Object> createdProduct = new HashMap<>();
        String[] splittedPicturePath = picturePath.split("/");

        createdProduct.put("category", category.getEditText().getText().toString());
        createdProduct.put("image_res", "images/thriftpoint/" + splittedPicturePath[splittedPicturePath.length - 1]);
        createdProduct.put("name", name.getEditText().getText().toString());
        createdProduct.put("price", Integer.parseInt(price.getEditText().getText().toString()));
        createdProduct.put("publisher", viewModel.getUserData().getValue().name);
        createdProduct.put("description", description.getEditText().getText().toString());
        viewModel.addNewProduct(createdProduct);
    }

    private void uploadImage(String filePath) throws URISyntaxException, InvalidKeyException,
            StorageException, IOException {

        String connectionString = "DefaultEndpointsProtocol=https;AccountName=guspascad;AccountKey=" +
                "ZBnfKtEVn2rjgYyHvlznRQYZ3IRyaRYKkGGD+wmir1+a341tOgxDDc4wj3dpxsCQYQDirUo892vW+AStrdZO1A" +
                "==;EndpointSuffix=core.windows.net";
        CloudStorageAccount storageAccount = CloudStorageAccount.parse(connectionString);

        CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

        CloudBlobContainer container = blobClient.getContainerReference("democontainer");

        String[] splittedPicturePath = picturePath.split("/");
        picturePath = splittedPicturePath[splittedPicturePath.length - 1];

        CloudBlockBlob blob = container.getBlockBlobReference("images/thriftpoint/" + picturePath);
        File source = new File(filePath);
        blob.upload(Files.newInputStream(source.toPath()), -1);
    }

    public static String getPath(Context context, Uri uri) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close();
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }
}