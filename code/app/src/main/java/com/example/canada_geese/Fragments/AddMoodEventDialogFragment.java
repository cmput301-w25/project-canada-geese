package com.example.canada_geese.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.canada_geese.Managers.DatabaseManager;
import com.example.canada_geese.Models.MoodEventModel;
import com.example.canada_geese.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddMoodEventDialogFragment extends DialogFragment {
    private Spinner moodSpinner;
    private Button addMoodButton;
    private OnMoodAddedListener moodAddedListener;

    public interface OnMoodAddedListener {
        void onMoodAdded(MoodEventModel moodEvent);
    }

    public void setOnMoodAddedListener(OnMoodAddedListener listener) {
        this.moodAddedListener = listener;
    }


    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_mood);

        dialog.setOnShowListener(dialogInterface -> {
            Window window = ((Dialog) dialogInterface).getWindow();
            if (window != null) {
                window.setBackgroundDrawableResource(R.drawable.dialog_background); // 确保背景生效
            }
        });

        return dialog;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            if (window != null) {
                int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85); // 85% 屏幕宽度
                window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        }
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_mood, container, false);

        moodSpinner = view.findViewById(R.id.emotion_spinner);
        addMoodButton = view.findViewById(R.id.add_mood_button);

        // 设置 Spinner 适配器
        String[] moodArray = new String[]{
                "Happiness 😊", "Anger 😠", "Sadness 😢", "Fear 😨",
                "Calm 😌", "Confusion 😕", "Disgust 🤢", "Shame 😳", "Surprise 😮"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                moodArray
        );
        moodSpinner.setAdapter(adapter);

        addMoodButton.setOnClickListener(v -> {
            String selectedMood = moodSpinner.getSelectedItem().toString();
            String moodName = selectedMood.split(" ")[0];

            // Create a new MoodEventModel
            MoodEventModel newEvent = new MoodEventModel(
                    moodName,
                    getCurrentTimestamp(),
                    getEmojiForEmotion(moodName),
                    getColorForEmotion(moodName),
                    false  // No trigger warning for now
            );

            // Add mood event to Firestore using DatabaseManager
            DatabaseManager.getInstance().addMoodEvent(newEvent);

            // Log for debugging
            Log.d("AddMoodEventDialog", "Attempted to add mood event: " + newEvent.getEmotion());

            // Close the dialog
            dismiss();
        });

        return view;
    }

    private String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
    }

    private String getEmojiForEmotion(String emotion) {
        switch (emotion) {
            case "Happiness": return "😊";
            case "Anger": return "😠";
            case "Sadness": return "😢";
            case "Fear": return "😨";
            case "Calm": return "😌";
            case "Confusion": return "😕";
            case "Disgust": return "🤢";
            case "Shame": return "😳";
            case "Surprise": return "😮";
            default: return "😐";
        }
    }

    private int getColorForEmotion(String emotion) {
        switch (emotion) {
            case "Happiness": return R.color.color_happiness;
            case "Anger": return R.color.color_anger;
            case "Sadness": return R.color.color_sadness;
            case "Fear": return R.color.color_fear;
            case "Calm": return R.color.color_calm;
            case "Confusion": return R.color.color_confusion;
            case "Disgust": return R.color.color_disgust;
            case "Shame": return R.color.color_shame;
            case "Surprise": return R.color.color_surprise;
            default: return R.color.colorPrimaryDark;
        }
    }
}
