package com.example.looking4fight;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.looking4fight.databinding.ActivityMainBinding;
import com.example.looking4fight.R;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new SecondFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item ->
        {

            if (item.getItemId() == R.id.home)
            {
                replaceFragment(new SecondFragment());
            } else if (item.getItemId() == R.id.profile)
            {
                replaceFragment(new FirstFragment());
            } else if (item.getItemId() == R.id.settings)
            {
                replaceFragment(new ThirdFragment());
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}