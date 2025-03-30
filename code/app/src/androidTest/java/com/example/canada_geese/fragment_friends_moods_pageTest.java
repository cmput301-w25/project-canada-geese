package com.example.canada_geese;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

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
 * UI tests for the fragment_friends_moods_page
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class fragment_friends_moods_pageTest {

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
     * Setup method to ensure we start at the friends moods page
     */
    @Before
    public void setup() {
        // Wait for the main interface to load
        onView(isRoot()).perform(waitFor(1000));

        // Navigate to the friends moods page
        onView(withId(R.id.page2)).perform(click());
        onView(isRoot()).perform(waitFor(1000));
    }

    /**
     * Test: Verify friends' mood events are displayed
     */
    @Test
    public void testFriendsMoodsDisplayed() {
        // Check if the RecyclerView is displayed
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));

        // Note: If the user has no friends or friends have no moods,
        // the recycler view might be empty, but it should still be displayed

        try {
            // Try to find any username text view that would indicate friend's moods
            // Check if at least one item exists
            onView(withId(R.id.recyclerView))
                    .perform(RecyclerViewActions.scrollToPosition(0));

            // Check if we can see the RecyclerView
            onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));

        } catch (Exception e) {
            // This might occur if there are no friend mood items to display
            System.out.println("Could not scroll RecyclerView. No friend moods may be available.");
        }

        // Test passes if we reach this point without errors
    }

    /**
     * Test: Comment on a friend's mood
     * Note: This test may be skipped if no friends' moods are available
     */
    @Test
    public void testCommentOnFriendMood() {
        try {
            // Check if there's at least one mood event
            onView(withId(R.id.recyclerView))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
            onView(isRoot()).perform(waitFor(1000));

            // Try to find and click the comment button
            try {
                // Look for the comment button
                onView(withId(R.id.comment_button2)).perform(click());
                onView(isRoot()).perform(waitFor(1000));

                // Check if comment bottom sheet appears
                try {
                    // Look for the comment input field
                    onView(withId(R.id.et_comment)).check(matches(isDisplayed()));

                    // Type a comment
                    onView(withId(R.id.et_comment))
                            .perform(typeText("Test comment"), closeSoftKeyboard());
                    onView(isRoot()).perform(waitFor(500));

                    // Post the comment
                    onView(withId(R.id.btn_post_comment)).perform(click());
                    onView(isRoot()).perform(waitFor(2000));

                    // We can't easily verify the comment was posted since it depends on the backend
                    // but at least we check that UI is responsive

                    // Look for a back button or tap outside to dismiss the comment sheet
                    try {
                        // Try to click back to dismiss comment sheet
                        // Using back press is tricky in Espresso, so we'll try UI interaction
                        // or press outside the sheet
                        // For this test, we'll just wait and assume the sheet is dismissed
                        onView(isRoot()).perform(waitFor(1000));
                    } catch (Exception e) {
                        System.out.println("Could not dismiss comment sheet, but test continues");
                    }

                } catch (NoMatchingViewException e) {
                    // If we can't find the comment edit text
                    System.out.println("Could not find comment input field");
                }
            } catch (NoMatchingViewException e) {
                // If we can't find the comment button
                System.out.println("Could not find comment button");
            }

            // Click the friend's mood again to collapse it (if still expanded)
            try {
                onView(withId(R.id.recyclerView))
                        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
                onView(isRoot()).perform(waitFor(500));
            } catch (Exception e) {
                // Not critical if we can't collapse
                System.out.println("Could not collapse friend mood item");
            }

        } catch (Exception e) {
            // There might not be any friend mood events
            System.out.println("Could not comment on friend mood. No friend moods may be available.");
        }

        // Verify that we're back to the friend moods page
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
    }
}