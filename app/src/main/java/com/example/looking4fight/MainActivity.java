package com.example.looking4fight;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.looking4fight.ui.login.ProfileFragment;
import com.example.looking4fight.ui.login.SecondFragment;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Log.d("MainActivity", "Button Clicked: " + itemId); // Log for Debugging

            if (itemId == R.id.nav_profile) {
                Log.d("MainActivity", "Loading ProfileFragment");
                loadFragment(new ProfileFragment());
                return true;
            } else if (itemId == R.id.nav_upload) {
                Log.d("MainActivity", "Loading SecondFragment");
                loadFragment(new SecondFragment());
                return true;
            }
            return false;
        });

        // Load ProfileFragment by default
        if (savedInstanceState == null) {
            Log.d("MainActivity", "Loading Default ProfileFragment");
            loadFragment(new ProfileFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commitAllowingStateLoss();  // Prevents crashes when state is lost
    }

}
