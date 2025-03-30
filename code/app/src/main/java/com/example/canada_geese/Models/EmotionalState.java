package com.example.canada_geese.Models;

import com.example.canada_geese.R;

/**
 * Enum representing different emotional states with associated display names, colors, and emojis.
 */
public enum EmotionalState {
    HAPPY("Happy", R.color.mood_happiness, "😊"),
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

    /**
     * Constructs an EmotionalState with a display name, a color resource ID, and an emoji.
     *
     * @param displayName the display name of the emotion.
     * @param colorResId the resource ID for the color associated with the emotion.
     * @param emoji the emoji representation of the emotion.
     */
    EmotionalState(String displayName, int colorResId, String emoji) {
        this.displayName = displayName;
        this.colorResId = colorResId;
        this.emoji = emoji;
    }

    /**
     * Gets the display name of the emotional state.
     *
     * @return the display name as a String.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the resource ID for the color associated with the emotional state.
     *
     * @return the color resource ID as an integer.
     */
    public int getColorResId() {
        return colorResId;
    }

    /**
     * Gets the emoji representation of the emotional state.
     *
     * @return the emoji as a String.
     */
    public String getEmoji() {
        return emoji;
    }

    /**
     * Converts a string to the corresponding EmotionalState.
     *
     * @param name the name of the emotional state as a String.
     * @return the corresponding EmotionalState.
     * @throws IllegalArgumentException if no matching EmotionalState is found.
     */
    public static EmotionalState fromString(String name) {
        for (EmotionalState state : EmotionalState.values()) {
            if (state.displayName.equalsIgnoreCase(name)) {
                return state;
            }
        }
        throw new IllegalArgumentException("No emotional state found for name: " + name);
    }
}