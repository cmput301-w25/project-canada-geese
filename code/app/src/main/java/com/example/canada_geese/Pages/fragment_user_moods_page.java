package com.example.canada_geese.Pages;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canada_geese.Adapters.MoodEventAdapter;
import com.example.canada_geese.Fragments.AddMoodEventDialogFragment;
import com.example.canada_geese.Fragments.CommentsFragment;
import com.example.canada_geese.Fragments.FilterBarFragment;
import com.example.canada_geese.Managers.DatabaseManager;
import com.example.canada_geese.Models.EmotionalState;
import com.example.canada_geese.Models.MoodEventModel;
import com.example.canada_geese.R;
import com.example.canada_geese.pageResources.DeleteConfirmationDialog;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * Fragment to display the list of mood events for the logged-in user.
 * Supports searching, adding, editing and deleting mood events.
 */
public class fragment_user_moods_page extends Fragment {
    private static final String TAG = "UserMoodsPage";
    private RecyclerView recyclerView;
    private MoodEventAdapter adapter;
    private SearchView searchView;
    private List<MoodEventModel> moodEventList;
    private MoodEventModel newMood;
    private boolean isLast7DaysSelected = false;
    private String selectedMood = "";
    private String searchQuery = "";
    private ImageButton filterButton;
    private boolean isPrivateSelected = false;


    /**
     * Required empty public constructor.
     */
    public fragment_user_moods_page() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of this fragment.
     *
     * @return A new instance of fragment_user_moods_page.
     */
    public static fragment_user_moods_page newInstance() {
        fragment_user_moods_page fragment = new fragment_user_moods_page();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Sets a new mood to be added to the list before the fragment is created.
     *
     * @param mood The MoodEventModel representing the new mood to add.
     */
    public void setNewMood(MoodEventModel mood) {
        this.newMood = mood;
    }

    /**
     * Inflates the layout for this fragment and initializes views.
     *
     * @param inflater           The LayoutInflater object to inflate views.
     * @param container          The parent view to attach the fragment's UI.
     * @param savedInstanceState A previous saved state of the fragment, if available.
     * @return The root view for this fragment's layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_moods_page, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.searchView);
        filterButton = view.findViewById(R.id.filter_button);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize empty list and adapter
        moodEventList = new ArrayList<>();
        adapter = new MoodEventAdapter(moodEventList, getContext(), false);
        recyclerView.setAdapter(adapter);

        filterButton.setOnClickListener(v -> toggleFilterBar());

        /*// Create filter bar fragment and set adapter
        FilterBarFragment filterBarFragment = new FilterBarFragment();
        filterBarFragment.setAdapter(adapter);
        // Attach existing FilterBarFragment (which already modifies the adapter)
        getChildFragmentManager().beginTransaction()
                .replace(R.id.filter_bar_fragment_container, filterBarFragment)
                .commit();*/

        // Set up click listeners for mood events
        setupMoodEventClickListeners();

        // Fetch mood events for the logged-in user from Firestore
        refreshMoodEventList();

        // Check if there is a new mood to add
        if (newMood != null) {
            adapter.addItem(newMood);
            newMood = null; // Reset to avoid duplicate addition
        }

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

        return view;
    }

    private void toggleFilterBar() {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment existingFragment = fragmentManager.findFragmentById(R.id.filter_bar_fragment_container);

        View filterButtonsContainer = getView().findViewById(R.id.filter_bar_fragment_container);

        if (existingFragment == null) {
            FilterBarFragment filterBarFragment = new FilterBarFragment();
            filterBarFragment.setAdapter(adapter);
            transaction.replace(R.id.filter_bar_fragment_container, filterBarFragment);
            transaction.commit();
            if (filterButtonsContainer != null) {
                filterButtonsContainer.setVisibility(View.VISIBLE);
            }
        } else {
            transaction.remove(existingFragment);
            transaction.commit();
            if (filterButtonsContainer != null) {
                filterButtonsContainer.setVisibility(View.INVISIBLE);
            }
        }
    }


    /**
     * Set up click and long-click listeners for the mood events.
     */
    private void setupMoodEventClickListeners() {
        // Click listener to handle saving edited mood events
        adapter.setOnMoodEventClickListener(updatedMood -> {
            // Save the updated mood to the database
            saveMoodEventChanges(updatedMood);
        });

        // Edit listener - used to handle UI transitions in the adapter
        adapter.setOnMoodEventEditListener((moodEvent, position) -> {
            AddMoodEventDialogFragment dialog = new AddMoodEventDialogFragment();
            dialog.setMoodToEdit(moodEvent, moodEvent.getDocumentId());

            dialog.setOnDismissListener(() -> {
                refreshMoodEventList(); // Refresh after editing
            });

            dialog.show(getChildFragmentManager(), "EditMoodDialog");
        });

        // Long click listener - Show delete confirmation
        adapter.setOnMoodEventLongClickListener(moodEvent -> {
            // Show delete confirmation dialog
            showDeleteConfirmation(moodEvent);
            return true; // Consume the long click
        });
    }

