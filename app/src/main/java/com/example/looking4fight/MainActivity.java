package com.example.looking4fight;
import android.net.Uri;
import java.io.*;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.Nullable;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.NavigationUI;
import com.example.looking4fight.databinding.ActivityMainBinding;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {
        private static final int PICK_FILE_REQUEST = 1;  // Request code for file picker
        private static final String TAG = "MainActivity";
        private ActivityMainBinding binding; // ViewBinding instance

        // Create the toolbar
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            // Set up the toolbar
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            // Set up button click listener to open the file picker
            binding.uploadButton.setOnClickListener(v -> openFileChooser());

            // Hide progress bar initially
            binding.uploadProgressBar.setVisibility(View.GONE);
        }

        // Launch file picker to select photos or videos
        private void openFileChooser() {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/* video/*"); // Allow selection of both images and videos
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FILE_REQUEST);
        }

        // Handle the result from file selection
        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                Uri fileUri = data.getData(); // URI of the selected file
                uploadFile(fileUri); // Pass the URI to the upload function
            }
        }

        // Example upload function (simulating upload)
        private void uploadFile(Uri fileUri) {
            // Show progress bar while uploading
            binding.uploadProgressBar.setVisibility(View.VISIBLE);

            try (InputStream inputStream = getContentResolver().openInputStream(fileUri)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    byte[] fileContent = inputStream.readAllBytes(); // Reads all bytes (Android 33+)
                    Log.d(TAG, "File content successfully read, size: " + fileContent.length + " bytes.");
                } else {
                    byte[] fileContent = new byte[inputStream.available()];
                    inputStream.read(fileContent);
                    Log.d(TAG, "File content successfully read, size: " + fileContent.length + " bytes.");
                }

                // After "upload", hide the progress bar and display the image using Glide
                binding.uploadProgressBar.setVisibility(View.GONE);

                // Use Glide to display the image in the ImageView
                Glide.with(this)
                        .load(fileUri)
                        .into(binding.uploadedImageView);  // Assuming you have an ImageView named uploadedImageView in your layout

            } catch (IOException e) {
                // Log the error
                Log.e(TAG, "Error reading file", e);
                binding.uploadProgressBar.setVisibility(View.GONE);
                Snackbar.make(binding.getRoot(), "File upload failed", Snackbar.LENGTH_SHORT).show();
            }
        }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
//@Override
//     public boolean onSupportNavigateUp() {
//         NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//         return NavigationUI.navigateUp(navController, appBarConfiguration)
//                 || super.onSupportNavigateUp();
//     }
// }


//steve push comment

