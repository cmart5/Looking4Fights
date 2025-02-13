package com.example.looking4fight.ui.login;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.looking4fight.R;
import com.example.looking4fight.data.model.UserProfileManager;
import com.example.looking4fight.data.model.Post;
import com.example.looking4fight.ui.login.adapter.ProfilePostAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    private ImageView profileImage;
    private TextView userName, userBio, postCount, followerCount, followingCount;
    private Button editProfileButton;
    private FloatingActionButton addPostButton;
    private RecyclerView postRecyclerView;
    private ProfilePostAdapter postAdapter;
    private List<Post> userPosts;
    private UserProfileManager userProfileManager;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private boolean hasProfilePicture = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profilefragment, container, false);

        // Initialize UI elements
        profileImage = view.findViewById(R.id.profile_image);
        userName = view.findViewById(R.id.email);
        userBio = view.findViewById(R.id.user_bio);
        postCount = view.findViewById(R.id.post_count);
        followerCount = view.findViewById(R.id.follower_count);
        followingCount = view.findViewById(R.id.following_count);
        editProfileButton = view.findViewById(R.id.edit_profile_button);
        addPostButton = view.findViewById(R.id.add_post_button);
        postRecyclerView = view.findViewById(R.id.post_recycler_view);

        userProfileManager = new UserProfileManager();

        // Setup RecyclerView for user posts
        userPosts = new ArrayList<>();
        postAdapter = new ProfilePostAdapter(userPosts);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        postRecyclerView.setAdapter(postAdapter);

        // Load User Profile
        userProfileManager.fetchUserProfile(new UserProfileManager.UserProfileCallback() {
            @Override
            public void onProfileLoaded(String name, String bio, String profileImageUri, long posts, long followers, long following) {
                userName.setText(name);
                userBio.setText(bio);
                postCount.setText(String.valueOf(posts));
                followerCount.setText(String.valueOf(followers));
                followingCount.setText(String.valueOf(following));

                // Load profile image using Glide
                if (profileImageUri != null && !profileImageUri.isEmpty()) {
                    hasProfilePicture = true;
                    Glide.with(requireContext())
                            .load(profileImageUri)
                            .placeholder(R.drawable.loading_bar)
                            .error(R.drawable.error_image)
                            .into(profileImage);
                } else {
                    hasProfilePicture = false;
                    profileImage.setImageResource(R.drawable.default_profile);
                }
            }
        });

        // Load only the user's posts
        loadUserPosts();

        // Make Profile Image Clickable for Upload
        profileImage.setOnClickListener(v -> openGallery());

        // Open Edit Profile Dialog for Name & Bio
        editProfileButton.setOnClickListener(v -> openEditProfileDialog());

        // Add Post Button Click
        addPostButton.setOnClickListener(v -> openPostCreationDialog());

        return view;
    }

    // Open Edit Profile Dialog for Name & Bio
    private void openEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Profile");

        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);

        EditText editUserName = dialogView.findViewById(R.id.edit_username);
        EditText editUserBio = dialogView.findViewById(R.id.edit_bio);

        // Pre-fill fields with existing user data
        editUserName.setText(userName.getText().toString());
        editUserBio.setText(userBio.getText().toString());

        // Auto-highlight text for quick editing
        editUserName.requestFocus();
        editUserName.selectAll();
        editUserBio.selectAll();

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newUserName = editUserName.getText().toString();
            String newUserBio = editUserBio.getText().toString();

            // Update UI
            userName.setText(newUserName);
            userBio.setText(newUserBio);

            // Save changes to Firestore or local storage
            userProfileManager.updateProfile(newUserName, newUserBio, imageUri, new UserProfileManager.UpdateCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Open Image Picker for Profile Picture
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), PICK_IMAGE_REQUEST);
    }

    // Handle Image Selection
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == requireActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            // Use Glide to instantly preview the new image
            Glide.with(requireContext())
                    .load(imageUri)
                    .placeholder(R.drawable.project_logo)
                    .error(R.drawable.error_image)
                    .into(profileImage);

            // Update profile picture in Firestore
            updateProfilePicture();
        }
    }

    // Save Profile Picture
    private void updateProfilePicture() {
        userProfileManager.updateProfile(userName.getText().toString(), userBio.getText().toString(), imageUri, new UserProfileManager.UpdateCallback() {
            @Override
            public void onSuccess() {
                if (imageUri != null) {
                    Glide.with(requireContext()).load(imageUri).into(profileImage);
                }
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Load user posts
    private void loadUserPosts() {
        userProfileManager.fetchUserPosts(new UserProfileManager.UserPostsCallback() {
            @Override
            public void onPostsLoaded(List<Post> posts) {
                userPosts.clear();
                userPosts.addAll(posts);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Open dialog to create a new post
    private void openPostCreationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Create New Post");

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_post, null);
        builder.setView(dialogView);

        EditText postContent = dialogView.findViewById(R.id.edit_post_content);

        builder.setPositiveButton("Post", (dialog, which) -> {
            String content = postContent.getText().toString().trim();
            if (!content.isEmpty()) {
                userProfileManager.createPost(content, null, new UserProfileManager.PostCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(requireContext(), "Post added!", Toast.LENGTH_SHORT).show();
                        loadUserPosts();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(requireContext(), "Failed to add post", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
