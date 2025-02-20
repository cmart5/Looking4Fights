package com.example.looking4fight.fragments;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import com.example.looking4fight.R;
import com.example.looking4fight.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private FloatingActionButton fabCreatePost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            replaceFragment(new ExploreFragment()); //Prevent reloading
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item ->
        {

            if (item.getItemId() == R.id.home)
            {
                replaceFragment(new ExploreFragment());
            }
            else if (item.getItemId() == R.id.profile)
            {
                replaceFragment(new ProfileFragment());
            }
            else if (item.getItemId() == R.id.settings)
            {
                replaceFragment(new SettingsFragment());
            }
            else if (item.getItemId() == R.id.createPost)
            {
                replaceFragment(new CreatePostFragment());
            }
            else if (item.getItemId() == R.id.events)
            {
                replaceFragment(new EventsFragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frameLayout);

        if (currentFragment != null && currentFragment.getClass().equals(fragment.getClass()))
        {
            return; // Avoid unnecessary fragment reload
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    private void openCreatePostDialog()
    {
        CreatePostFragment createPostFragment = CreatePostFragment.newInstance();
        createPostFragment.show(getSupportFragmentManager(), "CreatePostFragment");
    }
}