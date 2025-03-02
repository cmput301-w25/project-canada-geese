package com.example.canada_geese.Models;

import com.example.canada_geese.R;

public enum EmotionalState {
    HAPPINESS("Happiness", R.color.mood_happiness, "😊"),
    ANGER("Anger", R.color.mood_anger, "😠"),
    SADNESS("Sadness", R.color.mood_sadness, "😢"),
    FEAR("Fear", R.color.mood_fear, "😨"),
    CONFUSION("Confusion", R.color.mood_confusion, "😕"),
    DISGUST("Disgust", R.color.mood_disgust, "🤢"),
    SHAME("Shame", R.color.mood_shame, "😳"),
    SURPRISE("Surprise", R.color.mood_surprise, "😮"),
    CALM("Calm", R.color.mood_calm, "😌");

    private final String displayName;
    private final int colorResId;
    private final String emoji;

    EmotionalState(String displayName, int colorResId, String emoji) {
        this.displayName = displayName;
        this.colorResId = colorResId;
        this.emoji = emoji;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getColorResId() {
        return colorResId;
    }

    public String getEmoji() {
        return emoji;
    }

    public static EmotionalState fromString(String name) {
        for (EmotionalState state : EmotionalState.values()) {
            if (state.displayName.equalsIgnoreCase(name)) {
                return state;
            }
        }
        throw new IllegalArgumentException("No emotional state found for name: " + name);
    }
}
