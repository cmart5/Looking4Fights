package com.example.looking4fight.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import com.example.looking4fight.R;
import com.example.looking4fight.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    // Initialize fragments
    private final Fragment exploreFragment = new ExploreFragment();
    private final Fragment profileFragment = new ProfileFragment();
    private final Fragment settingsFragment = new SettingsFragment();
    private final Fragment createPostFragment = new CreatePostFragment();
    private Fragment activeFragment = exploreFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FragmentManager fragmentManager = getSupportFragmentManager();

        // Load fragments only if first launch
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.frameLayout, exploreFragment, "explore").commit();
            fragmentManager.beginTransaction()
                    .add(R.id.frameLayout, profileFragment, "profile").hide(profileFragment).commit();
            fragmentManager.beginTransaction()
                    .add(R.id.frameLayout, settingsFragment, "settings").hide(settingsFragment).commit();
            fragmentManager.beginTransaction()
                    .add(R.id.frameLayout, createPostFragment, "createPost").hide(createPostFragment).commit();
        } else {
            // Restore active fragment after configuration change
            activeFragment = fragmentManager.findFragmentByTag(savedInstanceState.getString("activeFragment"));
        }

        // Set up bottom navigation
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.home) {
                selectedFragment = exploreFragment;
            } else if (item.getItemId() == R.id.profile) {
                selectedFragment = profileFragment;
            } else if (item.getItemId() == R.id.settings) {
                selectedFragment = settingsFragment;
            } else if (item.getItemId() == R.id.createPost) {
                selectedFragment = createPostFragment;
            }

            if (selectedFragment != null) {
                switchFragment(selectedFragment);
            }

            return true;
        });
    }

    private void switchFragment(Fragment fragment) {
        if (fragment != activeFragment) {
            getSupportFragmentManager().beginTransaction()
                    .hide(activeFragment)
                    .show(fragment)
                    .commit();
            activeFragment = fragment;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("activeFragment", activeFragment.getTag());
    }
}
