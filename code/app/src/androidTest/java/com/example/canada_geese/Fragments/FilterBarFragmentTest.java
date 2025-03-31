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
        onView(isRoot()).perform(waitFor(1000));

        onView(withId(R.id.page1)).perform(click());
        onView(isRoot()).perform(waitFor(500));

        onView(withId(R.id.filter_button)).perform(click());
        onView(isRoot()).perform(waitFor(500));
    }

    /**
     * Test: Filter by last 7 days
     * Verifies the "Last 7 Days" chip can be clicked and shows visual feedback
     */
    @Test
    public void testFilter_Last7Days() {
        onView(withId(R.id.filter_bar_fragment_container)).check(matches(isDisplayed()));

        onView(withId(R.id.chip_last_7_days)).perform(ViewActions.scrollTo(), click());

        onView(isRoot()).perform(waitFor(500));

        onView(withId(R.id.chip_last_7_days)).check(matches(isChecked()));
    }

    /**
     * Test: Filter by mood selection - Happiness
     */
    @Test
    public void testFilter_SelectMood() {
        onView(withId(R.id.filter_bar_fragment_container)).check(matches(isDisplayed()));
        onView(withId(R.id.chip_mood_happiness)).perform(ViewActions.scrollTo(), click());

        onView(isRoot()).perform(waitFor(500));

        onView(withId(R.id.chip_mood_happiness)).check(matches(isChecked()));
    }

    /**
     * Test: Clear all filters
     */
    @Test
    public void testFilter_ClearAll() {

        onView(withId(R.id.chip_last_7_days)).perform(ViewActions.scrollTo(), click());
        onView(isRoot()).perform(waitFor(300));
        onView(withId(R.id.chip_mood_happiness)).perform(ViewActions.scrollTo(), click());
        onView(isRoot()).perform(waitFor(300));
        onView(withId(R.id.chip_last_7_days)).check(matches(isChecked()));
        onView(withId(R.id.chip_mood_happiness)).check(matches(isChecked()));
        onView(withId(R.id.chip_clear_all)).perform(ViewActions.scrollTo(), click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.chip_last_7_days)).check(matches(isNotChecked()));
        onView(withId(R.id.chip_mood_happiness)).check(matches(isNotChecked()));
    }

    /**
     * Test: Default chip selection state
     */
    @Test
    public void testSpinner_DefaultSelection() {
        onView(withId(R.id.filter_bar_fragment_container)).check(matches(isDisplayed()));
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
        onView(withId(R.id.chip_last_7_days)).perform(ViewActions.scrollTo(), click());
        onView(isRoot()).perform(waitFor(300));

        onView(withId(R.id.chip_mood_happiness)).perform(ViewActions.scrollTo(), click());
        onView(isRoot()).perform(waitFor(300));
        onView(withId(R.id.chip_last_7_days)).check(matches(isChecked()));
        onView(withId(R.id.chip_mood_happiness)).check(matches(isChecked()));
        onView(withId(R.id.chip_clear_all)).perform(ViewActions.scrollTo(), click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.chip_last_7_days)).check(matches(isNotChecked()));
        onView(withId(R.id.chip_mood_happiness)).check(matches(isNotChecked()));
        onView(withId(R.id.chip_mood_sadness)).perform(ViewActions.scrollTo(), click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.chip_mood_sadness)).check(matches(isChecked()));
    }

    /**
     * Test: Private mood filter chip
     * Tests the private mood filter functionality
     */
    @Test
    public void testPrivateFilterChip() {
        onView(withId(R.id.filter_bar_fragment_container)).check(matches(isDisplayed()));
        onView(withText("Private")).perform(ViewActions.scrollTo());
        onView(withId(R.id.chip_mood_private)).check(matches(isDisplayed()));
        onView(withId(R.id.chip_mood_private)).check(matches(isNotChecked()));
        onView(withId(R.id.chip_mood_private)).perform(click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.chip_mood_private)).check(matches(isChecked()));
        onView(withId(R.id.chip_mood_happiness)).perform(ViewActions.scrollTo(), click());
        onView(isRoot()).perform(waitFor(300));
        onView(withId(R.id.chip_mood_private)).perform(ViewActions.scrollTo());
        onView(withId(R.id.chip_mood_private)).check(matches(isChecked()));

        onView(withId(R.id.chip_mood_happiness)).perform(ViewActions.scrollTo());
        onView(withId(R.id.chip_mood_happiness)).check(matches(isChecked()));
        onView(withId(R.id.chip_clear_all)).perform(ViewActions.scrollTo(), click());
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.chip_mood_private)).perform(ViewActions.scrollTo());
        onView(withId(R.id.chip_mood_private)).check(matches(isNotChecked()));
    }
}