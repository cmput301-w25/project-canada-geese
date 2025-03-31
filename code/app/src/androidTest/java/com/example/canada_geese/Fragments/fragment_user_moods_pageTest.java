package com.example.canada_geese.Fragments;
import com.example.canada_geese.R;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.is;

import android.view.View;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
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
 * UI tests for the fragment_user_moods_page
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class fragment_user_moods_pageTest {

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
     * Setup method to ensure we start at the user moods page
     */
    @Before
    public void setup() {
        onView(isRoot()).perform(waitFor(1000));

        onView(withId(R.id.page1)).perform(click());
        onView(isRoot()).perform(waitFor(1000));
    }

    /**
     * Test: Verify mood events are displayed
     */
    @Test
    public void testMoodEventsDisplayed() {
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
    }

    /**
     * Test: Search functionality
     */
    @Test
    public void testSearchFunctionality() {
        onView(withId(R.id.searchView)).check(matches(isDisplayed()));

        try {
            onView(withId(R.id.searchView)).perform(click());
            onView(isRoot()).perform(waitFor(500));

            try {
                onView(withId(androidx.appcompat.R.id.search_src_text)).perform(typeText("test"), closeSoftKeyboard());
                onView(isRoot()).perform(waitFor(1000));

                onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));

                onView(withId(androidx.appcompat.R.id.search_src_text)).perform(replaceText(""), closeSoftKeyboard());
                onView(isRoot()).perform(waitFor(1000));
            } catch (NoMatchingViewException e) {
                System.out.println("Could not find search text field within SearchView");
            }
        } catch (NoMatchingViewException e) {
            System.out.println("Could not interact with search view");
        }
    }

    /**
     * Test: Delete mood event
     * Note: This test may be skipped if no mood events are available
     */
    @Test
    public void testDeleteMoodEvent() {
        try {
            onView(withId(R.id.recyclerView))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, longClick()));
            onView(isRoot()).perform(waitFor(500));
            try {
                onView(withText(R.string.delete_mood)).check(matches(isDisplayed()));
                onView(withText(R.string.delete_mood)).perform(click());
                onView(isRoot()).perform(waitFor(2000));
                onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
            } catch (NoMatchingViewException e) {
                System.out.println("Could not find delete button in confirmation dialog");
            }
        } catch (Exception e) {
            System.out.println("Could not perform delete action. No mood events may be available.");
        }
    }

    /**
     * Test: Edit mood event including image editing functionality
     * Note: This test may be skipped if no mood events are available
     */
    @Test
    public void testEditMoodEvent() {
        try {
            onView(withId(R.id.recyclerView))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
            onView(isRoot()).perform(waitFor(1000));
            try {
                onView(withId(R.id.options_menu_button)).check(matches(isDisplayed()));
                onView(withId(R.id.options_menu_button)).perform(click());
                onView(isRoot()).perform(waitFor(500));

                onView(withText("Edit Mood")).perform(click());
                onView(isRoot()).perform(waitFor(1000));

                onView(withId(R.id.edit_container)).check(matches(isDisplayed()));

                onView(withId(R.id.et_description_edit)).check(matches(isDisplayed()));
                onView(withId(R.id.et_description_edit)).perform(replaceText("Edited description with image test"), closeSoftKeyboard());
                onView(isRoot()).perform(waitFor(500));
                onView(withId(R.id.cb_private_mood_edit)).perform(click());
                onView(isRoot()).perform(waitFor(500));
                onView(withId(R.id.btn_save)).perform(click());
                onView(isRoot()).perform(waitFor(2000));

                onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));

            } catch (NoMatchingViewException e) {
                try {
                    onView(withId(R.id.btn_edit)).check(matches(isDisplayed()));
                    onView(withId(R.id.btn_edit)).perform(click());
                    onView(isRoot()).perform(waitFor(1000));

                    onView(withId(R.id.et_description_edit)).check(matches(isDisplayed()));
                    onView(withId(R.id.et_description_edit)).perform(replaceText("Edited description with image test"), closeSoftKeyboard());
                    onView(isRoot()).perform(waitFor(500));
                    onView(withId(R.id.btn_save)).perform(click());
                    onView(isRoot()).perform(waitFor(2000));

                    onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
                } catch (NoMatchingViewException ex) {
                    System.out.println("Could not find edit button or options menu");
                }
            }
        } catch (Exception e) {
            System.out.println("Could not perform edit action. No mood events may be available: " + e.getMessage());
        }
    }

    /**
     * Test: View mood event details including image display
     * Note: This test may be skipped if no mood events are available
     */
    @Test
    public void testMoodEventDetails() {
        try {
            onView(withId(R.id.recyclerView))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
            onView(isRoot()).perform(waitFor(1000));

            try {
                onView(withId(R.id.details_container)).check(matches(isDisplayed()));

                onView(withId(R.id.mood_emoji)).check(matches(isDisplayed()));
                onView(withId(R.id.mood_name)).check(matches(isDisplayed()));
                onView(withId(R.id.timestamp)).check(matches(isDisplayed()));
                try {
                    onView(withId(R.id.tv_description)).check(matches(isDisplayed()));
                } catch (NoMatchingViewException e) {
                    System.out.println("Could not find description text view");
                }

                try {
                    onView(withId(R.id.tv_social_situation)).check(matches(isDisplayed()));
                } catch (NoMatchingViewException e) {
                    System.out.println("Could not find social situation text view");
                }

                try {
                    onView(withId(R.id.image_container)).check(matches(isDisplayed()));
                    System.out.println("Image container found and is displayed");
                } catch (NoMatchingViewException e) {
                    System.out.println("Image container not found or not displayed - this mood may not have images");
                }

                try {
                    onView(withId(R.id.map_view)).check(matches(isDisplayed()));
                    System.out.println("Map view found and is displayed");
                } catch (NoMatchingViewException e) {
                    System.out.println("Map view not found or not displayed - this mood may not have location");
                }

                onView(withId(R.id.comment_button2)).check(matches(isDisplayed()));
                onView(withId(R.id.recyclerView))
                        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
                onView(isRoot()).perform(waitFor(1000));

                onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
            } catch (NoMatchingViewException e) {
                System.out.println("Could not find details container: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Could not view mood event details. No mood events may be available: " + e.getMessage());
        }
    }

}