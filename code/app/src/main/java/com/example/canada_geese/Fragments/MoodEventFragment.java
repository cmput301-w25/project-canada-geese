package com.example.canada_geese.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
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
import com.example.canada_geese.Managers.DatabaseManager;
import com.example.canada_geese.Models.MoodEventModel;
import com.example.canada_geese.Pages.fragment_map_view_page;
import com.example.canada_geese.R;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A fragment that displays a list of mood events and allows searching and filtering of mood evnents
 */
public class MoodEventFragment extends Fragment {
    private RecyclerView recyclerView;
    private MoodEventAdapter adapter;
    private SearchView searchView;
    private List<MoodEventModel> moodEventList;

    /**
     * Default constructor for MoodEventFragment.
     */
    public MoodEventFragment() {}

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mood_event, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.searchView);
        View addMoodEventButton = view.findViewById(R.id.add_mood_event_button);
        View filterButton = view.findViewById(R.id.filter_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        moodEventList = new ArrayList<>();
        adapter = new MoodEventAdapter(moodEventList, getContext());
        recyclerView.setAdapter(adapter);

        fetchMoodEventsFromFirestore();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    adapter.updateList(moodEventList);
                } else {
                    adapter.filter(newText);
                }
                return false;
            }
        });

        filterButton.setOnClickListener(v -> showFilterDialog());

        addMoodEventButton.setOnClickListener(v -> {
            AddMoodEventDialogFragment dialog = new AddMoodEventDialogFragment();
            dialog.setOnMoodAddedListener(moodEvent -> fetchMoodEventsFromFirestore());
            dialog.show(getParentFragmentManager(), "AddMoodEventDialog");
        });

        return view;
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_filter, null);
        Spinner emotionSpinner = view.findViewById(R.id.filter_mood_spinner);
        CheckBox last7DaysCheckBox = view.findViewById(R.id.filter_last_7_days);

        builder.setView(view)
                .setPositiveButton("Apply", (dialog, which) -> {
                    String selectedEmotion = emotionSpinner.getSelectedItem().toString();
                    boolean filterLast7Days = last7DaysCheckBox.isChecked();
                    adapter.setFilters(selectedEmotion, filterLast7Days); // Update adapter's filter state
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Fetches mood events from Firestore and updates the RecyclerView.
     * Logs the success or failure of the operation.
     */
    private void fetchMoodEventsFromFirestore() {
        Log.d("MoodEventFragment", "Attempting to fetch mood events from Firestore...");

        DatabaseManager.getInstance().fetchMoodEvents(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Log.d("MoodEventFragment", "Fetched " + task.getResult().size() + " mood events.");
                moodEventList.clear();

                for (DocumentSnapshot doc : task.getResult()) {
                    MoodEventModel mood = doc.toObject(MoodEventModel.class);
                    if (mood != null) {
                        Log.d("MoodEventFragment", "Loaded mood: " + mood.getEmotion() + ", " + mood.getTimestamp());
                        moodEventList.add(mood);
                    } else {
                        Log.e("MoodEventFragment", "Failed to convert document to MoodEventModel.");
                    }
                }

                adapter.updateList(moodEventList);
                Log.d("MoodEventFragment", "RecyclerView updated with latest mood events.");
            } else {
                Log.e("MoodEventFragment", "Failed to fetch mood events: ", task.getException());
            }
        });
    }

    /**
     * Provides sample mood events for testing purposes.
     *
     * @return A list of sample MoodEventModel objects.
     */
    private List<MoodEventModel> getSampleMoodEvents() {
        List<MoodEventModel> list = new ArrayList<>();
        list.add(new MoodEventModel("Happiness", "test", new Date(), "😊", R.color.color_happiness, false, true, 51.0447, -114.0719));
        list.add(new MoodEventModel("Anger", "test", new Date(), "😠", R.color.color_anger, false, false, 0.0, 0.0));
        list.add(new MoodEventModel("Fear", "test", new Date(), "😢", R.color.color_sadness, false, false, 0.0, 0.0));
        return list;
    }
}
