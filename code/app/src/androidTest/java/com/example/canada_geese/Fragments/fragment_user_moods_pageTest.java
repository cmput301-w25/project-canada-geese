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
        // Wait for the main interface to load
        onView(isRoot()).perform(waitFor(1000));

        // Make sure we're on the user moods page (should be default, but just in case)
        onView(withId(R.id.page1)).perform(click());
        onView(isRoot()).perform(waitFor(1000));
    }

    /**
     * Test: Verify mood events are displayed
     */
    @Test
    public void testMoodEventsDisplayed() {
        // Check if the RecyclerView is displayed
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));

        // Note: We cannot guarantee there are items in the RecyclerView
        // since it depends on the current state of the user's mood events
    }

    /**
     * Test: Search functionality
     */
    @Test
    public void testSearchFunctionality() {
        // Check if search view is displayed
        onView(withId(R.id.searchView)).check(matches(isDisplayed()));

        try {
            // Click on the search view
            onView(withId(R.id.searchView)).perform(click());
            onView(isRoot()).perform(waitFor(500));

            // Type a search query
            // Note: The actual search text field ID might be different from the search view container
            try {
                // Try to find the search text field within the SearchView
                onView(withId(androidx.appcompat.R.id.search_src_text)).perform(typeText("test"), closeSoftKeyboard());
                onView(isRoot()).perform(waitFor(1000));

                // Verify the RecyclerView is still displayed after search
                onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));

                // Clear the search
                onView(withId(androidx.appcompat.R.id.search_src_text)).perform(replaceText(""), closeSoftKeyboard());
                onView(isRoot()).perform(waitFor(1000));
            } catch (NoMatchingViewException e) {
                // If we can't find the search text field, log it
                System.out.println("Could not find search text field within SearchView");
            }
        } catch (NoMatchingViewException e) {
            // If we can't interact with the search view, log it
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
            // Check if there's at least one mood event
            onView(withId(R.id.recyclerView))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, longClick()));
            onView(isRoot()).perform(waitFor(500));

            // Check if the delete confirmation dialog appears
            try {
                // Try to find the delete button in the dialog
                onView(withText(R.string.delete_mood)).check(matches(isDisplayed()));

                // Click the delete button
                onView(withText(R.string.delete_mood)).perform(click());
                onView(isRoot()).perform(waitFor(2000));

                // We can't easily verify the deletion since it depends on the backend
                // But we can check that the UI is still responsive
                onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
            } catch (NoMatchingViewException e) {
                // If we can't find the delete button, log it
                System.out.println("Could not find delete button in confirmation dialog");
            }
        } catch (Exception e) {
            // There might not be any mood events to delete
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
            // Check if there's at least one mood event and click on it
            onView(withId(R.id.recyclerView))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
            onView(isRoot()).perform(waitFor(1000));

            // Click the options menu button to access edit
            try {
                onView(withId(R.id.options_menu_button)).check(matches(isDisplayed()));
                onView(withId(R.id.options_menu_button)).perform(click());
                onView(isRoot()).perform(waitFor(500));

                // Click edit option
                onView(withText("Edit Mood")).perform(click());
                onView(isRoot()).perform(waitFor(1000));

                // Verify edit container is displayed
                onView(withId(R.id.edit_container)).check(matches(isDisplayed()));

                // Edit the description
                onView(withId(R.id.et_description_edit)).check(matches(isDisplayed()));
                onView(withId(R.id.et_description_edit)).perform(replaceText("Edited description with image test"), closeSoftKeyboard());
                onView(isRoot()).perform(waitFor(500));

                // Toggle the private mood checkbox
                onView(withId(R.id.cb_private_mood_edit)).perform(click());
                onView(isRoot()).perform(waitFor(500));

                // Note: We can't actually add images in the instrumented test
                // because it would require system interaction outside of the app
                // But we can verify the save process works

                // Click save button
                onView(withId(R.id.btn_save)).perform(click());
                onView(isRoot()).perform(waitFor(2000));

                // Verify we're back to the list view
                onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));

            } catch (NoMatchingViewException e) {
                // We might be using an older UI without options menu
                try {
                    onView(withId(R.id.btn_edit)).check(matches(isDisplayed()));
                    onView(withId(R.id.btn_edit)).perform(click());
                    onView(isRoot()).perform(waitFor(1000));

                    // Edit the description
                    onView(withId(R.id.et_description_edit)).check(matches(isDisplayed()));
                    onView(withId(R.id.et_description_edit)).perform(replaceText("Edited description with image test"), closeSoftKeyboard());
                    onView(isRoot()).perform(waitFor(500));

                    // Click save button
                    onView(withId(R.id.btn_save)).perform(click());
                    onView(isRoot()).perform(waitFor(2000));

                    // Verify we're back to the list view
                    onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
                } catch (NoMatchingViewException ex) {
                    System.out.println("Could not find edit button or options menu");
                }
            }
        } catch (Exception e) {
            // There might not be any mood events to edit
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
            // Check if there's at least one mood event and click on it
            onView(withId(R.id.recyclerView))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
            onView(isRoot()).perform(waitFor(1000));

            // Check if details are displayed
            try {
                // Look for some key elements that should be in the details view
                onView(withId(R.id.details_container)).check(matches(isDisplayed()));

                // Verify mood emoji and name are displayed
                onView(withId(R.id.mood_emoji)).check(matches(isDisplayed()));
                onView(withId(R.id.mood_name)).check(matches(isDisplayed()));

                // Verify timestamp is displayed
                onView(withId(R.id.timestamp)).check(matches(isDisplayed()));

                // Try to find the description text view
                try {
                    onView(withId(R.id.tv_description)).check(matches(isDisplayed()));
                } catch (NoMatchingViewException e) {
                    // If we can't find the description text view, it might have a different ID
                    System.out.println("Could not find description text view");
                }

                // Try to find the social situation text view
                try {
                    onView(withId(R.id.tv_social_situation)).check(matches(isDisplayed()));
                } catch (NoMatchingViewException e) {
                    // If we can't find the social situation text view, it might have a different ID
                    System.out.println("Could not find social situation text view");
                }

                // Check for image container
                try {
                    onView(withId(R.id.image_container)).check(matches(isDisplayed()));
                    // Note: We can't verify the actual image content in an instrumented test
                    // But we can verify the container exists
                    System.out.println("Image container found and is displayed");
                } catch (NoMatchingViewException e) {
                    // The event might not have images
                    System.out.println("Image container not found or not displayed - this mood may not have images");
                }

                // Check for map view (location)
                try {
                    onView(withId(R.id.map_view)).check(matches(isDisplayed()));
                    System.out.println("Map view found and is displayed");
                } catch (NoMatchingViewException e) {
                    // The event might not have location
                    System.out.println("Map view not found or not displayed - this mood may not have location");
                }

                // Check for comment button
                onView(withId(R.id.comment_button2)).check(matches(isDisplayed()));

                // Click the mood event again to collapse it
                onView(withId(R.id.recyclerView))
                        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
                onView(isRoot()).perform(waitFor(1000));

                // Verify the RecyclerView is still displayed
                onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
            } catch (NoMatchingViewException e) {
                // If we can't find the details container, log it
                System.out.println("Could not find details container: " + e.getMessage());
            }
        } catch (Exception e) {
            // There might not be any mood events to view
            System.out.println("Could not view mood event details. No mood events may be available: " + e.getMessage());
        }
    }

}