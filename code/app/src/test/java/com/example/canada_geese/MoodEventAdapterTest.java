package com.example.canada_geese;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import com.example.canada_geese.Adapters.MoodEventAdapter;
import com.example.canada_geese.Models.MoodEventModel;
import com.example.canada_geese.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MoodEventAdapterTest {

    @Mock
    private Context mockContext;

    private TestAdapter adapter;
    private List<MoodEventModel> originalList;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Prepare test data
        originalList = new ArrayList<>();

        // Create mood events with different emotions, descriptions, and dates
        Calendar cal = Calendar.getInstance();

        // Mood from today
        MoodEventModel todayMood = new MoodEventModel(
                "Happiness", "Today I'm happy", new Date(), "😊",
                R.color.color_happiness, false, false, 0.0, 0.0);

        // Mood from 10 days ago
        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -10);
        MoodEventModel tenDaysAgoMood = new MoodEventModel(
                "Anger", "I was angry last week", cal.getTime(), "😠",
                R.color.color_anger, false, false, 0.0, 0.0);

        // Mood from 3 days ago
        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -3);
        MoodEventModel threeDaysAgoMood = new MoodEventModel(
                "Sadness", "Feeling sad", cal.getTime(), "😢",
                R.color.color_sadness, false, false, 0.0, 0.0);

        // Add moods to the list
        originalList.add(todayMood);
        originalList.add(tenDaysAgoMood);
        originalList.add(threeDaysAgoMood);

        // Initialize adapter with mock context and test data
        adapter = new TestAdapter(originalList, mockContext, false);
    }

    @Test
    public void testFilter_ByQuery() {
        // Test filtering by description content
        adapter.filter("happy", false, "");

        // Should find only moods containing "happy" in the description
        assertEquals(1, adapter.getItemCount());
        assertEquals("Happiness", adapter.getItemAtPosition(0).getEmotion());
        assertEquals("Today I'm happy", adapter.getItemAtPosition(0).getDescription());

        // Test filtering by another word
        adapter.filter("sad", false, "");
        assertEquals(1, adapter.getItemCount());
        assertEquals("Sadness", adapter.getItemAtPosition(0).getEmotion());

        // Test with no match
        adapter.filter("nonexistent", false, "");
        assertEquals(0, adapter.getItemCount());

        // Reset filter
        adapter.filter("", false, "");
        assertEquals(3, adapter.getItemCount());
    }

    @Test
    public void testFilter_ByLast7Days() {
        // Test filtering by last 7 days
        adapter.filter("", true, "");

        // Should find moods from today and 3 days ago, but not 10 days ago
        assertEquals(2, adapter.getItemCount());

        // Verify the emotions of the filtered moods (should be Happiness and Sadness)
        List<String> emotionsFound = new ArrayList<>();
        for (int i = 0; i < adapter.getItemCount(); i++) {
            emotionsFound.add(adapter.getItemAtPosition(i).getEmotion());
        }

        assertTrue(emotionsFound.contains("Happiness"));
        assertTrue(emotionsFound.contains("Sadness"));

        // Reset filter
        adapter.filter("", false, "");
        assertEquals(3, adapter.getItemCount());
    }

    @Test
    public void testFilter_ByMood() {
        // Test filtering by specific mood
        adapter.filter("", false, "Happiness");

        // Should find only happiness mood
        assertEquals(1, adapter.getItemCount());
        assertEquals("Happiness", adapter.getItemAtPosition(0).getEmotion());

        // Test filtering by another mood
        adapter.filter("", false, "Anger");
        assertEquals(1, adapter.getItemCount());
        assertEquals("Anger", adapter.getItemAtPosition(0).getEmotion());

        // Test combining mood filter with last 7 days
        adapter.filter("", true, "Happiness");
        assertEquals(1, adapter.getItemCount());
        assertEquals("Happiness", adapter.getItemAtPosition(0).getEmotion());

        // Test combining mood filter with query
        adapter.filter("happy", false, "Happiness");
        assertEquals(1, adapter.getItemCount());
        assertEquals("Happiness", adapter.getItemAtPosition(0).getEmotion());

        // Reset filter
        adapter.filter("", false, "");
        assertEquals(3, adapter.getItemCount());
    }

    @Test
    public void testUpdateList() {
        // Create a new list with different moods
        List<MoodEventModel> newList = new ArrayList<>();

        // Add new test moods
        MoodEventModel calm = new MoodEventModel(
                "Calm", "Feeling relaxed", new Date(), "😌",
                R.color.color_calm, false, false, 0.0, 0.0);
        MoodEventModel fear = new MoodEventModel(
                "Fear", "Scary movie", new Date(), "😨",
                R.color.color_fear, false, false, 0.0, 0.0);

        newList.add(calm);
        newList.add(fear);

        // Update adapter with new list
        adapter.updateList(newList);

        // Check that adapter now contains only the new items
        assertEquals(2, adapter.getItemCount());

        // Verify the emotions match the new list
        assertEquals("Calm", adapter.getItemAtPosition(0).getEmotion());
        assertEquals("Fear", adapter.getItemAtPosition(1).getEmotion());
    }

    @Test
    public void testAddItem() {
        // Save original count
        int originalCount = adapter.getItemCount();

        // Create a new mood to add
        MoodEventModel newMood = new MoodEventModel(
                "Surprise", "Unexpected event", new Date(), "😮",
                R.color.color_surprise, false, false, 0.0, 0.0);

        // Add the new mood
        adapter.addItem(newMood);

        // Verify it was added at the beginning (position 0)
        assertEquals(originalCount + 1, adapter.getItemCount());
        assertEquals("Surprise", adapter.getItemAtPosition(0).getEmotion());
        assertEquals("Unexpected event", adapter.getItemAtPosition(0).getDescription());
    }

    /**
     * Helper class that wraps MoodEventAdapter to access its internal data
     */
    private class TestAdapter {
        private final MoodEventAdapter realAdapter;

        public TestAdapter(List<MoodEventModel> list, Context context, boolean isFriendPage) {
            this.realAdapter = new MoodEventAdapter(list, context, isFriendPage);
        }

        public int getItemCount() {
            return realAdapter.getItemCount();
        }

        public void filter(String query, boolean last7Days, String selectedMood) {
            realAdapter.filter(query, last7Days, selectedMood);
        }

        public void updateList(List<MoodEventModel> newList) {
            realAdapter.updateList(newList);
        }

        public void addItem(MoodEventModel item) {
            realAdapter.addItem(item);
        }

        public MoodEventModel getItemAtPosition(int position) {
            try {
                // Use reflection to access the moodEventList field
                Field field = MoodEventAdapter.class.getDeclaredField("moodEventList");
                field.setAccessible(true);
                List<MoodEventModel> list = (List<MoodEventModel>) field.get(realAdapter);
                return list.get(position);
            } catch (Exception e) {
                throw new RuntimeException("Failed to access moodEventList field", e);
            }
        }
    }
}