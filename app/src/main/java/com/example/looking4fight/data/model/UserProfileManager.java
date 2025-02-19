package com.example.looking4fight.data.model;

import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.CollectionReference;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

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

        if (currentUser == null) {
            Log.w(TAG, "Current user is null. Authentication may have failed.");
        } else {
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
        void onProfileLoaded(String name, String bio, String profileImage, long posts, long followers, long following,
                             String height, String weight, String reach, String location, String gym);

    }

    public interface UpdateCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    // Fetch user profile details, including the new fields
    public void fetchUserProfile(final UserProfileCallback callback) {
        if (currentUser == null) {
            Log.e(TAG, "Current user is null. Cannot fetch profile.");
            return;
        }

        String userId = currentUser.getUid();
        Log.d(TAG, "Fetching profile for UID: " + userId);

        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "Document data: " + document.getData());

                    // Retrieve all fields with fallback values
                    String name = document.getString("name") != null ? document.getString("name") : "Unknown User";
                    String bio = document.getString("bio") != null ? document.getString("bio") : "No bio available.";
                    String profileImage = document.getString("profileImage") != null ? document.getString("profileImage") : "";
                    long posts = document.getLong("posts") != null ? document.getLong("posts") : 0;
                    long followers = document.getLong("followers") != null ? document.getLong("followers") : 0;
                    long following = document.getLong("following") != null ? document.getLong("following") : 0;
                    String height = document.getString("height") != null ? document.getString("height") : "N/A";
                    String weight = document.getString("weight") != null ? document.getString("weight") : "N/A";
                    String reach = document.getString("reach") != null ? document.getString("reach") : "N/A";
                    String location = document.getString("location") != null ? document.getString("location") : "N/A";
                    String gym = document.getString("gym") != null ? document.getString("gym") : "N/A";

                    // Trigger callback with retrieved data
                    callback.onProfileLoaded(name, bio, profileImage, posts, followers, following, height, weight, reach, location, gym);

                    Log.d(TAG, "User profile successfully loaded.");
                } else {
                    Log.d(TAG, "No document found for UID: " + userId);
                    callback.onProfileLoaded(
                            "Unknown User", "No bio available.", "", 0, 0, 0, "N/A", "N/A", "N/A", "N/A", "N/A"
                    );
                }
            } else {
                Log.e(TAG, "Error fetching document: ", task.getException());
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
                mediaUri != null ? mediaUri.toString() : "", // Ensure mediaUrl is not null
                "Untitled Post", // Provide a default title if missing
                content,
                currentUser.getUid(), // Use UID instead of displayName for consistency
                System.currentTimeMillis() // Add a timestamp
        );

        postsCollection.add(newPost)
                .addOnSuccessListener(documentReference -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    // Fetch user posts (no changes needed here)
    public void fetchUserPosts(UserPostsCallback callback) {
        if (currentUser == null) {
            callback.onFailure(new Exception("User not signed in"));
            return;
        }

        postsCollection.whereEqualTo("username", currentUser.getDisplayName())
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

    // Update profile details, including the new fields
    public void updateProfile(String name, String bio, Uri profileImageUri, String height, String weight, String reach,
                              String location, String gym, final UpdateCallback callback) {
        if (currentUser == null) {
            Log.e(TAG, "User is not signed in.");
            callback.onFailure(new Exception("User is not signed in."));
            return;
        }

        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("name", name);
        userUpdates.put("bio", bio);
        userUpdates.put("height", height);
        userUpdates.put("weight", weight);
        userUpdates.put("reach", reach);
        userUpdates.put("location", location);
        userUpdates.put("gym", gym);

        Log.d(TAG, "Updating profile with data: " + userUpdates);

        if (profileImageUri != null) {
            StorageReference profileImageRef = storage.getReference().child("profile_images/" + currentUser.getUid() + ".jpg");

            Log.d(TAG, "Storage Path: " + profileImageRef.getPath());
            Log.d(TAG, "User ID: " + currentUser.getUid());

            // Upload the profile image to Firebase Storage
            profileImageRef.putFile(profileImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Retrieve the download URL once the upload succeeds
                        profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            userUpdates.put("profileImage", uri.toString());
                            Log.d(TAG, "Profile image uploaded successfully: " + uri.toString());

                            // Save all updates, including the profile image URL
                            saveUserData(userUpdates, callback);
                        }).addOnFailureListener(e -> {
                            Log.e(TAG, "Failed to get profile image download URL", e);
                            callback.onFailure(e);
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to upload profile image", e);
                        callback.onFailure(e);
                    });
        } else {
            // If no new profile image is provided, save other updates directly
            saveUserData(userUpdates, callback);
        }
    }




    // Save user data to Firestore
    private void saveUserData(Map<String, Object> data, final UpdateCallback callback) {
        Log.d(TAG, "Saving data to Firestore: " + data);

        db.collection("users").document(currentUser.getUid()).set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Profile saved successfully.");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving profile: ", e);
                    callback.onFailure(e);
                });
    }

}
