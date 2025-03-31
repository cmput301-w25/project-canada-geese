package com.example.canada_geese.Managers;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.canada_geese.Models.PendingMoodEvent;

import java.util.List;

@Dao
public interface PendingMoodDao {
    @Insert
    void insert(PendingMoodEvent event);

    @Query("SELECT * FROM pending_mood_events")
    List<PendingMoodEvent> getAll();

    @Delete
    void delete(PendingMoodEvent event);
}