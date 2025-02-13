package com.example.looking4fight.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.looking4fight.fragments.MainActivity;
import com.example.looking4fight.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private EditText emailField, passwordField, confirmPasswordField;
    private Button registerButton, loginRedirectButton;
    private ProgressBar loadingProgressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // UI References
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        confirmPasswordField = findViewById(R.id.confirm_password);
        registerButton = findViewById(R.id.register);
        loginRedirectButton = findViewById(R.id.redirect_login);
        loadingProgressBar = findViewById(R.id.loading);

        // Register Button Click
        registerButton.setOnClickListener(v -> registerUser());

        // Redirect to Login Page
        loginRedirectButton.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String confirmPassword = confirmPasswordField.getText().toString().trim();

        // Validate Fields
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Email is required!");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Password is required!");
            return;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordField.setError("Passwords do not match!");
            return;
        }

        loadingProgressBar.setVisibility(View.VISIBLE);

        // Create User in Firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    loadingProgressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                        navigateToMainActivity(user);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Registration Error", task.getException());
                    }
                });
    }

    private void navigateToMainActivity(FirebaseUser user) {
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        finish();
    }
}
