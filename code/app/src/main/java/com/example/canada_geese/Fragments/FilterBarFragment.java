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

/**
 * Fragment representing the filter bar for mood event filtering.
 * Allows filtering by emotion, time range, and privacy setting.
 */
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

    /**
     * Sets the adapter that will be filtered.
     * @param adapter The MoodEventAdapter to apply filters to.
     */
    public void setAdapter(MoodEventAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * Sets whether this filter bar is being used on the friends page.
     * @param isForFriendsPage True if used on friends page.
     */
    public void setIsForFriendsPage(boolean isForFriendsPage) {
        this.isForFriendsPage = isForFriendsPage;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_bar, container, false);

        chipLast7Days = view.findViewById(R.id.chip_last_7_days);
        chipClearAll = view.findViewById(R.id.chip_clear_all);
        chipPrivate = view.findViewById(R.id.chip_mood_private);

        if (isForFriendsPage) {
            chipPrivate.setVisibility(View.GONE);
        }

        setupChipActions(view);

        return view;
    }

    /**
     * Sets up actions for all chips in the filter bar.
     * @param view The root view containing chips.
     */
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

        setupMoodChip(view.findViewById(R.id.chip_mood_anger), "Angry");
        setupMoodChip(view.findViewById(R.id.chip_mood_confusion), "Confused");
        setupMoodChip(view.findViewById(R.id.chip_mood_disgust), "Disgusted");
        setupMoodChip(view.findViewById(R.id.chip_mood_fear), "Scared");
        setupMoodChip(view.findViewById(R.id.chip_mood_happiness), "Happy");
        setupMoodChip(view.findViewById(R.id.chip_mood_sadness), "Sad");
        setupMoodChip(view.findViewById(R.id.chip_mood_shame), "Ashamed");
        setupMoodChip(view.findViewById(R.id.chip_mood_surprise), "Surprised");
        setupMoodChip(view.findViewById(R.id.chip_mood_calm), "Calm");
    }

    /**
     * Sets up a mood chip and its filter logic.
     * @param chip The Chip view.
     * @param mood The mood label.
     */
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

    /**
     * Unchecks all mood chips except clear chip.
     * @param view The root view containing the chip group.
     */
    private void resetMoodChips(View view) {
        ChipGroup chipGroup = view.findViewById(R.id.filter_chip_group);
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            View child = chipGroup.getChildAt(i);
            if (child instanceof Chip && child.getId() != R.id.chip_clear_all) {
                ((Chip) child).setChecked(false);
            }
        }
    }

    /**
     * Applies current filters to the adapter.
     */
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
