package com.example.canada_geese.Pages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.canada_geese.R;
import com.google.firebase.auth.FirebaseAuth;

public class fragment_user_profile_page extends Fragment {

    public fragment_user_profile_page() {
        // Required empty public constructor
    }

    public static fragment_user_profile_page newInstance() {
        fragment_user_profile_page fragment = new fragment_user_profile_page();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile_page, container, false);

        Button signOutButton = rootView.findViewById(R.id.btnSignOut);

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. Sign out from Firebase
                FirebaseAuth.getInstance().signOut();

                // 2. Clear stored data from SharedPreferences
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear(); // This will remove all saved data (email, password, username, remember me flag)
                editor.apply();

                // 3. Navigate to LoginActivity
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear previous activities in stack
                startActivity(intent);

                // Optionally, finish the current activity so the user can't go back to the profile page
                requireActivity().finish();
            }
        });

        return rootView;
    }
}