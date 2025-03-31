package com.example.canada_geese.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "pending_mood_events")
public class PendingMoodEvent {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String emotion;
    public String description;
    public String emoji;
    public int color;
    public boolean isPrivate;
    public boolean hasLocation;
    public double latitude;
    public double longitude;
    public String socialSituation;
    public long timestamp;

    public List<String> imagePaths;
}