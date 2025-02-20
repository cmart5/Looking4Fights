package com.example.looking4fight.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.looking4fight.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.*;

public class CreatePostFragment extends DialogFragment {

    private ActivityResultLauncher<Intent> galleryLauncher;
    private ImageView mediaPreview;
    private Button buttonSubmitPost;
    private EditText titleInput, descriptionInput;
    private Uri selectedMediaUri;

    public static CreatePostFragment newInstance() {
        return new CreatePostFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedMediaUri = result.getData().getData();
                        if (selectedMediaUri != null) {
                            mediaPreview.setImageURI(selectedMediaUri);
                            mediaPreview.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_create_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mediaPreview = view.findViewById(R.id.mediaPreview);
        titleInput = view.findViewById(R.id.editTextTitle);
        descriptionInput = view.findViewById(R.id.editTextDescription);
        Button buttonUploadMedia = view.findViewById(R.id.buttonUploadMedia);
        buttonSubmitPost = view.findViewById(R.id.buttonSubmitPost);

        buttonUploadMedia.setOnClickListener(v -> openGallery());

        ImageButton closeButton = view.findViewById(R.id.imageButton);
        closeButton.setOnClickListener(v -> dismiss());

        buttonSubmitPost.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();

            if (selectedMediaUri == null) {
                Toast.makeText(getContext(), "Please select an image or video", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
                Toast.makeText(getContext(), "Title and description cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadPostToFirebase(selectedMediaUri, title, description);
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("*/*");
        String[] mimeTypes = {"image/*", "video/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        galleryLauncher.launch(intent);
    }

    private void uploadPostToFirebase(Uri selectedMediaUri, String title, String description) {
        String fileName = "posts/" + UUID.randomUUID().toString();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(fileName);

        storageRef.putFile(selectedMediaUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> savePostToFirestore(uri.toString(), title, description)))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void savePostToFirestore(String mediaUrl, String title, String description) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Get current timestamp in milliseconds
        long timestampMillis = System.currentTimeMillis();

        // Convert timestamp to a formatted date string (e.g., "18-Feb-2025 15:31:38")
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault());
        String formattedDate = sdf.format(new Date(timestampMillis));

        Map<String, Object> post = new HashMap<>();
        post.put("mediaUrl", mediaUrl);
        post.put("title", title);
        post.put("description", description);
        post.put("userId", auth.getUid());
        post.put("timestampMillis", timestampMillis);  // Keep the original timestamp
        post.put("timestampFormatted", formattedDate); // Store human-readable date

        db.collection("posts").add(post)
                .addOnSuccessListener(documentReference -> dismiss())
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to save post", Toast.LENGTH_SHORT).show();
                });
    }
}
