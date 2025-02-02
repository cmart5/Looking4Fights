package com.example.looking4fight.data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;

public class LoginRepository {
    private static volatile LoginRepository instance;
    private FirebaseAuth mAuth;

    // Private constructor: Singleton
    private LoginRepository() {
        mAuth = FirebaseAuth.getInstance();
    }

    public static LoginRepository getInstance() {
        if (instance == null) {
            instance = new LoginRepository();
        }
        return instance;
    }

    // Check if user is logged in
    public boolean isLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    // Get current user
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    // Log out user
    public void logout() {
        mAuth.signOut();
    }

    // Login user with email & password
    public void loginUser(String email, String password, final LoginCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(mAuth.getCurrentUser());
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    // Register user with email & password
    public void registerUser(String email, String password, final LoginCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(mAuth.getCurrentUser());
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    // 🔹 Google Login
    public void loginWithGoogle(String idToken, final LoginCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(mAuth.getCurrentUser());
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    // 🔹 Facebook Login
    public void loginWithFacebook(String accessToken, final LoginCallback callback) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(mAuth.getCurrentUser());
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    // Callback interface for login/register operations
    public interface LoginCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(Exception e);
    }
}
