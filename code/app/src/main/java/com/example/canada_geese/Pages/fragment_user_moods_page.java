package com.example.canada_geese.Pages;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canada_geese.Adapters.MoodEventAdapter;
import com.example.canada_geese.Models.MoodEventModel;
import com.example.canada_geese.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class fragment_user_moods_page extends Fragment {
    private RecyclerView recyclerView;
    private MoodEventAdapter adapter;
    private List<MoodEventModel> moodEventList;
    private FirebaseFirestore db;
    private SearchView searchView;
    private ImageView filterIcon;
    private String selectedEmotion = "All";
    private boolean filterLast7Days = false;


    public fragment_user_moods_page() {
        // Required empty public constructor
    }

    public static fragment_user_moods_page newInstance() {
        return new fragment_user_moods_page();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_moods_page, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        searchView = rootView.findViewById(R.id.searchView);
        filterIcon = rootView.findViewById(R.id.filrerIcon);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        moodEventList = new ArrayList<>();
        adapter = new MoodEventAdapter(moodEventList, getContext());
        recyclerView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();

        fetchMoodEvents();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query, selectedEmotion, filterLast7Days);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText, selectedEmotion, filterLast7Days);
                return true;
            }
        });

        filterIcon.setOnClickListener(v -> showFilterDialog());

        return rootView;
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_filter, null);
        Spinner emotionSpinner = view.findViewById(R.id.emotionSpinner);
        CheckBox last7DaysCheckBox = view.findViewById(R.id.last7DaysCheckBox);

        // Set initial values
        last7DaysCheckBox.setChecked(filterLast7Days);

        builder.setView(view)
                .setPositiveButton("Apply", (dialog, which) -> {
                    selectedEmotion = emotionSpinner.getSelectedItem().toString();
                    filterLast7Days = last7DaysCheckBox.isChecked();
                    adapter.filter(searchView.getQuery().toString(), selectedEmotion, filterLast7Days);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void fetchMoodEvents() {
        CollectionReference moodEventsRef = db.collection("moodEvents");
        moodEventsRef.orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    moodEventList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        MoodEventModel mood = document.toObject(MoodEventModel.class);
                        moodEventList.add(mood);
                    }
                    adapter.updateList(moodEventList);
                });
    }

}