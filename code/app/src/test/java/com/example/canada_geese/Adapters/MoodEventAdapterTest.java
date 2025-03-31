package com.example.canada_geese.Adapters;

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
import java.util.*;

/**
 * Unit tests for the MoodEventAdapter class using Robolectric.
 * This test class verifies filtering, updating, and item management functionality.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MoodEventAdapterTest {

    @Mock
    private Context mockContext;

    private TestAdapter adapter;
    private List<MoodEventModel> originalList;

    /**
     * Set up test data before each test runs.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        originalList = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        MoodEventModel todayMood = new MoodEventModel(
                "Happiness", "Today I'm happy", new Date(), "\uD83D\uDE0A",
                R.color.color_happiness, false, false, 0.0, 0.0);

        cal.add(Calendar.DAY_OF_YEAR, -10);
        MoodEventModel tenDaysAgoMood = new MoodEventModel(
                "Anger", "I was angry last week", cal.getTime(), "\uD83D\uDE20",
                R.color.color_anger, true, false, 0.0, 0.0);

        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -3);
        MoodEventModel threeDaysAgoMood = new MoodEventModel(
                "Sadness", "Feeling sad", cal.getTime(), "\uD83D\uDE22",
                R.color.color_sadness, false, false, 0.0, 0.0);

        originalList.add(todayMood);
        originalList.add(tenDaysAgoMood);
        originalList.add(threeDaysAgoMood);

        adapter = new TestAdapter(originalList, mockContext, false);
    }

    /**
     * Test: Filter mood events by search query.
     */
    @Test
    public void testFilter_ByQuery() {
        Set<String> emptySet = new HashSet<>();

        adapter.filter("happy", false, emptySet, false);
        assertEquals(1, adapter.getItemCount());
        assertEquals("Happiness", adapter.getItemAtPosition(0).getEmotion());

        adapter.filter("sad", false, emptySet, false);
        assertEquals(1, adapter.getItemCount());
        assertEquals("Sadness", adapter.getItemAtPosition(0).getEmotion());

        adapter.filter("nonexistent", false, emptySet, false);
        assertEquals(0, adapter.getItemCount());

        adapter.filter("", false, emptySet, false);
        assertEquals(3, adapter.getItemCount());
    }

    /**
     * Test: Filter mood events that occurred within the last 7 days.
     */
    @Test
    public void testFilter_ByLast7Days() {
        Set<String> emptySet = new HashSet<>();

        adapter.filter("", true, emptySet, false);
        assertEquals(2, adapter.getItemCount());

        List<String> emotionsFound = new ArrayList<>();
        for (int i = 0; i < adapter.getItemCount(); i++) {
            emotionsFound.add(adapter.getItemAtPosition(i).getEmotion());
        }

        assertTrue(emotionsFound.contains("Happiness"));
        assertTrue(emotionsFound.contains("Sadness"));

        adapter.filter("", false, emptySet, false);
        assertEquals(3, adapter.getItemCount());
    }

    /**
     * Test: Filter mood events by selected emotion categories.
     */
    @Test
    public void testFilter_ByMood() {
        Set<String> moodSet = new HashSet<>();

        moodSet.add("Happiness");
        adapter.filter("", false, moodSet, false);
        assertEquals(1, adapter.getItemCount());
        assertEquals("Happiness", adapter.getItemAtPosition(0).getEmotion());

        moodSet.clear();
        moodSet.add("Anger");
        adapter.filter("", false, moodSet, false);
        assertEquals(1, adapter.getItemCount());
        assertEquals("Anger", adapter.getItemAtPosition(0).getEmotion());

        moodSet.clear();
        moodSet.add("Happiness");
        adapter.filter("happy", false, moodSet, false);
        assertEquals(1, adapter.getItemCount());
        assertEquals("Happiness", adapter.getItemAtPosition(0).getEmotion());

        adapter.filter("", false, new HashSet<>(), false);
        assertEquals(3, adapter.getItemCount());
    }

    /**
     * Test: Replace the adapter list with a new list.
     */
    @Test
    public void testUpdateList() {
        List<MoodEventModel> newList = new ArrayList<>();

        MoodEventModel calm = new MoodEventModel("Calm", "Feeling relaxed", new Date(), "\uD83D\uDE0C",
                R.color.color_calm, false, false, 0.0, 0.0);
        MoodEventModel fear = new MoodEventModel("Fear", "Scary movie", new Date(), "\uD83D\uDE28",
                R.color.color_fear, false, false, 0.0, 0.0);

        newList.add(calm);
        newList.add(fear);

        adapter.updateList(newList);
        assertEquals(2, adapter.getItemCount());
        assertEquals("Calm", adapter.getItemAtPosition(0).getEmotion());
        assertEquals("Fear", adapter.getItemAtPosition(1).getEmotion());
    }

    /**
     * Test: Add a new mood event to the adapter.
     */
    @Test
    public void testAddItem() {
        int originalCount = adapter.getItemCount();

        MoodEventModel newMood = new MoodEventModel("Surprise", "Unexpected event", new Date(), "\uD83D\uDE2E",
                R.color.color_surprise, false, false, 0.0, 0.0);

        adapter.addItem(newMood);
        assertEquals(originalCount + 1, adapter.getItemCount());
        assertEquals("Surprise", adapter.getItemAtPosition(0).getEmotion());
    }

    /**
     * Test: Filter mood events that are marked as private.
     */
    @Test
    public void testFilterByPrivate() {
        Set<String> allMoods = new HashSet<>();

        adapter.filter("", false, allMoods, true);
        assertEquals(1, adapter.getItemCount());
        assertEquals("Anger", adapter.getItemAtPosition(0).getEmotion());

        adapter.filter("", false, allMoods, false);
        assertEquals(3, adapter.getItemCount());
    }

    /**
     * Test: Handle mood events that include image URLs.
     */
    @Test
    public void testHandleImageUrls() {
        List<String> imageUrls = new ArrayList<>();
        imageUrls.add("https://example.com/image1.jpg");
        imageUrls.add("https://example.com/image2.jpg");

        MoodEventModel withImages = new MoodEventModel("Surprise", "Has images", new Date(), "\uD83D\uDE2E",
                R.color.color_surprise, false, false, 0.0, 0.0);
        withImages.setImageUrls((ArrayList<String>) imageUrls);

        adapter.addItem(withImages);
        MoodEventModel result = adapter.getItemAtPosition(0);
        assertEquals(2, result.getImageUrls().size());
        assertEquals("https://example.com/image1.jpg", result.getImageUrls().get(0));
        assertEquals("https://example.com/image2.jpg", result.getImageUrls().get(1));
    }

    /**
     * Internal wrapper class to access private data from MoodEventAdapter for testing.
     */
    private static class TestAdapter {
        private final MoodEventAdapter realAdapter;

        public TestAdapter(List<MoodEventModel> list, Context context, boolean isFriendPage) {
            this.realAdapter = new MoodEventAdapter(list, context, isFriendPage);
        }

        public int getItemCount() {
            return realAdapter.getItemCount();
        }

        public void filter(String query, boolean last7Days, Set<String> selectedMoods, boolean isPrivateSelected) {
            realAdapter.filter(query, last7Days, selectedMoods, isPrivateSelected);
        }

        public void updateList(List<MoodEventModel> newList) {
            realAdapter.updateList(newList);
        }

        public void addItem(MoodEventModel item) {
            realAdapter.addItem(item);
        }

        public MoodEventModel getItemAtPosition(int position) {
            try {
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
