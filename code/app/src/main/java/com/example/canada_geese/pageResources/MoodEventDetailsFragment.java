package com.example.canada_geese.pageResources;

import android.os.Bundle;
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

import com.example.canada_geese.R;
import com.example.canada_geese.Models.EmotionalState;
import com.google.android.material.card.MaterialCardView;

/**
 * A fragment that displays detailed information about a mood event,
 * including options to edit or delete the mood event.
 */
public class MoodEventDetailsFragment extends Fragment {
    private MaterialCardView cardMoodDetails;
    private TextView tvEmoji;
    private TextView tvMoodName;
    private TextView tvTimestamp;
    private CheckBox cbTriggerWarning;
    private EditText etAdditionalInfo;
    private Button btnEdit;
    private Button btnDelete;

    private boolean isEditMode = false;
    private String moodEventId;

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
        cardMoodDetails = view.findViewById(R.id.card_mood_details);
        tvEmoji = view.findViewById(R.id.tv_emoji);
        tvMoodName = view.findViewById(R.id.tv_mood_name);
        tvTimestamp = view.findViewById(R.id.tv_timestamp);
        cbTriggerWarning = view.findViewById(R.id.cb_trigger_warning);
        etAdditionalInfo = view.findViewById(R.id.et_additional_info);
        btnEdit = view.findViewById(R.id.btn_edit);
        btnDelete = view.findViewById(R.id.btn_delete);
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
     * Currently, this is a placeholder for future implementation.
     */
    private void loadMoodEvent() {
        // TODO: Load mood event data from the database (need to work with Member 5)
    }

    /**
     * Toggles the edit mode for mood event details.
     * Enables or disables editing of trigger warning and additional info fields.
     */
    private void toggleEditMode() {
        isEditMode = !isEditMode;

        // Updating UI state
        cbTriggerWarning.setEnabled(isEditMode);
        etAdditionalInfo.setEnabled(isEditMode);

        if (isEditMode) {
            btnEdit.setText(R.string.save);
        } else {
            btnEdit.setText(R.string.edit_mood);
            saveMoodEvent();
        }
    }

    /**
     * Saves the edited mood event details.
     * Currently, this is a placeholder for future implementation.
     */
    private void saveMoodEvent() {
        if (!isEditMode) return;

        // Get the edited data
        boolean hasTriggerWarning = cbTriggerWarning.isChecked();
        String additionalInfo = etAdditionalInfo.getText().toString().trim();

        // TODO: Save to database (need to work with Member 5)

        Toast.makeText(getContext(), "Changes saved", Toast.LENGTH_SHORT).show();
    }

    /**
     * Displays a confirmation dialog for deleting a mood event.
     */
    private void showDeleteConfirmation() {
        DeleteConfirmationDialog dialog = new DeleteConfirmationDialog(
                () -> {
                    // TODO: Delete mood events (need to work with Member 5)
                    // Return to the previous page after successful deletion
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                }
        );
        dialog.show(getChildFragmentManager(), "delete_confirmation");
    }

    /**
     * Updates the display with the provided mood event information.
     *
     * @param state     The EmotionalState representing the mood.
     * @param moodName  The name of the mood.
     * @param timestamp The timestamp of the mood event.
     */
    public void updateMoodEventDisplay(EmotionalState state, String moodName, String timestamp) {
        tvEmoji.setText(state.getEmoji());
        tvMoodName.setText(moodName);
        tvTimestamp.setText(timestamp);
        cardMoodDetails.setCardBackgroundColor(
                requireContext().getColor(state.getColorResId())
        );
    }
}
