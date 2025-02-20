package com.example.looking4fight.fragments;

import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.*;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
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
    private FrameLayout skeletonLayout;
    private LinearLayout contentLayout;

    private ImageView profileImage;
    private TextView userName, userBio, postCount, followerCount, followingCount;
    private TextView userHeight, userWeight, userReach, userLocation, userGym;
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

        //Load skeleton view before content
        skeletonLayout = view.findViewById(R.id.skeleton_layout);
        contentLayout = view.findViewById(R.id.content_layout);

        //Reset initial state of layouts
        skeletonLayout.setVisibility(View.VISIBLE); // Show skeleton by default
        skeletonLayout.setAlpha(1f); // Ensure skeleton is fully visible
        skeletonLayout.setTranslationY(0f);

        contentLayout.setVisibility(View.GONE); // Hide content by default
        contentLayout.setAlpha(0f); // Ensure content starts invisible
        contentLayout.setTranslationY(50f);

        // Initialize UI elements
        profileImage = view.findViewById(R.id.profile_image);
        userName = view.findViewById(R.id.username);
        userBio = view.findViewById(R.id.user_bio);
        postCount = view.findViewById(R.id.post_count);
        followerCount = view.findViewById(R.id.follower_count);
        followingCount = view.findViewById(R.id.following_count);
        userHeight = view.findViewById(R.id.user_height);
        userWeight = view.findViewById(R.id.user_weight);
        userReach = view.findViewById(R.id.user_reach);
        userLocation = view.findViewById(R.id.user_location);
        userGym = view.findViewById(R.id.user_gym);
        editProfileButton = view.findViewById(R.id.edit_profile_button);
