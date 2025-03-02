package com.example.canada_geese.Pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.canada_geese.R;

public class fragment_user_moods_page extends Fragment {


    public fragment_user_moods_page() {
        // Required empty public constructor
    }

    public static fragment_user_moods_page newInstance() {
        fragment_user_moods_page fragment = new fragment_user_moods_page();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_moods_page, container, false);

        return rootView;
    }
}