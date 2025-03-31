package com.example.canada_geese.Pages;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.canada_geese.Fragments.AddMoodEventDialogFragment;
import com.example.canada_geese.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// Offline
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;

import com.example.canada_geese.Workers.MoodSyncWorker;
/**
 * The main activity of the application that manages the bottom navigation and loads different fragments.
 * Handles user navigation between different pages such as User Moods, Friends' Moods, Map View, and Profile.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     * Sets up the bottom navigation and initializes the default fragment.
     *
     * @param savedInstanceState Previous state of the activity, if available.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.page1);

        // Set up the initial fragment
        if (savedInstanceState == null) {
            // Load the User Moods page as the default fragment
            loadFragment(fragment_user_moods_page.newInstance());
        }

        // Handle bottom navigation item selection
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            /**
             * Handles navigation item selection to load the appropriate fragment.
             *
             * @param item The selected menu item.
             * @return True if the fragment was successfully loaded, false otherwise.
             */
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Fragment fragment = null;
                int itemId = item.getItemId();  // Get the item ID

                // Load the appropriate fragment based on selected item ID
                if (itemId == R.id.page1) {
                    fragment = fragment_user_moods_page.newInstance();  // User Moods page
                } else if (itemId == R.id.page2) {
                    fragment = fragment_friends_moods_page.newInstance();  // Friends' Moods page
                } else if (itemId == R.id.page3) {
                    AddMoodEventDialogFragment dialog = new AddMoodEventDialogFragment();
                    dialog.setOnDismissListener(() -> {
                        bottomNavigationView.setSelectedItemId(R.id.page1); // Select User Moods Page
                    });
                    dialog.show(getSupportFragmentManager(), "AddMoodEventDialog");
                    return false;
                } else if (itemId == R.id.page4) {
                    fragment = fragment_map_view_page.newInstance();  // Map View page
                } else if (itemId == R.id.page5) {
                    fragment = fragment_user_profile_page.newInstance();  // User Profile page
                }

                return loadFragment(fragment);
            }
        });
        MoodSyncWorker.scheduleSync(this);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest request = new NetworkRequest.Builder().build();
        cm.registerNetworkCallback(request, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                MoodSyncWorker.scheduleSync(getApplicationContext());
            }
        });
    }

    /**
     * Replaces the current fragment with the specified fragment.
     *
     * @param fragment The fragment to load.
     * @return True if the fragment was successfully loaded, false otherwise.
     */
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