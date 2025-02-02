package com.example.looking4fight.data.model;

import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfileManager {
    private static final String TAG = "UserProfileManager";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;
    private CollectionReference postsCollection;

    public UserProfileManager() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            postsCollection = db.collection("posts");
        }
    }

    public interface PostCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface UserPostsCallback {
        void onPostsLoaded(List<Post> posts);
        void onFailure(Exception e);
    }

    public interface UserProfileCallback {
        void onProfileLoaded(String name, String bio, String profileImage, long posts, long followers, long following);
    }

    public interface UpdateCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    // Fetch user profile details
    public void fetchUserProfile(final UserProfileCallback callback) {
        if (currentUser == null) return;

        DocumentReference userRef = db.collection("users").document(currentUser.getUid());
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String name = document.getString("name");
                    String bio = document.getString("bio");
                    String profileImage = document.getString("profileImage");
                    long posts = document.getLong("posts") != null ? document.getLong("posts") : 0;
                    long followers = document.getLong("followers") != null ? document.getLong("followers") : 0;
                    long following = document.getLong("following") != null ? document.getLong("following") : 0;

                    callback.onProfileLoaded(name, bio, profileImage, posts, followers, following);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.w(TAG, "Error getting document", task.getException());
            }
        });
    }

    // Create a new post
    public void createPost(String content, Uri mediaUri, PostCallback callback) {
        if (currentUser == null) {
            callback.onFailure(new Exception("User not signed in"));
            return;
        }

        Post newPost = new Post(
                mediaUri != null ? mediaUri.toString() : null,
                content,
                currentUser.getDisplayName(), // Ensure display name is set in Firebase
                0
        );

        postsCollection.add(newPost)
                .addOnSuccessListener(documentReference -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    // Fetch user posts
    public void fetchUserPosts(UserPostsCallback callback) {
        if (currentUser == null) {
            callback.onFailure(new Exception("User not signed in"));
            return;
        }

        postsCollection.whereEqualTo("username", currentUser.getDisplayName()) // Fetch only user's posts
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Post> userPosts = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Post post = document.toObject(Post.class);
                        userPosts.add(post);
                    }
                    callback.onPostsLoaded(userPosts);
                })
                .addOnFailureListener(callback::onFailure);
    }

    // Update profile details
    public void updateProfile(String name, String bio, Uri profileImageUri, final UpdateCallback callback) {
        if (currentUser == null) return;

        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("name", name);
        userUpdates.put("bio", bio);

        if (profileImageUri != null) {
            StorageReference profileImageRef = storage.getReference().child("profile_images/" + currentUser.getUid() + ".jpg");
            profileImageRef.putFile(profileImageUri).addOnSuccessListener(taskSnapshot ->
                    profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        userUpdates.put("profileImage", uri.toString());
                        saveUserData(userUpdates, callback);
                    }));
        } else {
            saveUserData(userUpdates, callback);
        }
    }

    // Save user data to Firestore
    private void saveUserData(Map<String, Object> data, final UpdateCallback callback) {
        db.collection("users").document(currentUser.getUid()).set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }
}
