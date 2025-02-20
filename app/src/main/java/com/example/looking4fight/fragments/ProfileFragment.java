package com.example.looking4fight.fragments;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profilefragment, container, false);

        // Initialize UI elements
        profileImage = view.findViewById(R.id.profile_image);
        userName = view.findViewById(R.id.username);
        userBio = view.findViewById(R.id.user_bio);
        postCount = view.findViewById(R.id.post_count);
        followerCount = view.findViewById(R.id.follower_count);
        followingCount = view.findViewById(R.id.following_count);
        editProfileButton = view.findViewById(R.id.edit_profile_button);
//        addPostButton = view.findViewById(R.id.add_post_button);
        postRecyclerView = view.findViewById(R.id.post_recycler_view);

        userProfileManager = new UserProfileManager();

        // Setup RecyclerView for user posts
        userPosts = new ArrayList<>();
        postAdapter = new ProfilePostAdapter(userPosts);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        postRecyclerView.setAdapter(postAdapter);

        // Load User Profile
        loadUserProfile();
//        loadUserPosts();

        // Set click listeners
        profileImage.setOnClickListener(v -> openGallery());
//        editProfileButton.setOnClickListener(v -> openEditProfileDialog());
        addPostButton.setOnClickListener(v -> openPostCreationDialog());

        return view;
    }

    private void loadUserProfile() {
        userProfileManager.fetchUserProfile((name, bio, profileImageUri, posts, followers, following) -> {
            userName.setText(name);
            userBio.setText(bio);
            postCount.setText(String.valueOf(posts));
            followerCount.setText(String.valueOf(followers));
            followingCount.setText(String.valueOf(following));

            if (profileImageUri != null && !profileImageUri.isEmpty()) {
                Glide.with(requireContext())
                        .load(profileImageUri)
                        .placeholder(R.drawable.loading_bar)
                        .error(R.drawable.error_image)
                        .into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.default_profile);
            }
        });
    }

//    private void loadUserPosts() {
//        userProfileManager.fetchUserPosts(posts -> {
//            userPosts.clear();
//            userPosts.addAll(posts);
//            postAdapter.notifyDataSetChanged();
//        });
//    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == requireActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(requireContext()).load(imageUri).into(profileImage);
            updateProfilePicture();
        }
    }

    private void updateProfilePicture() {
        userProfileManager.updateProfile(userName.getText().toString(), userBio.getText().toString(), imageUri, new UserProfileManager.UpdateCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(requireContext(), "Profile picture updated!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to update profile picture", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openPostCreationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Create New Post");

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_create_post, null);
        builder.setView(dialogView);

        EditText editTextTitle = dialogView.findViewById(R.id.editTextTitle);
        EditText editTextDescription = dialogView.findViewById(R.id.editTextDescription);
        ImageView mediaPreview = dialogView.findViewById(R.id.mediaPreview);
        Button buttonUploadMedia = dialogView.findViewById(R.id.buttonUploadMedia);
        Button buttonSubmitPost = dialogView.findViewById(R.id.buttonSubmitPost);

        buttonUploadMedia.setOnClickListener(v -> openGallery());

        buttonSubmitPost.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();
            if (!title.isEmpty() && !description.isEmpty()) {
                userProfileManager.createPost(title, description, imageUri, new UserProfileManager.PostCallback() {
                    @Override
                    public void onSuccess()
                    {
                        Toast.makeText(requireContext(), "Post added!", Toast.LENGTH_SHORT).show();
//                        loadUserPosts();
                    }
                    @Override
                    public void onFailure(Exception e)
                    {
                        Toast.makeText(requireContext(), "Failed to add post", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
