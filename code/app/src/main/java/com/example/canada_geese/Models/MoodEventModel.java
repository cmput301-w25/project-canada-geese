package com.example.canada_geese.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;

/**
 * Represents a mood event with various attributes such as emotion, timestamp, emoji, color, and location data.
 */
public class MoodEventModel {
    private String emotion;
    private String description;
    private Date timestamp;
    private String emoji;
    private int color;
    private boolean isPrivate;
    private boolean hasLocation;
    private double latitude;
    private double longitude;
    private String userId;
    private String documentId; // New field to store Firestore document ID
    private String socialSituation;
    private ArrayList<String> imageUrls; // URLs to uploaded images

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
     * @param timestamp         the timestamp of the event as a Date.
     * @param emoji             the emoji representation of the emotion.
     * @param color             the color associated with the emotion.
     * @param isPrivate         whether the mood event is private (true) or public (false).
     * @param hasLocation       whether the event includes location data.
     * @param latitude          the latitude of the event location.
     * @param longitude         the longitude of the event location.
     */
    public MoodEventModel(String emotion, String description, Date timestamp, String emoji, int color, boolean isPrivate, boolean hasLocation, double latitude, double longitude) {
        this.emotion = emotion;
        this.description = description;
        this.timestamp = timestamp;
        this.emoji = emoji;
        this.color = color;
        this.isPrivate = isPrivate;
        this.hasLocation = hasLocation;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and setters
    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    // Helper method to get formatted timestamp string for display
    public String getFormattedTimestamp() {
        if (timestamp == null) return "";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
        return sdf.format(timestamp);
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public boolean getIsPrivate() {
        return isPrivate;
    }

    public boolean hasTriggerWarning() {
        return isPrivate;
    }

    public void setTriggerWarning(boolean triggerWarning) {
        this.isPrivate = triggerWarning;
    }

    public boolean getTriggerWarning() {
        return isPrivate;
    }

    public boolean HasLocation() {
        return hasLocation;
    }

    public boolean getHasLocation() {
        return hasLocation;
    }

    public void setHasLocation(boolean hasLocation) {
        this.hasLocation = hasLocation;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // New getter and setter for documentId
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getSocialSituation() {
        return socialSituation;
    }

    public void setSocialSituation(String socialSituation) {
        this.socialSituation = socialSituation;
    }

    public ArrayList<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}