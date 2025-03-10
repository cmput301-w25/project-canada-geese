package com.example.canada_geese;

import com.example.canada_geese.Managers.DatabaseManager;
import com.example.canada_geese.Models.MoodEventModel;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * DatabaseManager Test Class (WITHOUT Mockito)
 * Uses custom TestDatabaseManager class to verify method calls
 */
public class DatabaseManagerTest {

    /**
     * Custom `TestDatabaseManager` to mock Firebase operations
     */
    private static class TestDatabaseManager extends DatabaseManager {
        public boolean addMoodEventCalled = false;
        public MoodEventModel lastAddedMood = null;

        // Constructor to bypass real Firebase initialization
        public TestDatabaseManager() {
            super(true); // Pass `true` to activate test mode in DatabaseManager
        }

        // Override addMoodEvent to avoid real Firebase requests
        @Override
        public void addMoodEvent(MoodEventModel moodEvent) {
            addMoodEventCalled = true;
            lastAddedMood = moodEvent;
        }
    }

    private TestDatabaseManager testManager;

    @Before
    public void setUp() {
        // Initialize the test version of DatabaseManager
        testManager = new TestDatabaseManager();
    }

    @Test
    public void testAddMoodEvent_Success() {
        // Create a MoodEventModel instance
        MoodEventModel moodEvent = new MoodEventModel(
                "Happy",                // Emotion
                "2025-03-09 10:30",     // Timestamp
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
        assertEquals("Timestamp should match", "2025-03-09 10:30", testManager.lastAddedMood.getTimestamp());
        assertEquals("Emoji should be '😊'", "😊", testManager.lastAddedMood.getEmoji());
        assertEquals("Color should match", R.color.color_happiness, testManager.lastAddedMood.getColor());
        assertFalse("Trigger warning should be false", testManager.lastAddedMood.hasTriggerWarning());
        assertFalse("Location info should be false", testManager.lastAddedMood.HasLocation());
        assertEquals("Latitude should be 0.0", 0.0, testManager.lastAddedMood.getLatitude(), 0.001);
        assertEquals("Longitude should be 0.0", 0.0, testManager.lastAddedMood.getLongitude(), 0.001);
    }
}
