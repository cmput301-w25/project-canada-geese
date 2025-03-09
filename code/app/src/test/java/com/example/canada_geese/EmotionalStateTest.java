package com.example.canada_geese;


import org.junit.Test;
import static org.junit.Assert.*;


import com.example.canada_geese.Models.EmotionalState;
import com.example.canada_geese.R;


public class EmotionalStateTest {


    @Test
    public void testGetDisplayName() {
        assertEquals("Happiness", EmotionalState.HAPPINESS.getDisplayName());
        assertEquals("Anger", EmotionalState.ANGER.getDisplayName());
        assertEquals("Sadness", EmotionalState.SADNESS.getDisplayName());
        assertEquals("Fear", EmotionalState.FEAR.getDisplayName());
        assertEquals("Confusion", EmotionalState.CONFUSION.getDisplayName());
        assertEquals("Disgust", EmotionalState.DISGUST.getDisplayName());
        assertEquals("Shame", EmotionalState.SHAME.getDisplayName());
        assertEquals("Surprise", EmotionalState.SURPRISE.getDisplayName());
        assertEquals("Calm", EmotionalState.CALM.getDisplayName());
    }


    @Test
    public void testGetColorResId() {
        assertEquals(R.color.mood_happiness, EmotionalState.HAPPINESS.getColorResId());
        assertEquals(R.color.mood_anger, EmotionalState.ANGER.getColorResId());
        assertEquals(R.color.mood_sadness, EmotionalState.SADNESS.getColorResId());
        assertEquals(R.color.mood_fear, EmotionalState.FEAR.getColorResId());
        assertEquals(R.color.mood_confusion, EmotionalState.CONFUSION.getColorResId());
        assertEquals(R.color.mood_disgust, EmotionalState.DISGUST.getColorResId());
        assertEquals(R.color.mood_shame, EmotionalState.SHAME.getColorResId());
        assertEquals(R.color.mood_surprise, EmotionalState.SURPRISE.getColorResId());
        assertEquals(R.color.mood_calm, EmotionalState.CALM.getColorResId());
    }


    @Test
    public void testGetEmoji() {
        assertEquals("😊", EmotionalState.HAPPINESS.getEmoji());
        assertEquals("😠", EmotionalState.ANGER.getEmoji());
        assertEquals("😢", EmotionalState.SADNESS.getEmoji());
        assertEquals("😨", EmotionalState.FEAR.getEmoji());
        assertEquals("😕", EmotionalState.CONFUSION.getEmoji());
        assertEquals("🤢", EmotionalState.DISGUST.getEmoji());
        assertEquals("😳", EmotionalState.SHAME.getEmoji());
        assertEquals("😮", EmotionalState.SURPRISE.getEmoji());
        assertEquals("😌", EmotionalState.CALM.getEmoji());
    }


    @Test
    public void testFromStringValid() {
        assertEquals(EmotionalState.HAPPINESS, EmotionalState.fromString("Happiness"));
        assertEquals(EmotionalState.ANGER, EmotionalState.fromString("Anger"));
        assertEquals(EmotionalState.SADNESS, EmotionalState.fromString("sadness"));
        assertEquals(EmotionalState.SURPRISE, EmotionalState.fromString("SurPriSe"));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testFromStringInvalid() {
        EmotionalState.fromString("NonexistentEmotion");
    }


    @Test(expected = IllegalArgumentException.class)
    public void testFromStringEmptyString() {
        EmotionalState.fromString("");
    }


    @Test(expected = IllegalArgumentException.class)
    public void testFromStringNull() {
        EmotionalState.fromString(null);
    }

    @Test
    public void testAllStatesHaveValidProperties() {
        for (EmotionalState state : EmotionalState.values()) {
            assertNotNull("Display name should not be null", state.getDisplayName());
            assertFalse("Display name should not be empty", state.getDisplayName().isEmpty());
            assertNotNull("Emoji should not be null", state.getEmoji());
            assertFalse("Emoji should not be empty", state.getEmoji().isEmpty());
            assertTrue("Color resource ID should be positive", state.getColorResId() > 0);
        }
    }

    @Test
    public void testEmotionalStateCount() {
        assertEquals(9, EmotionalState.values().length);
    }
}