//        addPostButton = view.findViewById(R.id.add_post_button);
        postRecyclerView = view.findViewById(R.id.post_recycler_view);
        userProfileManager = new UserProfileManager();
        setupAutoHighlight(view);

        // Setup RecyclerView for user posts
        userPosts = new ArrayList<>();
        postAdapter = new ProfilePostAdapter(userPosts);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        postRecyclerView.setAdapter(postAdapter);

        //Show skeleton while loading
        showSkeleton();
        //Load user posts from firestore
        loadUserPosts();

        // Load User Profile
        userProfileManager.fetchUserProfile(new UserProfileManager.UserProfileCallback() {
            @Override
            public void onProfileLoaded(String name, String bio, String profileImageUri, long posts, long followers, long following,
                                        String height, String weight, String reach, String location, String gym) {

                //hide skeleton and show content
                hideSkeleton();

                userName.setText(name != null ? name : "Unknown User");
                userBio.setText(bio != null ? bio : "No bio available.");
                postCount.setText(String.valueOf(posts));
                followerCount.setText(String.valueOf(followers));
                followingCount.setText(String.valueOf(following));
                userHeight.setText((height != null ? height : "N/A"));
                userWeight.setText((weight != null ? weight : "N/A"));
                userReach.setText((reach != null ? reach : "N/A"));
                userLocation.setText((location != null ? location : "N/A"));
                userGym.setText((gym != null ? gym : "N/A"));

                // Load profile image using Glide
                if (profileImageUri != null && !profileImageUri.isEmpty()) {
                    Glide.with(requireContext())
                            .load(profileImageUri)
                            .placeholder(R.drawable.loading_bar)
                            .error(R.drawable.error_image)
                            .into(profileImage);
                } else {
                    profileImage.setImageResource(R.drawable.default_profile);
                }
            }
        });

        // Make Profile Image Clickable for Upload
        profileImage.setOnClickListener(v -> openGallery());

        // Open Edit Profile Dialog for Name & Bio
        editProfileButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Edit Profile");

            // Inflate the dialog layout
            View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null);
            builder.setView(dialogView);
            setupAutoHighlight(dialogView);

            EditText editUserName = dialogView.findViewById(R.id.edit_username);
            EditText editUserBio = dialogView.findViewById(R.id.edit_bio);
            EditText editHeight = dialogView.findViewById(R.id.edit_height);
            EditText editWeight = dialogView.findViewById(R.id.edit_weight);
            EditText editReach = dialogView.findViewById(R.id.edit_reach);
            EditText editLocation = dialogView.findViewById(R.id.edit_location);
            EditText editGym = dialogView.findViewById(R.id.edit_gym);

            // Pre-fill fields with existing user data
            editUserName.setText(userName.getText().toString());
            editUserBio.setText(userBio.getText().toString());
            editHeight.setText(userHeight.getText().toString().replace("Height: ", ""));
            editWeight.setText(userWeight.getText().toString().replace("Weight: ", ""));
            editReach.setText(userReach.getText().toString().replace("Reach: ", ""));
            editLocation.setText(userLocation.getText().toString().replace("Location: ", ""));
            editGym.setText(userGym.getText().toString().replace("Gym: ", ""));

            builder.setPositiveButton("Save", (dialog, which) -> {
                String newUserName = editUserName.getText().toString();
                String newUserBio = editUserBio.getText().toString();
                String newHeight = editHeight.getText().toString();
                String newWeight = editWeight.getText().toString();
                String newReach = editReach.getText().toString();
                String newLocation = editLocation.getText().toString();
                String newGym = editGym.getText().toString();

                // Update UI
                userName.setText(newUserName);
                userBio.setText(newUserBio);
                userHeight.setText("Height: " + newHeight);
                userWeight.setText("Weight: " + newWeight);
                userReach.setText("Reach: " + newReach);
                userLocation.setText("Location: " + newLocation);
                userGym.setText("Gym: " + newGym);

                // Save changes to Firestore
                userProfileManager.updateProfile(newUserName, newUserBio, imageUri, newHeight, newWeight, newReach, newLocation, newGym, new UserProfileManager.UpdateCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            dialog.show();
        });

        return view;
    }
    private boolean isAnimating = false;

    private void showSkeleton() {
        if (isAnimating) return;
        isAnimating = true;

        contentLayout.setAlpha(1f);
        contentLayout.setTranslationY(0f);
        skeletonLayout.setAlpha(0f);
        skeletonLayout.setTranslationY(-50f);

        contentLayout.animate()
                .alpha(0f)
                .translationY(50f)
                .setDuration(800)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    contentLayout.setVisibility(View.GONE);
                    skeletonLayout.setVisibility(View.VISIBLE);
                    skeletonLayout.animate()
                            .alpha(1f)
                            .translationY(0f)
                            .setDuration(800)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .withEndAction(() -> isAnimating = false)
                            .start();
                })
                .start();
    }

    private void hideSkeleton() {
        if (isAnimating) return;
        isAnimating = true;

        skeletonLayout.setAlpha(1f);
        skeletonLayout.setTranslationY(0f);
        contentLayout.setAlpha(0f);
        contentLayout.setTranslationY(50f);

        skeletonLayout.animate()
                .alpha(0f)
                .translationY(-50f)
                .setDuration(800)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    skeletonLayout.setVisibility(View.GONE);
                    contentLayout.setVisibility(View.VISIBLE);
                    contentLayout.animate()
                            .alpha(1f)
                            .translationY(0f)
                            .setDuration(800)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .withEndAction(() -> isAnimating = false)
                            .start();
                })
                .start();
    }

    private void setupAutoHighlight(View view) {
        int[] editTextIds = {
                R.id.edit_username,
                R.id.edit_bio,
                R.id.edit_height,
                R.id.edit_weight,
                R.id.edit_reach,
                R.id.edit_location,
                R.id.edit_gym
        };
        for (int editTextId : editTextIds) {
            EditText editText = view.findViewById(editTextId);
            if (editText != null) {
                editText.setFocusable(true); //Ensure edit text is focusable
                editText.setOnFocusChangeListener((v, hasFocus) -> { //use post to ensure 'selectall' is called
                    if (hasFocus) {
                        editText.post(editText::selectAll);
                    }
                });
            }
        }
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
        userProfileManager.updateProfile(userName.getText().toString(), userBio.getText().toString(), imageUri, userHeight.getText().toString(), userWeight.getText().toString(),
                userReach.getText().toString(), userLocation.getText().toString(), userGym.getText().toString(), new UserProfileManager.UpdateCallback() {
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
                Log.d("ProfileFragment", "Fetched " + posts.size() + " posts");
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

}
