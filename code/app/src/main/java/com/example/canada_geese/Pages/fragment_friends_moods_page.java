package com.example.canada_geese.Pages;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canada_geese.Adapters.MoodEventAdapter;
import com.example.canada_geese.Fragments.CommentsFragment;
import com.example.canada_geese.Managers.DatabaseManager;
import com.example.canada_geese.Models.MoodEventModel;
import com.example.canada_geese.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment that displays the moods of friends.
 * This is a placeholder fragment that currently only displays a TextView.
 */
public class fragment_friends_moods_page extends Fragment {

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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<MoodEventModel> moodEventList = new ArrayList<>();
        MoodEventAdapter adapter = new MoodEventAdapter(moodEventList, getContext(), true);
        recyclerView.setAdapter(adapter);

        DatabaseManager.getInstance().fetchFollowedUsersMoodEvents(task -> {
            if (task.isSuccessful()) {
                List<MoodEventModel> friendsMoods = task.getResult();

                // Fetch UID → username map and then update adapter
                DatabaseManager.getInstance().fetchAllUsernames(userMap -> {
                    adapter.setUidToUsernameMap(userMap);
                    adapter.updateList(friendsMoods);
                });

            } else {
                Log.e("FriendsMoodsPage", "Error fetching friends' moods", task.getException());
            }
        });

        adapter.setOnCommentClickListener(new MoodEventAdapter.OnCommentClickListener() {
            @Override
            public void onCommentClick(MoodEventModel moodEvent) {
                // Pass the mood event's document ID to the CommentsFragment
                CommentsFragment commentsSheet = CommentsFragment.newInstance(moodEvent.getDocumentId());
                commentsSheet.show(getChildFragmentManager(), commentsSheet.getTag());
            }
        });

        return rootView;
    }
}