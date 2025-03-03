package com.example.canada_geese.pageResources;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canada_geese.R;
import com.example.canada_geese.Adapters.EmotionalStateAdapter;
import com.example.canada_geese.Models.EmotionalState;

public class MoodEventFragment extends Fragment implements
        EmotionalStateAdapter.OnEmotionalStateSelectedListener {

    private RecyclerView emotionalStateRecyclerView;
    private EmotionalStateAdapter emotionalStateAdapter;
    private CardView moodDetailsCard;
    private EditText moodNameInput;
    private CheckBox triggerWarningCheckbox;
    private EditText additionalInfoInput;
    private Button addMoodButton;
    private EmotionalState selectedState = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mood_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
        setupListeners();
    }

    private void initViews(View view) {
        //emotionalStateRecyclerView = view.findViewById(R.id.rv_emotional_states);
        moodDetailsCard = view.findViewById(R.id.card_mood_details);
        //moodNameInput = view.findViewById(R.id.et_mood_name);
        triggerWarningCheckbox = view.findViewById(R.id.cb_trigger_warning);
        additionalInfoInput = view.findViewById(R.id.et_additional_info);
        //addMoodButton = view.findViewById(R.id.btn_add_mood);
    }

    private void setupRecyclerView() {
        emotionalStateAdapter = new EmotionalStateAdapter(this);
        emotionalStateRecyclerView.setAdapter(emotionalStateAdapter);
        emotionalStateRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext())
        );
    }

    private void setupListeners() {
        addMoodButton.setOnClickListener(v -> addMoodEvent());
    }

    @Override
    public void onEmotionalStateSelected(EmotionalState state) {
        selectedState = state;
        moodDetailsCard.setVisibility(View.VISIBLE);
    }

    private void addMoodEvent() {
        if (selectedState == null) {
            Toast.makeText(getContext(),
                    "Please select an emotional state",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String moodName = moodNameInput.getText().toString().trim();
        if (moodName.isEmpty()) {
            moodNameInput.setError("Please enter a mood name");
            return;
        }

        // Create a mood event object and save it
        // TODO: Implement data saving interaction with Member 5

        // clearform
        clearForm();
    }

    private void clearForm() {
        selectedState = null;
        emotionalStateAdapter.setSelectedState(null);
        moodNameInput.setText("");
        triggerWarningCheckbox.setChecked(false);
        additionalInfoInput.setText("");
        moodDetailsCard.setVisibility(View.GONE);
    }
}