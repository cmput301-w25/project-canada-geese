package com.example.canada_geese.Models;

import com.google.firebase.firestore.ServerTimestamp;

/**
 * Represents a mood event with various attributes such as emotion, timestamp, emoji, color, and location data.
 */
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

    /**
     * No-argument constructor required for Firestore.
     */
    public MoodEventModel() {
    }

    /**
     * Constructs a MoodEventModel with the specified parameters.
     *
     * @param emotion           the emotion associated with the event.
     * @param description       a description of the event.
     * @param timestamp         the timestamp of the event as a String.
     * @param emoji             the emoji representation of the emotion.
     * @param color             the color associated with the emotion.
     * @param hasTriggerWarning whether the event has a trigger warning.
     * @param hasLocation       whether the event includes location data.
     * @param latitude          the latitude of the event location.
     * @param longitude         the longitude of the event location.
     */
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

    /**
     * Gets the emotion associated with the event.
     *
     * @return the emotion as a String.
     */
    public String getEmotion() {
        return emotion;
    }

    /**
     * Gets the description of the event.
     *
     * @return the description as a String.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the timestamp of the event.
     *
     * @return the timestamp as a String.
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the emoji representation of the emotion.
     *
     * @return the emoji as a String.
     */
    public String getEmoji() {
        return emoji;
    }

    /**
     * Gets the color associated with the emotion.
     *
     * @return the color as an integer resource ID.
     */
    public int getColor() {
        return color;
    }

    /**
     * Checks if the event has a trigger warning.
     *
     * @return true if the event has a trigger warning, false otherwise.
     */
    public boolean hasTriggerWarning() {
        return triggerWarning;
    }

    /**
     * Checks if the event has location data.
     *
     * @return true if the event has location data, false otherwise.
     */
    public boolean HasLocation() {
        return hasLocation;
    }

    /**
     * Gets the latitude of the event location.
     *
     * @return the latitude as a double.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Gets the longitude of the event location.
     *
     * @return the longitude as a double.
     */
    public double getLongitude() {
        return longitude;
    }
}
