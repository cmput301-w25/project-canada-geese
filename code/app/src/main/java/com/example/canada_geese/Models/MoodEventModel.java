package com.example.canada_geese.Models;

import com.google.firebase.firestore.ServerTimestamp;

public class MoodEventModel {
    private String emotion;
    private String description;
    private String timestamp;
    private String emoji;
    private int color;
    private boolean triggerWarning;
    private boolean hasLocation;
    private double latitude;
    private double longitude;

    // No-argument constructor required for Firestore
    public MoodEventModel() {
    }

    public MoodEventModel(String emotion, String description, String timestamp, String emoji, int color, boolean hasTriggerWarning, boolean hasLocation, double latitude, double longitude) {
        this.emotion = emotion;
        this.timestamp = timestamp;
        this.emoji = emoji;
        this.color = color;
        this.triggerWarning = hasTriggerWarning;
        this.hasLocation = hasLocation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
    }

    public String getEmotion() {
        return emotion;
    }

    public String getDescription() {
        return description;
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
    public boolean HasLocation(){return hasLocation;}
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}

