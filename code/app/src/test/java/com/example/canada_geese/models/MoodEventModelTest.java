package com.example.canada_geese.models;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.canada_geese.Models.MoodEventModel;
import com.example.canada_geese.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MoodEventModelTest {

    private SimpleDateFormat dateFormat;

    @Before
    public void setUp() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    @Test
    public void testConstructorAndGetters() throws ParseException {
        Date timestamp = dateFormat.parse("2023-10-01");

        MoodEventModel moodEvent = new MoodEventModel(
                "Happy", "",
                timestamp,
                "😊",
                R.color.mood_happiness,
                true,
                true,
                0.0,
                0.0
        );

        assertEquals("Happy", moodEvent.getEmotion());
        assertEquals(timestamp, moodEvent.getTimestamp());
        assertEquals("😊", moodEvent.getEmoji());
        assertEquals(R.color.mood_happiness, moodEvent.getColor());
        assertTrue(moodEvent.hasTriggerWarning());
    }

    @Test
    public void testHasTriggerWarning_True() throws ParseException {
        Date timestamp = dateFormat.parse("2023-10-01");

        MoodEventModel moodEvent = new MoodEventModel(
                "Happy", "",
                timestamp,
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
    public void testHasTriggerWarning_False() throws ParseException {
        Date timestamp = dateFormat.parse("2023-10-01");

        MoodEventModel moodEvent = new MoodEventModel(
                "Happy", "",
                timestamp,
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
    public void testMultipleInstancesAreIndependent() throws ParseException {
        Date timestamp1 = dateFormat.parse("2023-10-01");
        Date timestamp2 = dateFormat.parse("2023-10-02");

        MoodEventModel mood1 = new MoodEventModel("Happy", "", timestamp1, "😊",
                R.color.mood_happiness, false, false, 0.0, 0.0);
        MoodEventModel mood2 = new MoodEventModel("Angry", "", timestamp2, "😠",
                R.color.mood_anger, true, true, 1.0, 1.0);

        assertNotEquals("Emotions should be different", mood1.getEmotion(), mood2.getEmotion());
        assertNotEquals("Timestamps should be different", mood1.getTimestamp(), mood2.getTimestamp());
        assertNotEquals("Emojis should be different", mood1.getEmoji(), mood2.getEmoji());
        assertNotEquals("Colors should be different", mood1.getColor(), mood2.getColor());
        assertNotEquals("Trigger warnings should be different",
                mood1.hasTriggerWarning(), mood2.hasTriggerWarning());
    }

    @Test
    public void testMoodEventWithEmptyStringValues() {
        Date currentDate = new Date();

        MoodEventModel mood = new MoodEventModel("", "", currentDate, "", 0, false, false, 0.0, 0.0);

        assertEquals("Empty emotion should be empty string", "", mood.getEmotion());
        assertEquals("Timestamp should be current date", currentDate, mood.getTimestamp());
        assertEquals("Empty emoji should be empty string", "", mood.getEmoji());
        assertEquals("Color should default to 0", 0, mood.getColor());
        assertFalse("Trigger warning should be false", mood.hasTriggerWarning());
    }

    @Test
    public void testMoodEventWithNullValues() {
        MoodEventModel mood = new MoodEventModel(null, "", null, null, 0, false, false, 0.0, 0.0);

        assertNull("Emotion should be null", mood.getEmotion());
        assertNull("Timestamp should be null", mood.getTimestamp());
        assertNull("Emoji should be null", mood.getEmoji());
        assertEquals("Color should default to 0", 0, mood.getColor());
        assertFalse("Trigger warning should be false", mood.hasTriggerWarning());
    }

    @Test
    public void testSetAndGetImageUrls() {
        MoodEventModel mood = new MoodEventModel();
        ArrayList<String> imageUrls = new ArrayList<>();
        imageUrls.add("https://example.com/image1.jpg");

        mood.setImageUrls(imageUrls);

        assertNotNull("Image URLs should not be null", mood.getImageUrls());
        assertEquals("Should have 1 image URL", 1, mood.getImageUrls().size());
        assertEquals("URL should match", "https://example.com/image1.jpg", mood.getImageUrls().get(0));
    }

    @Test
    public void testMultipleImageHandling() {
        MoodEventModel mood = new MoodEventModel();
        ArrayList<String> imageUrls = new ArrayList<>();
        imageUrls.add("https://example.com/image1.jpg");
        imageUrls.add("https://example.com/image2.jpg");
        imageUrls.add("https://example.com/image3.jpg");

        mood.setImageUrls(imageUrls);

        assertEquals("Should store 3 image URLs", 3, mood.getImageUrls().size());
        assertTrue("Should contain image2.jpg", mood.getImageUrls().contains("https://example.com/image2.jpg"));
    }

    @Test
    public void testSocialSituationField() {
        MoodEventModel mood = new MoodEventModel();
        String situation = "With friends";

        mood.setSocialSituation(situation);

        assertEquals("Social situation should be set and retrieved correctly", "With friends", mood.getSocialSituation());
    }

}