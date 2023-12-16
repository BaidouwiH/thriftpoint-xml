package com.example.thriftpoint_xml;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.thriftpoint_xml.viewmodels.MainViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);

        ExecutorService execSignUp =
                Executors.newSingleThreadExecutor();
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        TextInputLayout emailInput = findViewById(R.id.email);
        TextInputLayout passwordInput = findViewById(R.id.password);
        TextInputLayout nameInput = findViewById(R.id.name);
        ProgressBar progressBar = findViewById(R.id.progress_circular);
        mAuth = FirebaseAuth.getInstance();
        Button signUp = findViewById(R.id.signup);
        signUp.setOnClickListener(v -> {
            String email = emailInput.getEditText().getText().toString();
            String password = passwordInput.getEditText().getText().toString();
            String name = nameInput.getEditText().getText().toString();

            if (!email.isEmpty() && !password.isEmpty()) {
                signUp.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                signUp.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                                FirebaseUser user = mAuth.getCurrentUser();
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("name", name);
                                userData.put("email", email);
                                userData.put("fav_products", new ArrayList<Map<String, Object>>());
                                userData.put("products_on_cart", new ArrayList<Map<String, Object>>());

                                execSignUp.execute(() -> viewModel.addNewUserData(user.getUid(), userData));

                                Intent intent = new Intent(this, MainScreen.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        TextView login = findViewById(R.id.login);
        login.setOnClickListener(view -> {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        });
    }
}
