package com.example.canada_geese.Models;

public class MoodEventModel {
    private String emotion;
    private String timestamp;
    private String emoji;
    private int color;
    private boolean triggerWarning;

    public MoodEventModel(String emotion, String timestamp, String emoji, int color, boolean hasTriggerWarning) {
        this.emotion = emotion;
        this.timestamp = timestamp;
        this.emoji = emoji;
        this.color = color;
        this.triggerWarning = triggerWarning;
    }

    public String getEmotion() {
        return emotion;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getEmoji() {
        return emoji;
    }

    public int getColor() {
        return color;
    }

    public boolean hasTriggerWarning() {
        return triggerWarning;
    }
}
