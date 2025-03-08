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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.example.canada_geese.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class fragment_user_profile_page extends Fragment {

    private FirebaseAuth mAuth;
    private TextView usernameText;

    private ImageView profileImage;
    private ImageButton signOutButton;

    public fragment_user_profile_page() {
        // Required empty public constructor
    }

    public static fragment_user_profile_page newInstance() {
        return new fragment_user_profile_page();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile_page, container, false);

        //  Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Find Views
        usernameText = rootView.findViewById(R.id.username_text);
        profileImage = rootView.findViewById(R.id.profile_image);
        signOutButton = rootView.findViewById(R.id.btn_logout);

        // Fetch the user details from Firebase
        loadUserProfile();

        // sign out button
        signOutButton.setOnClickListener(v -> signOutUser());

        return rootView;
    }
    private void signOutUser() {
        // 1. Sign out from Firebase
        mAuth.signOut();

        // 2. Clear SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // 3. Navigate back to LoginActivity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        // Optionally, finish the current activity so the user can't go back to the profile page
        requireActivity().finish(); // Close the profile activity
    }

    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Set username from Firebase Authentication
            String username = user.getDisplayName();  // Default name
            String email = user.getEmail();           // Fallback if no display name

            if (username != null && !username.isEmpty()) {
                usernameText.setText(username);
            } else {
                Log.d("UserProfile", "Username is empty, using email");
                usernameText.setText(email);
            }

            // Load profile picture if available
            Uri profileUri = user.getPhotoUrl();
            if (profileUri != null) {
                profileImage.setImageURI(profileUri);
            } else {
                profileImage.setImageResource(R.drawable.default_profile); // Placeholder image
            }
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

}