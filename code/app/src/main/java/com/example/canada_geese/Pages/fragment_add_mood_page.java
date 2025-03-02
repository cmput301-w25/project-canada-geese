package com.example.canada_geese.Pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.canada_geese.R;

public class fragment_add_mood_page extends Fragment {



    public fragment_add_mood_page() {
        // Required empty public constructor
    }

    public static fragment_add_mood_page newInstance() {
        fragment_add_mood_page fragment = new fragment_add_mood_page();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_mood_page, container, false);
        TextView textView = rootView.findViewById(R.id.textView);

        return rootView;
    }
}