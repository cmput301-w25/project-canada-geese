package com.example.canada_geese.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
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
                if (newText.isEmpty()) {
                    adapter.updateList(moodEventList);
                } else {
                    adapter.filter(newText);
                }
                return false;
            }



        });

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


    private List<MoodEventModel> getSampleMoodEvents() {
        List<MoodEventModel> list = new ArrayList<>();
        list.add(new MoodEventModel("Happiness", "2025-02-12 08:15", "😊", R.color.color_happiness, false));
        list.add(new MoodEventModel("Anger", "2025-02-11 03:42", "😠", R.color.color_anger, false));
        list.add(new MoodEventModel("Sadness", "2025-02-07 21:16", "😢", R.color.color_sadness, false));
        return list;
    }
}