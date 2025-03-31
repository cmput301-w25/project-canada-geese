package com.example.canada_geese.Managers;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.canada_geese.Models.PendingMoodEvent;

@Database(entities = {PendingMoodEvent.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class OfflineDatabase extends RoomDatabase {
    private static OfflineDatabase INSTANCE;

    public abstract PendingMoodDao pendingMoodDao();

    public static synchronized OfflineDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(),
                    OfflineDatabase.class,
                    "offline_mood_db"
            ).build();
        }
        return INSTANCE;
    }
}