package com.example.canada_geese.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.canada_geese.Adapters.MoodEventAdapter;
import com.example.canada_geese.Models.MoodEventModel;
import com.example.canada_geese.R;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MoodEventFragment extends Fragment {
    private RecyclerView recyclerView;
    private MoodEventAdapter adapter;
    private SearchView searchView;
    private List<MoodEventModel> moodEventList;

    public MoodEventFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mood_event, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.searchView);
        View addMoodEventButton = view.findViewById(R.id.add_mood_event_button);
        View filterButton = view.findViewById(R.id.filter_button); // Add this button

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        moodEventList = new ArrayList<>(getSampleMoodEvents());

        adapter = new MoodEventAdapter(moodEventList, getContext());
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });

        filterButton.setOnClickListener(v -> showFilterDialog());

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

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_filter, null);
        Spinner emotionSpinner = view.findViewById(R.id.emotionSpinner);
        CheckBox last7DaysCheckBox = view.findViewById(R.id.last7DaysCheckBox);

        builder.setView(view)
                .setPositiveButton("Apply", (dialog, which) -> {
                    String selectedEmotion = emotionSpinner.getSelectedItem().toString();
                    boolean filterLast7Days = last7DaysCheckBox.isChecked();
                    adapter.setFilters(selectedEmotion, filterLast7Days); // Update adapter's filter state
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private List<MoodEventModel> getSampleMoodEvents() {
        List<MoodEventModel> list = new ArrayList<>();
        list.add(new MoodEventModel("Happiness", new Date(), "😊", R.color.color_happiness, false));
        list.add(new MoodEventModel("Anger", new Date(), "😠", R.color.color_anger, false));
        list.add(new MoodEventModel("Sadness", new Date(), "😢", R.color.color_sadness, false));
        return list;
    }
}