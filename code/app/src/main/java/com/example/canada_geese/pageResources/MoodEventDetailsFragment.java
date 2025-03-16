package com.example.canada_geese.pageResources;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.canada_geese.Managers.DatabaseManager;
import com.example.canada_geese.Models.EmotionalState;
import com.example.canada_geese.Models.MoodEventModel;
import com.example.canada_geese.R;
import com.google.android.material.card.MaterialCardView;

/**
 * A fragment that displays detailed information about a mood event,
 * including options to edit or delete the mood event.
 */
public class MoodEventDetailsFragment extends Fragment {
    private static final String TAG = "MoodEventDetails";
    private MaterialCardView cardMoodDetails;
    private TextView tvEmoji;
    private TextView tvMoodName;
    private TextView tvTimestamp;
    private TextView tvDescription;
    private CheckBox cbTriggerWarning;
    private EditText etAdditionalInfo;
    private Button btnEdit;
    private Button btnDelete;
    private View viewDetailsLayout;
    private View viewEditLayout;

    private boolean isEditMode = false;
    private String moodEventId;
    private MoodEventModel currentMoodEvent;

    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views.
     * @param container          The parent view that this fragment's UI should be attached to.
     * @param savedInstanceState A previous saved state of the fragment, if available.
     * @return The root view for this fragment's layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mood_event_details, container, false);
    }

    /**
     * Called immediately after onCreateView has returned.
     * Initializes views and sets up listeners.
     *
     * @param view               The view returned by onCreateView.
     * @param savedInstanceState A previous saved state of the fragment, if available.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupListeners();

        // Initially show details view, not edit view
        showDetailsView();

        // Retrieve mood event ID from arguments and load the event if available.
        if (getArguments() != null) {
            moodEventId = getArguments().getString("mood_event_id");
            if (moodEventId != null) {
                loadMoodEvent();
            }
        }
    }

    /**
     * Initializes view components for this fragment.
     *
     * @param view The root view for this fragment's layout.
     */
    private void initViews(View view) {
        // Details view components
        cardMoodDetails = view.findViewById(R.id.card_mood_details);
        tvEmoji = view.findViewById(R.id.tv_emoji);
        tvMoodName = view.findViewById(R.id.tv_mood_name);
        tvTimestamp = view.findViewById(R.id.tv_timestamp);
        tvDescription = view.findViewById(R.id.tv_description);
        cbTriggerWarning = view.findViewById(R.id.cb_trigger_warning);
        btnEdit = view.findViewById(R.id.btn_edit);
        btnDelete = view.findViewById(R.id.btn_delete);

        // Edit view components
        etAdditionalInfo = view.findViewById(R.id.et_additional_info);
        Button btnSave = view.findViewById(R.id.btn_save);

        // Get the layout containers for details view and edit view
        viewDetailsLayout = view.findViewById(R.id.details_layout);
        viewEditLayout = view.findViewById(R.id.edit_layout);

        // Set the same listeners for both edit and save buttons
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> toggleEditMode());
        }
    }

    /**
     * Shows the details view and hides the edit view.
     */
    private void showDetailsView() {
        if (viewDetailsLayout != null && viewEditLayout != null) {
            viewDetailsLayout.setVisibility(View.VISIBLE);
            viewEditLayout.setVisibility(View.GONE);
        }
        isEditMode = false;
    }

    /**
     * Shows the edit view and hides the details view.
     */
    private void showEditView() {
        if (viewDetailsLayout != null && viewEditLayout != null) {
            viewDetailsLayout.setVisibility(View.GONE);
            viewEditLayout.setVisibility(View.VISIBLE);
        }
        isEditMode = true;
    }

    /**
     * Sets up button click listeners for editing and deleting mood events.
     */
    private void setupListeners() {
        btnEdit.setOnClickListener(v -> toggleEditMode());
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
    }

    /**
     * Loads the mood event data based on the provided mood event ID.
     */
    private void loadMoodEvent() {
        Log.d(TAG, "Loading mood event with ID: " + moodEventId);

        // For now, we're using the passed data from the adapter
        // In a real app, you would query Firebase using the moodEventId

        // TODO: Implement actual loading from Firebase when database integration is complete
        // For example:
        DatabaseManager.getInstance().fetchMoodEvents(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (com.google.firebase.firestore.DocumentSnapshot doc : task.getResult()) {
                    MoodEventModel mood = doc.toObject(MoodEventModel.class);
                    if (mood != null) {
                        String eventId = mood.getEmotion() + "_" + mood.getTimestamp();
                        if (eventId.equals(moodEventId)) {
                            currentMoodEvent = mood;
                            updateUIWithMoodEvent(mood);
                            break;
                        }
                    }
                }
            }
        });
    }

    /**
     * Updates the UI with mood event data.
     *
     * @param moodEvent The mood event to display.
     */
    private void updateUIWithMoodEvent(MoodEventModel moodEvent) {
        if (moodEvent != null && getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                // Update details view
                tvEmoji.setText(moodEvent.getEmoji());
                tvMoodName.setText(moodEvent.getEmotion());
                tvTimestamp.setText(moodEvent.getTimestamp());
                cbTriggerWarning.setChecked(moodEvent.hasTriggerWarning());

                // Update description text
                if (tvDescription != null) {
                    tvDescription.setText(moodEvent.getDescription());
                }

                // Update edit view
                View editView = getView();
                if (editView != null) {
                    TextView tvEmojiEdit = editView.findViewById(R.id.tv_emoji_edit);
                    TextView tvMoodNameEdit = editView.findViewById(R.id.tv_mood_name_edit);
                    TextView tvTimestampEdit = editView.findViewById(R.id.tv_timestamp_edit);
                    CheckBox cbTriggerWarningEdit = editView.findViewById(R.id.cb_trigger_warning_edit);

                    if (tvEmojiEdit != null) tvEmojiEdit.setText(moodEvent.getEmoji());
                    if (tvMoodNameEdit != null) tvMoodNameEdit.setText(moodEvent.getEmotion());
                    if (tvTimestampEdit != null) tvTimestampEdit.setText(moodEvent.getTimestamp());
                    if (cbTriggerWarningEdit != null) cbTriggerWarningEdit.setChecked(moodEvent.hasTriggerWarning());

                    if (etAdditionalInfo != null) {
                        etAdditionalInfo.setText(moodEvent.getDescription());
                    }
                }

                // Set the card background color for both views
                if (moodEvent.getColor() != 0 && getContext() != null) {
                    int color = getContext().getColor(moodEvent.getColor());
                    cardMoodDetails.setCardBackgroundColor(color);

                    // Also set the color for the edit card if it exists
                    View view = getView();
                    if (view != null) {
                        MaterialCardView cardMoodEdit = view.findViewById(R.id.card_mood_edit);
                        if (cardMoodEdit != null) {
                            cardMoodEdit.setCardBackgroundColor(color);
                        }
                    }
                }
            });
        }
    }

    /**
     * Toggles the edit mode for mood event details.
     * Switches between details view and edit view.
     */
    private void toggleEditMode() {
        if (!isEditMode) {
            // Switch to edit mode
            showEditView();
            btnEdit.setText(R.string.save);
        } else {
            // Switch back to details view and save changes
            showDetailsView();
            btnEdit.setText(R.string.edit_mood);
            saveMoodEvent();
        }
    }

    /**
     * Saves the edited mood event details to the database.
     */
    private void saveMoodEvent() {
        if (currentMoodEvent == null) return;

        // Get the edited data
        View view = getView();
        CheckBox cbTriggerWarningEdit = view != null ? view.findViewById(R.id.cb_trigger_warning_edit) : null;
        boolean hasTriggerWarning = cbTriggerWarningEdit != null ? cbTriggerWarningEdit.isChecked() : false;
        String additionalInfo = etAdditionalInfo != null ? etAdditionalInfo.getText().toString().trim() : "";

        // Create an updated mood event with the new values
        MoodEventModel updatedMood = new MoodEventModel(
                currentMoodEvent.getEmotion(),
                additionalInfo,
                currentMoodEvent.getTimestamp(),
                currentMoodEvent.getEmoji(),
                currentMoodEvent.getColor(),
                hasTriggerWarning,
                currentMoodEvent.HasLocation(),
                currentMoodEvent.getLatitude(),
                currentMoodEvent.getLongitude()
        );

        // Find the document ID for this mood event
        DatabaseManager.getInstance().findMoodEventDocumentId(
                currentMoodEvent.getTimestamp(),
                currentMoodEvent.getEmotion(),
                task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Get the document ID from the document snapshot
                        String documentId = task.getResult().getId();

                        // Update the document using its ID
                        DatabaseManager.getInstance().updateMoodEvent(updatedMood, documentId, updateTask -> {
                            if (updateTask.isSuccessful()) {
                                // Show success toast
                                Toast.makeText(getContext(), "Changes saved", Toast.LENGTH_SHORT).show();

                                // Return to the previous screen (home page)
                                if (getActivity() != null) {
                                    getActivity().onBackPressed();
                                }
                            } else {
                                // Show error toast
                                Toast.makeText(getContext(), "Failed to save changes", Toast.LENGTH_SHORT).show();
                                if (task.getException() != null) {
                                    Log.e(TAG, "Error updating mood event", updateTask.getException());
                                }
                            }
                        });
                    } else {
                        // Show error toast if document not found
                        Toast.makeText(getContext(), "Could not find mood event to update", Toast.LENGTH_SHORT).show();
                        if (task.getException() != null) {
                            Log.e(TAG, "Error finding mood event document ID", task.getException());
                        }
                    }
                }
        );
    }

    /**
     * Displays a confirmation dialog for deleting a mood event.
     */
    private void showDeleteConfirmation() {
        DeleteConfirmationDialog dialog = new DeleteConfirmationDialog(
                () -> {
                    // Delete the mood event
                    deleteMoodEvent();
                }
        );
        dialog.show(getChildFragmentManager(), "delete_confirmation");
    }

    /**
     * Deletes the current mood event from the database.
     */
    private void deleteMoodEvent() {
        if (currentMoodEvent == null) return;

        // First find the document ID for this mood event
        DatabaseManager.getInstance().findMoodEventDocumentId(
                currentMoodEvent.getTimestamp(),
                currentMoodEvent.getEmotion(),
                task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Get the document ID from the document snapshot
                        String documentId = task.getResult().getId();

                        // Now delete the document using its ID
                        DatabaseManager.getInstance().deleteMoodEvent(documentId, deleteTask -> {
                            if (deleteTask.isSuccessful()) {
                                // Show success toast
                                Toast.makeText(getContext(), "Mood event deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                // Show error toast
                                Toast.makeText(getContext(), "Failed to delete mood event", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Error deleting mood event", deleteTask.getException());
                            }

                            // Return to the previous screen
                            if (getActivity() != null) {
                                getActivity().onBackPressed();
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
     * Updates the display with the provided mood event information.
     *
     * @param state     The EmotionalState representing the mood.
     * @param moodName  The name of the mood.
     * @param timestamp The timestamp of the mood event.
     */
    public void updateMoodEventDisplay(EmotionalState state, String moodName, String timestamp) {
        if (state != null && getContext() != null) {
            tvEmoji.setText(state.getEmoji());
            tvMoodName.setText(moodName);
            tvTimestamp.setText(timestamp);
            cardMoodDetails.setCardBackgroundColor(
                    requireContext().getColor(state.getColorResId())
            );
        }
    }
}