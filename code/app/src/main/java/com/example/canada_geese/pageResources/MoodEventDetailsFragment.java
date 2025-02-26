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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mood_event_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupListeners();

        // 从参数中获取心情事件ID
        if (getArguments() != null) {
            moodEventId = getArguments().getString("mood_event_id");
            if (moodEventId != null) {
                loadMoodEvent();
            }
        }
    }

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

    private void setupListeners() {
        btnEdit.setOnClickListener(v -> toggleEditMode());
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void loadMoodEvent() {
        // TODO: Load mood event data from the database (need to work with Member 5)
    }

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

    private void saveMoodEvent() {
        if (!isEditMode) return;

        // Get the edited data
        boolean hasTriggerWarning = cbTriggerWarning.isChecked();
        String additionalInfo = etAdditionalInfo.getText().toString().trim();

        // TODO: Save to database (need to work with Member 5)

        Toast.makeText(getContext(), "Changes saved", Toast.LENGTH_SHORT).show();
    }

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

    public void updateMoodEventDisplay(EmotionalState state, String moodName, String timestamp) {
        tvEmoji.setText(state.getEmoji());
        tvMoodName.setText(moodName);
        tvTimestamp.setText(timestamp);
        cardMoodDetails.setCardBackgroundColor(
                requireContext().getColor(state.getColorResId())
        );
    }
}