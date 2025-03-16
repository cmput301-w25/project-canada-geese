package com.example.canada_geese.Pages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.canada_geese.Adapters.UserSearchAdapter;
import com.example.canada_geese.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to display and manage the user's profile page.
 * Includes user information such as username and profile picture,
 * and provides a sign-out functionality.
 */
public class fragment_user_profile_page extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView usernameText;
    private TextView followersCountText;
    private TextView followingCountText;
    private ImageButton menuImageButton;
    private ImageView profileImage;
    private SearchView searchView;
    private ListView followersListView;
    private LinearLayout followersSection;
    private LinearLayout followingSection;
    private ArrayAdapter<String> userAdapter;
    private RecyclerView searchResultsList;
    LinearLayout searchResultsContainer;
    LinearLayout profileContentContainer;
    private List<String> userList; // Declare userList here

    /**
     * Required empty public constructor.
     */
    public fragment_user_profile_page() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of this fragment.
     *
     * @return A new instance of fragment_user_profile_page.
     */
    public static fragment_user_profile_page newInstance() {
        return new fragment_user_profile_page();
    }

    /**
     * Inflates the layout for this fragment and initializes Firebase authentication and UI components.
     *
     * @param inflater           The LayoutInflater object to inflate views.
     * @param container          The parent view to attach the fragment's UI.
     * @param savedInstanceState A previous saved state of the fragment, if available.
     * @return The root view for this fragment's layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile_page, container, false);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        // Find Views for the UI components of the profile content container
        usernameText = rootView.findViewById(R.id.username_text);
        profileImage = rootView.findViewById(R.id.profile_image);
        followersSection = rootView.findViewById(R.id.followers_section);
        followingSection = rootView.findViewById(R.id.following_section);
        followersCountText = rootView.findViewById(R.id.followers_count);
        followingCountText = rootView.findViewById(R.id.following_count);
        followersListView = rootView.findViewById(R.id.followers_list);

        // Find Views for the search bar and search results container
        searchView = rootView.findViewById(R.id.searchView);
        menuImageButton = rootView.findViewById(R.id.menu_button);

        // Find views for the search list view
        searchResultsList = rootView.findViewById(R.id.search_results_list);

        // Initialize the search results container AND profile content container
        searchResultsContainer = rootView.findViewById(R.id.search_results_container);
        profileContentContainer = rootView.findViewById(R.id.profile_content_container);

        // Initialize the ListView and Adapter basic implementation right now with just users name
        userList = new ArrayList<>();
        userAdapter= new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, userList);
        followersListView.setAdapter(userAdapter);

//        // Initialize the search results listview
//        List<String> searchResultsContainer = new ArrayList<>();
//        ArrayAdapter<String> searchResultsAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, searchResultsContainer);
//        searchResultsContainer.setAdapter(searchResultsAdapter);


        // Fetch the user details from Firebase
        loadUserProfile();
        // Show the list of followers
        showFollowersList();
        // Set up the menu button
        menuImageButton.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(requireContext(), menuImageButton);
            popup.getMenuInflater().inflate(R.menu.profile_menu, popup.getMenu());

            // Set click listener for menu items
            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_signout) {
                    signOutUser();
                    return true;
                } else if (id == R.id.action_requests) {
                    // Navigate to the follow requests page


                    Toast.makeText(requireContext(), "Follow Requests", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.action_settings) {
                    // Navigate to the settings page
                    Toast.makeText(requireContext(), "Settings", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            });

            popup.show();
        });

        // Click listener for the followers section
        followersSection.setOnClickListener(v -> {
            // Show the list of followers
            showFollowersList();
        });
        // Click listener for the following section
        followingSection.setOnClickListener(v -> {
            // Show the list of following
            showFollowingList();
        });

//        // SearchView listener to show search results
//        searchView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (searchResultsContainer.getVisibility() == View.INVISIBLE) {
//                    searchResultsContainer.setVisibility(View.VISIBLE);
//                    profileContentContainer.setVisibility(View.INVISIBLE);
//                }
//                else if (searchResultsContainer.getVisibility() == View.VISIBLE) {
//                    searchResultsContainer.setVisibility(View.INVISIBLE);
//                    profileContentContainer.setVisibility(View.VISIBLE);
//                }
//            }
//        });




        return rootView;
    }



    /**
     * Signs out the current user, clears stored login preferences, and redirects to the login screen.
     */
    private void signOutUser() {
        // Sign out activity on firebase
        mAuth.signOut();

        // Sign-out clears shared preference
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Navigate back to LoginActivity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        // Clear the back stack and start the new activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        // Finish the current activity so the user can't go back to the profile page
        requireActivity().finish(); // Close the profile activity
    }

    /**
     * Loads the user's profile information including username and profile picture.
     * Displays default information if user data is not available.
     */
    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        // Use the signed in user to pull up their profile information from the database
        if (user != null) {
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            usernameText.setText(username);
                        }
                    });
            // Ignoring firestore storage setup for current implementation for user profile
            // Load profile picture if available
            Uri profileUri = user.getPhotoUrl();
            if (profileUri != null) {
                profileImage.setImageURI(profileUri);
            } else {
                profileImage.setImageResource(R.drawable.profile); // Placeholder image
            }
//            profileImage.setImageResource(R.drawable.profile);

            // Load Followers to the user profile
            db.collection("users").document(user.getUid()).collection("Followers").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        int followersCount = queryDocumentSnapshots.size();
                        followersCountText.setText(String.valueOf(followersCount));
                    });
            // Load Following to the user profile
            db.collection("users").document(user.getUid()).collection("Following").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        int followingCount = queryDocumentSnapshots.size();
                        followingCountText.setText(String.valueOf(followingCount));
                    });
        }
    }

    private void showFollowersList() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid()).collection("Followers").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        userList.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            String username = document.getString("username");
                            userList.add(username); // Add only the username to the list
                        }
                        userAdapter.notifyDataSetChanged(); // Refresh the ListView
                    });

        }
    }

    private void showFollowingList() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid()).collection("Following").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        userList.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            String username = document.getString("username");
                            userList.add(username); // Add only the username to the list
                        }
                        userAdapter.notifyDataSetChanged(); // Refresh the ListView
                    });
        }
    }

}
