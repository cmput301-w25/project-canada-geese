package com.example.canada_geese.Pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canada_geese.Adapters.MoodEventAdapter;
import com.example.canada_geese.Fragments.AddMoodEventDialogFragment;
import com.example.canada_geese.Models.MoodEventModel;
import com.example.canada_geese.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A fragment that manages and displays a list of mood events.
 * Provides functionality for searching and adding new mood events.
 */
public class fragment_add_mood_page extends Fragment {
    private RecyclerView recyclerView;
    private MoodEventAdapter adapter;
    private SearchView searchView;
    private List<MoodEventModel> moodEventList;

    /**
     * Required empty public constructor.
     */
    public fragment_add_mood_page() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of this fragment.
     *
     * @return A new instance of fragment_add_mood_page.
     */
    public static fragment_add_mood_page newInstance() {
        fragment_add_mood_page fragment = new fragment_add_mood_page();
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
        View view = inflater.inflate(R.layout.fragment_mood_event, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.searchView);
        View addMoodEventButton = view.findViewById(R.id.add_mood_event_button);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize data and adapter
        moodEventList = new ArrayList<>(getSampleMoodEvents());
        adapter = new MoodEventAdapter(moodEventList, getContext(),false);
        recyclerView.setAdapter(adapter);

        // Setup search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query, false, "");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText, false, "");
                return false;
            }
        });

        // Add close listener to ensure list resets when search is closed
        searchView.setOnCloseListener(() -> {
            adapter.filter("", false, "");
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

    /**
     * Ensures the filter is reset when the fragment resumes.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.filter("", false, "");
        }
    }
    private Date parseDate(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            return sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date(); // Return current date if parsing fails
        }
    }
    /**
     * Provides sample mood events for testing purposes.
     *
     * @return A list of sample MoodEventModel objects.
     */
    private List<MoodEventModel> getSampleMoodEvents() {
        List<MoodEventModel> list = new ArrayList<>();
        list.add(new MoodEventModel("Happiness", "test", parseDate("2025-02-12 08:15"), "😊", R.color.color_happiness, false, true, 51.0447, -114.0719));
        list.add(new MoodEventModel("Anger", "test", parseDate("2025-02-11 03:42"), "😠", R.color.color_anger, false, false, 0.0, 0.0));
        list.add(new MoodEventModel("Fear", "test", parseDate("2025-02-07 21:16"), "😢", R.color.color_sadness, false, false, 0.0, 0.0));
        return list;
    }

}
