package com.example.looking4fight.ui.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.looking4fight.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SecondFragment extends Fragment {
    private static final int PICK_FILE_REQUEST = 1;
    private ImageView uploadedImageView;
    private ProgressBar uploadProgressBar;
    private Button uploadButton;
    private Uri fileUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        // Initialize UI elements
        uploadedImageView = view.findViewById(R.id.uploadedImageView);
        uploadProgressBar = view.findViewById(R.id.uploadProgressBar);
        uploadButton = view.findViewById(R.id.uploadButton);

        uploadProgressBar.setVisibility(View.GONE); // Hide progress bar initially

        // Set up button click listener to open the file picker
        uploadButton.setOnClickListener(v -> openFileChooser());

        return view;
    }

    // Launch file picker to select photos or videos
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/* video/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FILE_REQUEST);
    }

    // Handle the result from file selection
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == requireActivity().RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            uploadFile(fileUri);
        }
    }

    // Upload file to Firebase Storage and save download URL to Firestore
    private void uploadFile(Uri fileUri) {
        uploadProgressBar.setVisibility(View.VISIBLE);

        // Get Firebase Storage reference
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child("profile_images/" + System.currentTimeMillis() + ".jpg");

        // Upload file to Firebase Storage
        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL after successful upload
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();

                        // Save the download URL to Firestore
                        saveToFirestore(downloadUrl);

                        // Display the uploaded image using Glide
                        Glide.with(requireContext())
                                .load(downloadUrl)
                                .into(uploadedImageView);

                        uploadProgressBar.setVisibility(View.GONE);
                    }).addOnFailureListener(e -> {
                        e.printStackTrace();
                        uploadProgressBar.setVisibility(View.GONE);
                    });
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    uploadProgressBar.setVisibility(View.GONE);
                });
    }

    // Save the download URL to Firestore
    private void saveToFirestore(String downloadUrl) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Update Firestore document with the download URL
        db.collection("users").document(userId)
                .update("profileImage", downloadUrl)
                .addOnSuccessListener(aVoid -> {
                    // Successfully updated Firestore
                    System.out.println("Profile image URL saved successfully!");
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                    e.printStackTrace();
                });
    }

    // Helper function to read InputStream as byte array (not used anymore but retained if needed)
    private byte[] readBytesFromStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024]; // 1KB buffer size
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        return outputStream.toByteArray();
    }
}
