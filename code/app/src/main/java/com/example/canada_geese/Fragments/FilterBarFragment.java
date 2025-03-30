package com.example.canada_geese.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.canada_geese.Adapters.MoodEventAdapter;
import com.example.canada_geese.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.HashSet;
import java.util.Set;


public class FilterBarFragment extends Fragment {
    private static final String TAG = "FilterBarFragment";
    private Chip chipLast7Days, chipClearAll, chipPrivate;
    private Set<String> selectedMoods = new HashSet<>();
    private MoodEventAdapter adapter;
    private boolean isLast7DaysSelected = false;
    private boolean isPrivateSelected = false;
    private String currentQuery = "";
    private boolean isForFriendsPage = false;



    public FilterBarFragment() {}

    public void setAdapter(MoodEventAdapter adapter) {
        this.adapter = adapter;
    }
    public void setIsForFriendsPage(boolean isForFriendsPage) {
        this.isForFriendsPage = isForFriendsPage;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_bar, container, false);

        // Initialize chips
        chipLast7Days = view.findViewById(R.id.chip_last_7_days);
        chipClearAll = view.findViewById(R.id.chip_clear_all);
        chipPrivate = view.findViewById(R.id.chip_mood_private);
        if (isForFriendsPage) {
            chipPrivate.setVisibility(View.GONE);
        }

        setupChipActions(view);

        return view;
    }



    private void setupChipActions(View view) {
        chipLast7Days.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d(TAG, "Last 7 days chip changed to: " + isChecked);
            isLast7DaysSelected = isChecked;
            applyFilters();
        });

        chipClearAll.setOnClickListener(v -> {
            Log.d(TAG, "Clear all chips clicked");
            isLast7DaysSelected = false;
            chipLast7Days.setChecked(false);
            selectedMoods.clear();
            resetMoodChips(view);
            applyFilters();
        });
        chipPrivate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d(TAG, "Private chip changed to: " + isChecked);
            isPrivateSelected = isChecked;
            applyFilters();
        });


        setupMoodChip(view.findViewById(R.id.chip_mood_anger), "Anger");
        setupMoodChip(view.findViewById(R.id.chip_mood_confusion), "Confusion");
        setupMoodChip(view.findViewById(R.id.chip_mood_disgust), "Disgust");
        setupMoodChip(view.findViewById(R.id.chip_mood_fear), "Fear");
        setupMoodChip(view.findViewById(R.id.chip_mood_happiness), "Happiness");
        setupMoodChip(view.findViewById(R.id.chip_mood_sadness), "Sadness");
        setupMoodChip(view.findViewById(R.id.chip_mood_shame), "Shame");
        setupMoodChip(view.findViewById(R.id.chip_mood_surprise), "Surprise");
        setupMoodChip(view.findViewById(R.id.chip_mood_calm), "Calm");
    }

    private void setupMoodChip(Chip chip, String mood) {
        chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d(TAG, mood + " chip changed to: " + isChecked);
            if (isChecked) {
                selectedMoods.add(mood);
            } else {
                selectedMoods.remove(mood);
            }
            applyFilters();
        });
    }

    private void resetMoodChips(View view) {
        ChipGroup chipGroup = view.findViewById(R.id.filter_chip_group);
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            View child = chipGroup.getChildAt(i);
            if (child instanceof Chip && child.getId() != R.id.chip_clear_all) {
                ((Chip) child).setChecked(false);
            }
        }
    }
    private void applyFilters() {
        if (adapter != null) {
            Log.d(TAG, "Applying filters - Last 7 days: " + isLast7DaysSelected +
                    ", Selected moods: " + selectedMoods);
            adapter.filter(currentQuery, isLast7DaysSelected, selectedMoods, isPrivateSelected);
        } else {
            Log.e(TAG, "Cannot apply filters: adapter is null");
        }
    }


    }



