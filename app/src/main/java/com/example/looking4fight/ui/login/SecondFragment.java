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

    // Example upload function (simulating upload)
    private void uploadFile(Uri fileUri) {
        uploadProgressBar.setVisibility(View.VISIBLE);
        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(fileUri)) {
            byte[] fileContent = readBytesFromStream(inputStream);
            uploadProgressBar.setVisibility(View.GONE);

            // Use Glide to display the image in the ImageView
            Glide.with(requireContext())
                    .load(fileUri)
                    .into(uploadedImageView);

        } catch (IOException e) {
            e.printStackTrace();
            uploadProgressBar.setVisibility(View.GONE);
        }
    }

    // Helper function to read InputStream as byte array
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