package com.example.canada_geese;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.canada_geese.Models.MoodEventModel;
import com.example.canada_geese.R;

public class MoodEventModelTest {

    @Test
    public void testConstructorAndGetters() {
        // 创建 MoodEventModel 的实例
        MoodEventModel moodEvent = new MoodEventModel(
                "Happy",
                "2023-10-01",
                "😊",
                R.color.mood_happiness,
                true,
                true,
                0.0,
                0.0
        );

        // 验证属性是否正确初始化
        assertEquals("Happy", moodEvent.getEmotion());
        assertEquals("2023-10-01", moodEvent.getTimestamp());
        assertEquals("😊", moodEvent.getEmoji());
        assertEquals(R.color.mood_happiness, moodEvent.getColor());
        assertTrue(moodEvent.hasTriggerWarning());
    }

    @Test
    public void testHasTriggerWarning_True() {
        MoodEventModel moodEvent = new MoodEventModel(
                "Happy",
                "2023-10-01",
                "😊",
                R.color.mood_happiness,
                true,
                true,
                0.0,
                0.0
        );

        assertTrue(moodEvent.hasTriggerWarning());
    }

    @Test
    public void testHasTriggerWarning_False() {
        MoodEventModel moodEvent = new MoodEventModel(
                "Happy",
                "2023-10-01",
                "😊",
                R.color.mood_happiness,
                false,
                false,
                0.0,
                0.0
        );

        assertFalse(moodEvent.hasTriggerWarning());
    }

    @Test
    public void testMultipleInstancesAreIndependent() {
        MoodEventModel mood1 = new MoodEventModel("Happy", "2023-10-01", "😊",
                R.color.mood_happiness, false, false, 0.0, 0.0);
        MoodEventModel mood2 = new MoodEventModel("Angry", "2023-10-02", "😠",
                R.color.mood_anger, true,true, 1.0, 1.0);

        assertNotEquals("Emotions should be different", mood1.getEmotion(), mood2.getEmotion());
        assertNotEquals("Timestamps should be different", mood1.getTimestamp(), mood2.getTimestamp());
        assertNotEquals("Emojis should be different", mood1.getEmoji(), mood2.getEmoji());
        assertNotEquals("Colors should be different", mood1.getColor(), mood2.getColor());
        assertNotEquals("Trigger warnings should be different",
                mood1.hasTriggerWarning(), mood2.hasTriggerWarning());
    }

    @Test
    public void testMoodEventWithEmptyStringValues() {
        MoodEventModel mood = new MoodEventModel("", "", "", 0, false,false,0.0,0.0);

        assertEquals("Empty emotion should be empty string", "", mood.getEmotion());
        assertEquals("Empty timestamp should be empty string", "", mood.getTimestamp());
        assertEquals("Empty emoji should be empty string", "", mood.getEmoji());
        assertEquals("Color should default to 0", 0, mood.getColor());
        assertFalse("Trigger warning should be false", mood.hasTriggerWarning());
    }

    @Test
    public void testMoodEventWithNullValues() {
        MoodEventModel mood = new MoodEventModel(null, null, null, 0, false, false, 0.0, 0.0);

        assertNull("Emotion should be null", mood.getEmotion());
        assertNull("Timestamp should be null", mood.getTimestamp());
        assertNull("Emoji should be null", mood.getEmoji());
        assertEquals("Color should default to 0", 0, mood.getColor());
        assertFalse("Trigger warning should be false", mood.hasTriggerWarning());
    }
}