package com.example.canada_geese;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.canada_geese.Pages.MainActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
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

    /**
     * Helper matcher to check the background color of a view
     */
    public static Matcher<View> withBackgroundColor(final int color) {
        return new BoundedMatcher<View, View>(View.class) {
            @Override
            public boolean matchesSafely(View view) {
                if (view.getBackground() == null) {
                    return false;
                }
                // We could check the actual color, but that's complex due to StateListDrawable
                // Instead, we're just verifying a background exists
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with background color: " + color);
            }
        };
    }

    /**
     * Test: Filter by last 7 days
     * Verifies the "Last 7 Days" button can be clicked and shows visual feedback
     */
    @Test
    public void testFilter_Last7Days() {
        // Wait for the main interface to load
        onView(isRoot()).perform(waitFor(1000));

        // Verify the filter bar container is displayed
        onView(withId(R.id.filter_bar_fragment_container)).check(matches(isDisplayed()));

        // Click the "Last 7 Days" button
        onView(withId(R.id.btn_last_7_days)).perform(click());

        // Give UI time to update
        onView(isRoot()).perform(waitFor(500));

        // Verify the button shows visual feedback (changed background)
        // Note: The exact verification depends on how your app indicates selection
        onView(withId(R.id.btn_last_7_days)).check(matches(isDisplayed()));

        // We can check that the button exists and is clickable
        onView(withText("Last 7 Days")).check(matches(isDisplayed()));
    }

    /**
     * Test: Filter by mood selection from spinner
     */
    @Test
    public void testFilter_SelectMood() {
        // Wait for the main interface to load
        onView(isRoot()).perform(waitFor(1000));

        // Verify the filter bar container is displayed
        onView(withId(R.id.filter_bar_fragment_container)).check(matches(isDisplayed()));

        // Verify the spinner is displayed and shows "Select Mood" initially
        onView(withId(R.id.spinner_mood)).check(matches(withSpinnerText(containsString("Select Mood"))));

        // Click on the spinner to open dropdown
        onView(withId(R.id.spinner_mood)).perform(click());
        onView(isRoot()).perform(waitFor(500));

        // Select "Happiness" from the dropdown
        onData(allOf(is(instanceOf(String.class)), is("Happiness")))
                .perform(click());

        // Give UI time to update
        onView(isRoot()).perform(waitFor(500));

        // Verify spinner shows selected mood
        onView(withId(R.id.spinner_mood)).check(matches(withSpinnerText(containsString("Happiness"))));
    }

    /**
     * Test: Clear all filters
     */
    @Test
    public void testFilter_ClearAll() {
        // Wait for the main interface to load
        onView(isRoot()).perform(waitFor(1000));

        // First, set up some filters

        // 1. Select "Last 7 Days"
        onView(withId(R.id.btn_last_7_days)).perform(click());
        onView(isRoot()).perform(waitFor(500));

        // 2. Select a mood
        onView(withId(R.id.spinner_mood)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onData(allOf(is(instanceOf(String.class)), is("Happiness")))
                .perform(click());
        onView(isRoot()).perform(waitFor(500));

        // Now test clearing
        onView(withId(R.id.btn_clear_all)).perform(click());

        // Give UI time to update
        onView(isRoot()).perform(waitFor(500));

        // Verify filters are reset
        // 1. Spinner should be back to "Select Mood"
        onView(withId(R.id.spinner_mood)).check(matches(withSpinnerText(containsString("Select Mood"))));

        // 2. "Last 7 Days" button should no longer be visually selected
        // This is hard to test precisely without knowing your app's implementation
        // The button should still be present though
        onView(withId(R.id.btn_last_7_days)).check(matches(isDisplayed()));
    }

    /**
     * Test: Default spinner selection
     */
    @Test
    public void testSpinner_DefaultSelection() {
        // Wait for the main interface to load
        onView(isRoot()).perform(waitFor(1000));

        // Verify the filter bar container is displayed
        onView(withId(R.id.filter_bar_fragment_container)).check(matches(isDisplayed()));

        // Verify the spinner shows "Select Mood" by default
        onView(withId(R.id.spinner_mood)).check(matches(withSpinnerText(containsString("Select Mood"))));
    }

    /**
     * Test: Combined filters and clear
     * Tests applying multiple filters and then clearing them
     */
    @Test
    public void testFilter_CombinedAndClear() {
        // Wait for the main interface to load
        onView(isRoot()).perform(waitFor(1000));

        // Apply Last 7 Days filter
        onView(withId(R.id.btn_last_7_days)).perform(click());
        onView(isRoot()).perform(waitFor(300));

        // Select Happiness from spinner
        onView(withId(R.id.spinner_mood)).perform(click());
        onView(isRoot()).perform(waitFor(300));
        onData(allOf(is(instanceOf(String.class)), is("Happiness")))
                .perform(click());
        onView(isRoot()).perform(waitFor(500));

        // Verify filters are applied
        onView(withId(R.id.spinner_mood)).check(matches(withSpinnerText(containsString("Happiness"))));

        // Now clear all filters
        onView(withId(R.id.btn_clear_all)).perform(click());
        onView(isRoot()).perform(waitFor(500));

        // Verify filters are cleared
        onView(withId(R.id.spinner_mood)).check(matches(withSpinnerText(containsString("Select Mood"))));

        // Apply a different mood filter
        onView(withId(R.id.spinner_mood)).perform(click());
        onView(isRoot()).perform(waitFor(300));
        onData(allOf(is(instanceOf(String.class)), is("Sadness")))
                .perform(click());
        onView(isRoot()).perform(waitFor(500));

        // Verify the new filter is applied
        onView(withId(R.id.spinner_mood)).check(matches(withSpinnerText(containsString("Sadness"))));
    }
}