    /**
     * Save mood event changes to the database
     *
     * @param updatedMood The updated mood event
     */
    private void saveMoodEventChanges(MoodEventModel updatedMood) {
        // Find the document ID and save changes
        DatabaseManager.getInstance().findMoodEventDocumentId(
                updatedMood.getTimestamp(),
                updatedMood.getEmotion(),
                task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String documentId = task.getResult().getId();

                        DatabaseManager.getInstance().updateMoodEvent(updatedMood, documentId, updateTask -> {
                            if (updateTask.isSuccessful()) {
                                Toast.makeText(getContext(), "Changes saved", Toast.LENGTH_SHORT).show();
                                refreshMoodEventList();
                            } else {
                                Toast.makeText(getContext(), "Failed to save changes", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Error updating mood event", updateTask.getException());
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Could not find mood event to update", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error finding mood event document ID", task.getException());
                    }
                }
        );
    }

    /**
     * Show a confirmation dialog for deleting a mood event.
     *
     * @param moodEvent The mood event to delete.
     */
    private void showDeleteConfirmation(MoodEventModel moodEvent) {
        DeleteConfirmationDialog dialog = new DeleteConfirmationDialog(() -> {
            // Delete the mood event
            deleteMoodEvent(moodEvent);
        });
        dialog.show(getChildFragmentManager(), "delete_confirmation");
    }

    /**
     * Delete a mood event from the database and update the UI.
     *
     * @param moodEvent The mood event to delete.
     */
    private void deleteMoodEvent(MoodEventModel moodEvent) {
        // First find the document ID for this mood event
        DatabaseManager.getInstance().findMoodEventDocumentId(
                moodEvent.getTimestamp(),
                moodEvent.getEmotion(),
                task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Get the document ID from the document snapshot
                        String documentId = task.getResult().getId();

                        // Now delete the document using its ID
                        DatabaseManager.getInstance().deleteMoodEvent(documentId, deleteTask -> {
                            if (deleteTask.isSuccessful()) {
                                // Remove from local list
                                int position = moodEventList.indexOf(moodEvent);
                                if (position != -1) {
                                    moodEventList.remove(position);
                                    adapter.notifyItemRemoved(position);
                                }

                                // Show success toast
                                Toast.makeText(getContext(), "Mood event deleted", Toast.LENGTH_SHORT).show();
                                // Refresh the list to ensure UI is updated
                                refreshMoodEventList();
                            } else {
                                // Show error toast
                                Toast.makeText(getContext(), "Failed to delete mood event", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Error deleting mood event", deleteTask.getException());
                            }
                        });
                    } else {
                        // Show error toast if document not found
                        Toast.makeText(getContext(), "Could not find mood event to delete", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error finding mood event document ID", task.getException());
                    }
                }
        );
    }

    /**
     * Adds a new mood to the list and scrolls to the top to display it.
     *
     * @param moodEvent The new MoodEventModel to add.
     */
    public void addNewMood(MoodEventModel moodEvent) {
        if (adapter != null) {
            adapter.addItem(moodEvent);
            recyclerView.smoothScrollToPosition(0); // Scroll to top to show the new entry
        } else {
            newMood = moodEvent; // Save mood to add it later if adapter is not ready
        }
    }

    /**
     * Resets the search filter when the fragment is resumed.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.filter("", false, new HashSet<>(), isPrivateSelected);
            // Refresh the mood events list
            refreshMoodEventList();
        }
    }

    /**
     * Fetches the latest mood events from Firebase and updates the UI.
     * Call this method whenever the mood list needs to be refreshed.
     */
    private void refreshMoodEventList() {
        // Fetch mood events for the logged-in user from Firestore
        DatabaseManager.getInstance().fetchMoodEvents(task -> {
            if (task.isSuccessful()) {
                List<MoodEventModel> newList = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    MoodEventModel moodEvent = document.toObject(MoodEventModel.class);
                    if (moodEvent != null) {
                        // Set the document ID from Firestore
                        moodEvent.setDocumentId(document.getId());
                        newList.add(moodEvent);
                    }
                }

                // Update the adapter with new data
                adapter.updateList(newList);

                // Update the class field to maintain the latest list
                moodEventList.clear();
                moodEventList.addAll(newList);
            } else {
                Log.e(TAG, "Error getting documents: ", task.getException());
            }
        });
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
     * Provides a sample list of mood events for testing purposes.
     *
     * @return A list of sample MoodEventModel objects.
     */
    private List<MoodEventModel> getSampleMoodEvents() {
        List<MoodEventModel> list = new ArrayList<>();
        list.add(new MoodEventModel("Happiness", "test", parseDate("2025-02-12 08:15"), "😊", R.color.color_happiness, false, true, 51.0447, -114.0719));
        list.add(new MoodEventModel("Anger", "test", parseDate("2025-02-11 03:42"), "😠", R.color.color_anger, false, true, 40.7128, -74.0060));
        list.add(new MoodEventModel("Fear", "test", parseDate("2025-02-07 21:16"), "😢", R.color.color_sadness, false, true, 48.8566, 2.3522));
        return list;
    }
}