package com.example.canada_geese.Pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canada_geese.Adapters.MoodEventAdapter;
import com.example.canada_geese.Fragments.AddMoodEventDialogFragment;
import com.example.canada_geese.Models.MoodEventModel;
import com.example.canada_geese.R;

import java.util.ArrayList;
import java.util.List;

public class fragment_add_mood_page extends Fragment {
    private RecyclerView recyclerView;
    private MoodEventAdapter adapter;
    private SearchView searchView;
    private List<MoodEventModel> moodEventList;

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
        View view = inflater.inflate(R.layout.fragment_mood_event, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.searchView);
        View addMoodEventButton = view.findViewById(R.id.add_mood_event_button);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize data and adapter
        moodEventList = new ArrayList<>(getSampleMoodEvents());
        adapter = new MoodEventAdapter(moodEventList, getContext());
        recyclerView.setAdapter(adapter);

        // Setup search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 直接使用filter方法，让适配器内部决定如何处理空查询
                adapter.filter(newText);
                return false;
            }
        });

        // Add close listener to ensure list resets when search is closed
        searchView.setOnCloseListener(() -> {
            adapter.filter("");
            return false;
        });

        // Setup add mood button
        addMoodEventButton.setOnClickListener(v -> {
            AddMoodEventDialogFragment dialog = new AddMoodEventDialogFragment();
            dialog.setOnMoodAddedListener(moodEvent -> {
                adapter.addItem(moodEvent);
                recyclerView.post(() -> recyclerView.scrollToPosition(0));
                Toast.makeText(getContext(), "Mood added successfully", Toast.LENGTH_SHORT).show();
            });
            dialog.show(getParentFragmentManager(), "AddMoodEventDialog");
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 确保在Fragment恢复时重置过滤
        if (adapter != null) {
            adapter.filter("");
        }
    }

    // Sample data method
    private List<MoodEventModel> getSampleMoodEvents() {
        List<MoodEventModel> list = new ArrayList<>();
        list.add(new MoodEventModel("Happiness", "2025-02-12 08:15", "😊", R.color.color_happiness, false, true, 51.0447, -114.0719));
        list.add(new MoodEventModel("Anger", "2025-02-11 03:42", "😠", R.color.color_anger, false, true, 51.0447, -114.0719));
        list.add(new MoodEventModel("Sadness", "2025-02-07 21:16", "😢", R.color.color_sadness, false, true, 51.0447, -114.0719));
        return list;
    }
}