package com.example.canada_geese.Pages;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.canada_geese.Fragments.AddMoodEventDialogFragment;
import com.example.canada_geese.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.page2);

        // Set up the initial fragment
        if (savedInstanceState == null) {
            // Using fragment_add_mood_page instead of MoodEventFragment
            loadFragment(fragment_add_mood_page.newInstance());
        }

        // Handle bottom navigation item selection
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Fragment fragment = null;
                int itemId = item.getItemId();  // Get the item ID

                // Use if-else instead of switch for itemId comparisons
                if (itemId == R.id.page1) {
                    fragment = fragment_friends_moods_page.newInstance();
                } else if (itemId == R.id.page2) {
                    fragment = fragment_user_moods_page.newInstance();
                } else if (itemId == R.id.page3) {
                    // Using fragment_add_mood_page
                    //fragment = fragment_add_mood_page.newInstance();
                    fragment_user_moods_page moodsFragment = fragment_user_moods_page.newInstance();

                    AddMoodEventDialogFragment dialog = new AddMoodEventDialogFragment();
                    dialog.setOnMoodAddedListener(moodsFragment::addNewMood);
                    dialog.show(getSupportFragmentManager(), "AddMoodEventDialog");

                    fragment = moodsFragment;
                } else if (itemId == R.id.page4) {
                    fragment = fragment_map_view_page.newInstance();
                } else if (itemId == R.id.page5) {
                    fragment = fragment_user_profile_page.newInstance();
                }

                return loadFragment(fragment);
            }
        });
    }

    // Method to load a fragment
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
            return true;
        }
        return false;
    }
}