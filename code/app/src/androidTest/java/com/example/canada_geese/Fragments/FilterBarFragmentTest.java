package com.example.canada_geese.Fragments;
import com.example.canada_geese.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.canada_geese.Pages.MainActivity;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * UI tests for the FilterBarFragment
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class FilterBarFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    /**
     * Helper method to wait for a specified number of milliseconds
     */
    public static ViewAction waitFor(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }

    @Before
    public void setup() {
        // Wait for the main interface to load
        onView(isRoot()).perform(waitFor(1000));

        // Ensure we're on the user moods page
        onView(withId(R.id.page1)).perform(click());
        onView(isRoot()).perform(waitFor(500));

        // Open the filter bar
        onView(withId(R.id.filter_button)).perform(click());
        onView(isRoot()).perform(waitFor(500));
    }

    /**
     * Test: Filter by last 7 days
     * Verifies the "Last 7 Days" chip can be clicked and shows visual feedback
     */
    @Test
    public void testFilter_Last7Days() {
        // Verify the filter bar fragment container is displayed
        onView(withId(R.id.filter_bar_fragment_container)).check(matches(isDisplayed()));

        // Find and click the "Last 7 Days" chip
        onView(withId(R.id.chip_last_7_days)).perform(ViewActions.scrollTo(), click());

        // Give UI time to update
        onView(isRoot()).perform(waitFor(500));

        // Verify the chip is selected (checked)
        onView(withId(R.id.chip_last_7_days)).check(matches(isChecked()));
    }

    /**
     * Test: Filter by mood selection - Happiness
     */
    @Test
    public void testFilter_SelectMood() {
        // Verify the filter bar fragment container is displayed
        onView(withId(R.id.filter_bar_fragment_container)).check(matches(isDisplayed()));

        // Find and click the Happiness chip
        onView(withId(R.id.chip_mood_happiness)).perform(ViewActions.scrollTo(), click());

        // Give UI time to update
        onView(isRoot()).perform(waitFor(500));

        // Verify Happiness chip is selected (checked)
        onView(withId(R.id.chip_mood_happiness)).check(matches(isChecked()));
    }

    /**
     * Test: Clear all filters
     */
    @Test
    public void testFilter_ClearAll() {
        // First, set up some filters

        // 1. Select "Last 7 Days"
        onView(withId(R.id.chip_last_7_days)).perform(ViewActions.scrollTo(), click());
        onView(isRoot()).perform(waitFor(300));

        // 2. Select Happiness mood
        onView(withId(R.id.chip_mood_happiness)).perform(ViewActions.scrollTo(), click());
        onView(isRoot()).perform(waitFor(300));

        // Verify filters are selected
        onView(withId(R.id.chip_last_7_days)).check(matches(isChecked()));
        onView(withId(R.id.chip_mood_happiness)).check(matches(isChecked()));

        // Now test clearing all filters
        onView(withId(R.id.chip_clear_all)).perform(ViewActions.scrollTo(), click());
        onView(isRoot()).perform(waitFor(500));

        // Verify filters are reset (not checked)
        onView(withId(R.id.chip_last_7_days)).check(matches(isNotChecked()));
        onView(withId(R.id.chip_mood_happiness)).check(matches(isNotChecked()));
    }

    /**
     * Test: Default chip selection state
     */
    @Test
    public void testSpinner_DefaultSelection() {
        // Verify the filter bar fragment container is displayed
        onView(withId(R.id.filter_bar_fragment_container)).check(matches(isDisplayed()));

        // Verify mood chips exist and are not selected by default
        onView(withId(R.id.chip_mood_happiness)).perform(ViewActions.scrollTo());
        onView(withId(R.id.chip_mood_happiness)).check(matches(isNotChecked()));

        onView(withId(R.id.chip_mood_sadness)).perform(ViewActions.scrollTo());
        onView(withId(R.id.chip_mood_sadness)).check(matches(isNotChecked()));

        onView(withId(R.id.chip_mood_anger)).perform(ViewActions.scrollTo());
        onView(withId(R.id.chip_mood_anger)).check(matches(isNotChecked()));
    }

    /**
     * Test: Combined filters and clear
     */
    @Test
    public void testFilter_CombinedAndClear() {
        // Apply multiple filters
        onView(withId(R.id.chip_last_7_days)).perform(ViewActions.scrollTo(), click());
        onView(isRoot()).perform(waitFor(300));

        onView(withId(R.id.chip_mood_happiness)).perform(ViewActions.scrollTo(), click());
        onView(isRoot()).perform(waitFor(300));

        // Verify both filters are selected
        onView(withId(R.id.chip_last_7_days)).check(matches(isChecked()));
        onView(withId(R.id.chip_mood_happiness)).check(matches(isChecked()));

        // Clear all filters
        onView(withId(R.id.chip_clear_all)).perform(ViewActions.scrollTo(), click());
        onView(isRoot()).perform(waitFor(500));

        // Verify filters are cleared
        onView(withId(R.id.chip_last_7_days)).check(matches(isNotChecked()));
        onView(withId(R.id.chip_mood_happiness)).check(matches(isNotChecked()));

        // Apply a different mood filter
        onView(withId(R.id.chip_mood_sadness)).perform(ViewActions.scrollTo(), click());
        onView(isRoot()).perform(waitFor(500));

        // Verify the new filter is applied
        onView(withId(R.id.chip_mood_sadness)).check(matches(isChecked()));
    }

    /**
     * Test: Private mood filter chip
     * Tests the private mood filter functionality
     */
    @Test
    public void testPrivateFilterChip() {
        // Verify the filter bar fragment container is displayed
        onView(withId(R.id.filter_bar_fragment_container)).check(matches(isDisplayed()));

        // First scroll to the private chip to ensure it's visible
        onView(withText("Private")).perform(ViewActions.scrollTo());

        // Now check it's visible and not checked by default
        onView(withId(R.id.chip_mood_private)).check(matches(isDisplayed()));
        onView(withId(R.id.chip_mood_private)).check(matches(isNotChecked()));

        // Click on the private mood chip
        onView(withId(R.id.chip_mood_private)).perform(click());
        onView(isRoot()).perform(waitFor(500));

        // Verify it's now checked
        onView(withId(R.id.chip_mood_private)).check(matches(isChecked()));

        // Test combining with another filter
        onView(withId(R.id.chip_mood_happiness)).perform(ViewActions.scrollTo(), click());
        onView(isRoot()).perform(waitFor(300));

        // Verify both filters are selected
        onView(withId(R.id.chip_mood_private)).perform(ViewActions.scrollTo());
        onView(withId(R.id.chip_mood_private)).check(matches(isChecked()));

        onView(withId(R.id.chip_mood_happiness)).perform(ViewActions.scrollTo());
        onView(withId(R.id.chip_mood_happiness)).check(matches(isChecked()));

        // Clear all filters
        onView(withId(R.id.chip_clear_all)).perform(ViewActions.scrollTo(), click());
        onView(isRoot()).perform(waitFor(500));

        // Verify private filter is cleared
        onView(withId(R.id.chip_mood_private)).perform(ViewActions.scrollTo());
        onView(withId(R.id.chip_mood_private)).check(matches(isNotChecked()));
    }
}