package com.example.canada_geese.Pages;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canada_geese.Adapters.MoodEventAdapter;
import com.example.canada_geese.Managers.DatabaseManager;
import com.example.canada_geese.Models.MoodEventModel;
import com.example.canada_geese.R;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class fragment_user_moods_page extends Fragment {
    private RecyclerView recyclerView;
    private MoodEventAdapter adapter;
    private SearchView searchView;
    private List<MoodEventModel> moodEventList;
    private MoodEventModel newMood;

    public fragment_user_moods_page() {
        // Required empty public constructor
    }

    public static fragment_user_moods_page newInstance() {
        fragment_user_moods_page fragment = new fragment_user_moods_page();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    // 设置新的心情，在Fragment创建之前调用
    public void setNewMood(MoodEventModel mood) {
        this.newMood = mood;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_moods_page, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.searchView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize empty list and adapter
        moodEventList = new ArrayList<>();
        adapter = new MoodEventAdapter(moodEventList, getContext());
        recyclerView.setAdapter(adapter);

        // Fetch mood events for the logged-in user
        DatabaseManager.getInstance().fetchMoodEvents(task -> {
            if (task.isSuccessful()) {
                List<MoodEventModel> newList = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    MoodEventModel moodEvent = document.toObject(MoodEventModel.class);
                    newList.add(moodEvent);
                }
                adapter.updateList(newList); // Refresh adapter with new data
            } else {
                Log.e("FetchError", "Error getting documents: ", task.getException());
            }
        });

        // Check if there is a new mood to add
        if (newMood != null) {
            adapter.addItem(newMood);
            newMood = null; // Reset to avoid duplicate addition
        }

        // Setup search functionality
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

        // Reset list when search is closed
        searchView.setOnCloseListener(() -> {
            adapter.filter("");
            return false;
        });

        return view;
    }


    public void addNewMood(MoodEventModel moodEvent) {
        if (adapter != null) {
            adapter.addItem(moodEvent);
            recyclerView.smoothScrollToPosition(0); // 滚动到顶部以显示新添加的条目
        } else {
            // 如果adapter尚未初始化，保存心情以便稍后添加
            newMood = moodEvent;
        }
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
        list.add(new MoodEventModel("Happiness","test", "2025-02-12 08:15", "😊", R.color.color_happiness, false, true, 51.0447, -114.0719));
        list.add(new MoodEventModel("Anger","test", "2025-02-11 03:42", "😠", R.color.color_anger, false, true, 40.7128, -74.0060));
        list.add(new MoodEventModel("Fear","test", "2025-02-07 21:16", "😢", R.color.color_sadness, false, true, 48.8566f, 2.3522));
        return list;
    }
}