package com.example.canada_geese.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.canada_geese.Adapters.MoodEventAdapter;
import com.example.canada_geese.R;

public class FilterBarFragment extends Fragment {

    private Button btnLast7Days, btnClearAll;
    private Spinner spinnerMood;
    private boolean isLast7DaysSelected = false;
    private MoodEventAdapter adapter;

    /**
     * Constructor to receive MoodEventAdapter.
     */
    public FilterBarFragment() {}

    /**
     * Use this method to set the adapter AFTER instantiation.
     */
    public void setAdapter(MoodEventAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * Inflates the layout for this fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_bar, container, false);

        // Initialize UI components
        btnLast7Days = view.findViewById(R.id.btn_last_7_days);
        spinnerMood = view.findViewById(R.id.spinner_mood);
        btnClearAll = view.findViewById(R.id.btn_clear_all);

        // Setup mood dropdown
        setupMoodSpinner();

        // Setup button actions
        setupButtonActions();

        return view;
    }

    /**
     * Configures the "Select Mood" spinner.
     */
    private void setupMoodSpinner() {
        String[] moods = {"Select Mood", "Happiness", "Anger", "Sadness", "Fear", "Confusion", "Disgust", "Shame", "Surprise", "Calm"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, moods);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMood.setAdapter(spinnerAdapter);
    }

    /**
     * Configures button click listeners.
     */
    private void setupButtonActions() {
        // "Last 7 Days" Button Toggle
        btnLast7Days.setOnClickListener(v -> {
            isLast7DaysSelected = !isLast7DaysSelected;
            btnLast7Days.setBackgroundColor(isLast7DaysSelected ? Color.GRAY : Color.WHITE);
            applyFilters();
        });

        // "Select Mood" Spinner Action
        spinnerMood.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnClearAll.setOnClickListener(v -> {
            isLast7DaysSelected = false;
            btnLast7Days.setBackgroundColor(Color.WHITE);
            spinnerMood.setSelection(0); // Reset mood spinner
            applyFilters(); // Re-apply filter with default values
        });
    }

    /**
     * Apply the selected filters to the adapter.
     */
    private void applyFilters() {
        if (adapter != null) {
            String selectedMood = spinnerMood.getSelectedItem().toString();
            boolean resetMoodFilter = selectedMood.equals("Select Mood"); // Reset if "Select Mood" is chosen
            adapter.filter("", isLast7DaysSelected, resetMoodFilter ? "" : selectedMood);
        }
    }

}