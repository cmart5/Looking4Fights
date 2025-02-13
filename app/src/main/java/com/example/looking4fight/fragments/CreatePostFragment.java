package com.example.looking4fight.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.looking4fight.R;
import com.example.looking4fight.data.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreatePostFragment extends Fragment
{
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ImageView mediaPreview;
    private Button buttonSubmitPost;
    private Uri selectedMediaUri;


    public CreatePostFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result ->
                {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null)
                    {
                        Uri selectedMediaUri = result.getData().getData();
                        if (selectedMediaUri != null)
                        {
                            mediaPreview.setImageURI(selectedMediaUri); // Show selected media
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_create_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mediaPreview = view.findViewById(R.id.mediaPreview);
        Button buttonUploadMedia = view.findViewById(R.id.buttonUploadMedia);
        buttonSubmitPost = view.findViewById(R.id.buttonSubmitPost);

        buttonUploadMedia.setOnClickListener(v -> openGallery());

        // Close button functionality
        ImageButton closeButton = view.findViewById(R.id.imageButton);
        closeButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        buttonSubmitPost.setOnClickListener(v ->
        {
            if (selectedMediaUri != null)
            {
                uploadPostToFirebase(selectedMediaUri, "New Post"); // Pass selected media and caption
            }
            else
            {
                Toast.makeText(getContext(), "Please select an image or video", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*"); // Only images

        // Allow both images and videos
        intent.setType("*/*");
        String[] mimeTypes = {"image/*", "video/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        galleryLauncher.launch(intent);
    }

    private void uploadPostToFirebase(Uri selectedMediaUri, String caption)
    {
        if (selectedMediaUri == null)
        {
            Toast.makeText(getContext(), "Please select an image or video", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a unique filename
        String fileName = "posts/" + UUID.randomUUID().toString();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(fileName);

        storageRef.putFile(selectedMediaUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                        .addOnSuccessListener(uri ->
                        {
                            String mediaUrl = uri.toString();
                            savePostToFirestore(mediaUrl, caption);
                        }))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void savePostToFirestore(String mediaUrl, String caption)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Post newPost = new Post(
                mediaUrl,
                "New Post", // Change this to user input if needed
                "User123",  // Change this to actual username
                0 // Initial like count
        );

        db.collection("posts")
                .add(newPost)
                .addOnSuccessListener(documentReference ->
                {
                    Toast.makeText(getContext(), "Post uploaded successfully!", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error uploading post", Toast.LENGTH_SHORT).show());
    }
}

