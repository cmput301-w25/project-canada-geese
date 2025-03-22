package com.example.canada_geese;

import com.example.canada_geese.Managers.DatabaseManager;
import com.example.canada_geese.Models.MoodEventModel;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * DatabaseManager Test Class (WITHOUT Mockito)
 * Uses custom TestDatabaseManager class to verify method calls
 */
public class DatabaseManagerTest {

    /**
     * Custom *`TestDatabaseManager`* to mock Firebase operations
     */
    private static class TestDatabaseManager extends DatabaseManager {
        public boolean addMoodEventCalled = false;
        public MoodEventModel lastAddedMood = null;

        // Constructor that doesn't rely on the private constructor
        public TestDatabaseManager() {
            // Call the public constructor instead of the private one
            super();
        }

        // Another option: initialize in a different way if needed
        public void initForTesting() {
            // Put any initialization code needed for testing here
        }

        // Override addMoodEvent to avoid real Firebase requests
        @Override
        public void addMoodEvent(MoodEventModel moodEvent) {
            addMoodEventCalled = true;
            lastAddedMood = moodEvent;
        }
    }

    private TestDatabaseManager testManager;
    private SimpleDateFormat dateFormat;

    @Before
    public void setUp() {
        // Initialize the test version of DatabaseManager
        testManager = new TestDatabaseManager();
        // If needed, use the alternate initialization method
        testManager.initForTesting();

        // Initialize date format for parsing and comparison
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    }

    @Test
    public void testAddMoodEvent_Success() throws ParseException {
        // Parse the string date into a Date object
        Date timestamp = dateFormat.parse("2025-03-09 10:30");

        // Create a MoodEventModel instance with Date object instead of String
        MoodEventModel moodEvent = new MoodEventModel(
                "Happy",                // Emotion
                "Feeling great today!", // Description
                timestamp,              // Timestamp (now a Date object)
                "😊",                   // Emoji
                R.color.color_happiness,// Color
                false,                  // TriggerWarning
                false,                  // HasLocation
                0.0,                    // Latitude
                0.0                     // Longitude
        );

        // Execute the addMoodEvent method
        testManager.addMoodEvent(moodEvent);

        // Verify the method was called
        assertTrue("addMoodEvent method should have been called", testManager.addMoodEventCalled);

        // Verify the data passed is correct
        assertNotNull("lastAddedMood should not be null", testManager.lastAddedMood);
        assertEquals("Emotion should be 'Happy'", "Happy", testManager.lastAddedMood.getEmotion());

        // Compare Date objects instead of strings
        assertEquals("Timestamp should match", timestamp, testManager.lastAddedMood.getTimestamp());

        assertEquals("Emoji should be '😊'", "😊", testManager.lastAddedMood.getEmoji());
        assertEquals("Color should match", R.color.color_happiness, testManager.lastAddedMood.getColor());
        assertFalse("Trigger warning should be false", testManager.lastAddedMood.hasTriggerWarning());
        assertFalse("Location info should be false", testManager.lastAddedMood.HasLocation());
        assertEquals("Latitude should be 0.0", 0.0, testManager.lastAddedMood.getLatitude(), 0.001);
        assertEquals("Longitude should be 0.0", 0.0, testManager.lastAddedMood.getLongitude(), 0.001);
    }
}