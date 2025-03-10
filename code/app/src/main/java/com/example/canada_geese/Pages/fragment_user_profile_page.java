package com.example.canada_geese.Pages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import androidx.fragment.app.Fragment;

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

    private ImageView profileImage;
    private ImageButton signOutButton;
    private ImageButton RequestsButton;
    private ListView followersListView;
    private LinearLayout followersSection;
    private LinearLayout followingSection;

    private SearchView searchView;
    private ArrayAdapter<String> userAdapter;
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

        // Find Views
        usernameText = rootView.findViewById(R.id.username_text);
        profileImage = rootView.findViewById(R.id.profile_image);
        signOutButton = rootView.findViewById(R.id.btn_logout);
        RequestsButton = rootView.findViewById(R.id.btn_follow_requests);

        followersCountText = rootView.findViewById(R.id.followers_count);
        followingCountText = rootView.findViewById(R.id.following_count);

        // Initialize the ListView and Adapter
        followersListView = rootView.findViewById(R.id.followers_list);

        userList = new ArrayList<>();
        userAdapter= new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, userList);
        followersListView.setAdapter(userAdapter);

        // Initialize the LinearLayouts
        followersSection = rootView.findViewById(R.id.followers_section);
        followingSection = rootView.findViewById(R.id.following_section);

        // Fetch the user details from Firebase
        loadUserProfile();
        // Show the list of followers
        showFollowersList();

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


        // sign out button
        signOutButton.setOnClickListener(v -> signOutUser());

        // follow requests button opens dialog with requests
//        RequestsButton.setOnClickListener(v -> {
//            // Open the requests dialog
//            RequestsDialogFragment requestsDialog = new RequestsDialogFragment();
//            requestsDialog.show(getChildFragmentManager(), "RequestsDialog");
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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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
