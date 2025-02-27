package com.example.looking4fight.ui.login;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.looking4fight.fragments.MainActivity;
import com.example.looking4fight.R;
import com.example.looking4fight.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_GOOGLE_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        //automatically goes to main activity if current user is logged in
        if(mAuth.getCurrentUser() != null){
            navigateToMainActivity(mAuth.getCurrentUser());
        }

        // Initialize Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Web Client ID
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // UI Elements
        SignInButton googleSignInButton = findViewById(R.id.google_sign_in);
        Button loginButton = findViewById(R.id.login);
        Button registerButton = findViewById(R.id.register);
        Button forgotPasswordButton = findViewById(R.id.btn_forgot_password);
        ProgressBar loadingProgressBar = findViewById(R.id.loading);

        // Google Sign-In
        googleSignInButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            signInWithGoogle();
        });

        // **Login Button Click Listener**
        loginButton.setOnClickListener(v ->
        {
            String email = binding.email.getText().toString().trim();
            String password = binding.password.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty())
            {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task ->
                        {
                            loadingProgressBar.setVisibility(View.GONE);
                            if (task.isSuccessful())
                            {
                                FirebaseUser user = mAuth.getCurrentUser();
                                navigateToMainActivity(user);
                            } else
                            {
                                Toast.makeText(LoginActivity.this, "Login failed. Check credentials.", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else
            {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            }
        });

        // **Register Button Click Listener**
        registerButton.setOnClickListener(v -> {
            // Navigate to RegisterActivity
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        // **Forgot Password Button Click Listener**
        forgotPasswordButton.setOnClickListener(v -> {
            Dialog forgotPasswordDialog = new Dialog(LoginActivity.this);
            forgotPasswordDialog.setContentView(R.layout.dialog_reset_password);
            forgotPasswordDialog.show();
            Button cancelButton = forgotPasswordDialog.findViewById(R.id.btn_cancel);
            Button sendEmailButton = forgotPasswordDialog.findViewById(R.id.btn_confirm);

            //canceling
            cancelButton.setOnClickListener(v1 -> {
                forgotPasswordDialog.dismiss();
            });

            //sending password reset email
            sendEmailButton.setOnClickListener(v1 -> {
                try {
                    EditText et = forgotPasswordDialog.findViewById(R.id.password_reset_email);
                    String email = et.getText().toString().trim();
                    if(email.isEmpty())
                    {
                        Toast.makeText(LoginActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            forgotPasswordDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Email sent.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                catch(Exception e)
                {
                    Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            });

        });
    }

    // Google Sign-In
    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task ->
                {
                    if (task.isSuccessful())
                    {
                        FirebaseUser user = mAuth.getCurrentUser();
                        navigateToMainActivity(user);
                    } else
                    {
                        Toast.makeText(LoginActivity.this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Handle activity results (Google only)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_SIGN_IN)
        {
            try
            {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data)
                        .getResult(ApiException.class);
                if (account != null)
                {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e)
            {
                Log.e(TAG, "Google sign in failed", e);
                Toast.makeText(this, "Google sign-in failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Navigate to Main Activity after successful login
    private void navigateToMainActivity(FirebaseUser user)
    {
        Toast.makeText(this, "Welcome " + (user != null ? user.getDisplayName() : "User"), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}
