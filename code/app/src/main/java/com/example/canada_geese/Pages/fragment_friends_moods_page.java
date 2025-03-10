package com.example.canada_geese.Pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.canada_geese.R;

/**
 * A fragment that displays the moods of friends.
 * This is a placeholder fragment that currently only displays a TextView.
 */
public class fragment_friends_moods_page extends Fragment {

    /**
     * Required empty public constructor.
     */
    public fragment_friends_moods_page() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of this fragment.
     *
     * @return A new instance of fragment_friends_moods_page.
     */
    public static fragment_friends_moods_page newInstance() {
        fragment_friends_moods_page fragment = new fragment_friends_moods_page();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views.
     * @param container          The parent view that this fragment's UI should be attached to.
     * @param savedInstanceState A previous saved state of the fragment, if available.
     * @return The root view for this fragment's layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends_moods_page, container, false);

        // Initialize and set up views
        TextView textView = rootView.findViewById(R.id.textView);

        return rootView;
    }
}