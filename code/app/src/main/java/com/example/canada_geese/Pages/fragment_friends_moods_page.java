package com.example.canada_geese.Pages;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canada_geese.Adapters.MoodEventAdapter;
import com.example.canada_geese.Fragments.CommentsFragment;
import com.example.canada_geese.Fragments.FilterBarFragment;
import com.example.canada_geese.Managers.DatabaseManager;
import com.example.canada_geese.Models.MoodEventModel;
import com.example.canada_geese.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * A fragment that displays the moods of friends.
 * This is a placeholder fragment that currently only displays a TextView.
 */
public class fragment_friends_moods_page extends Fragment {
    private boolean isLast7DaysSelected = false;
    private String selectedMood = "";
    private String searchQuery = "";
    private LinearLayout filterButton;
    private MoodEventAdapter adapter;
    private SearchView searchView;
    private boolean isPrivateSelected = false;

    /**
     * Required empty public constructor.
     */
    public fragment_friends_moods_page() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of this fragment.
     *
     * @return A new instance of fragment_friends_moods_page.
     */
    public static fragment_friends_moods_page newInstance() {
        fragment_friends_moods_page fragment = new fragment_friends_moods_page();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends_moods_page, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        filterButton = rootView.findViewById(R.id.filter_button);
        searchView = rootView.findViewById(R.id.searchView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<MoodEventModel> moodEventList = new ArrayList<>();
        adapter = new MoodEventAdapter(moodEventList, getContext(), true);
        recyclerView.setAdapter(adapter);
        filterButton.setOnClickListener(v -> toggleFilterBar());

        DatabaseManager.getInstance().fetchFollowedUsersMoodEvents(task -> {
            if (task.isSuccessful()) {
                List<MoodEventModel> friendsMoods = task.getResult();
                Collections.sort(friendsMoods, (a, b) -> {
                    if (a.getTimestamp() == null || b.getTimestamp() == null) return 0;
                    return b.getTimestamp().compareTo(a.getTimestamp());
                });

                // Fetch UID → username map and then update adapter
                DatabaseManager.getInstance().fetchAllUsernames(userMap -> {
                    adapter.setUidToUsernameMap(userMap);
                    adapter.updateList(friendsMoods);
                });

            } else {
                Log.e("FriendsMoodsPage", "Error fetching friends' moods", task.getException());
            }
        });

        // Setup search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query, false, new HashSet<>(), isPrivateSelected);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText, false, new HashSet<>(),isPrivateSelected);
                return false;
            }
        });

        // Reset list when search is closed
        searchView.setOnCloseListener(() -> {
            adapter.filter("", false, new HashSet<>(), isPrivateSelected);
            return false;
        });

        adapter.setOnCommentClickListener(new MoodEventAdapter.OnCommentClickListener() {
            @Override
            public void onCommentClick(MoodEventModel moodEvent) {
                String moodEventId = moodEvent.getDocumentId();
                String moodOwnerId = moodEvent.getUserId(); // 👈 This was missing before

                CommentsFragment commentsSheet = CommentsFragment.newInstance(moodEventId, moodOwnerId);
                commentsSheet.show(getChildFragmentManager(), commentsSheet.getTag());
            }
        });

        return rootView;
    }

    private void toggleFilterBar() {
        boolean isSelected = filterButton.isSelected();
        filterButton.setSelected(!isSelected);
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment existingFragment = fragmentManager.findFragmentById(R.id.filter_bar_fragment_container);

        View filterButtonsContainer = getView().findViewById(R.id.filter_bar_fragment_container);
        View spaceAboveRecycler = getView().findViewById(R.id.space); // 👈 Space view

        if (existingFragment == null) {
            FilterBarFragment filterBarFragment = new FilterBarFragment();
            filterBarFragment.setIsForFriendsPage(true);
            filterBarFragment.setAdapter(adapter);
            transaction.replace(R.id.filter_bar_fragment_container, filterBarFragment);
            transaction.commit();

            if (filterButtonsContainer != null) {
                filterButtonsContainer.setVisibility(View.VISIBLE);
            }
            if (spaceAboveRecycler != null) {
                spaceAboveRecycler.setVisibility(View.VISIBLE);
            }
        } else {
            transaction.remove(existingFragment);
            transaction.commit();

            if (filterButtonsContainer != null) {
                filterButtonsContainer.setVisibility(View.GONE);
            }
            if (spaceAboveRecycler != null) {
                spaceAboveRecycler.setVisibility(View.GONE);
            }
        }
    }
}