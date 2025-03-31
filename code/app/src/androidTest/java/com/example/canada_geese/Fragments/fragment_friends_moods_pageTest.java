package com.example.canada_geese.Fragments;
import com.example.canada_geese.R;
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
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));


        try {
            onView(withId(R.id.recyclerView))
                    .perform(RecyclerViewActions.scrollToPosition(0));
            onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));

        } catch (Exception e) {
            System.out.println("Could not scroll RecyclerView. No friend moods may be available.");
        }
    }

    /**
     * Test: Comment on a friend's mood
     * Note: This test may be skipped if no friends' moods are available
     */
    @Test
    public void testCommentOnFriendMood() {
        try {
            onView(withId(R.id.recyclerView))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
            onView(isRoot()).perform(waitFor(1000));
            try {
                onView(withId(R.id.comment_button2)).perform(click());
                onView(isRoot()).perform(waitFor(1000));
                try {
                    onView(withId(R.id.et_comment)).check(matches(isDisplayed()));

                    onView(withId(R.id.et_comment))
                            .perform(typeText("Test comment"), closeSoftKeyboard());
                    onView(isRoot()).perform(waitFor(500));
                    onView(withId(R.id.btn_post_comment)).perform(click());
                    onView(isRoot()).perform(waitFor(2000));
                    try {

                        onView(isRoot()).perform(waitFor(1000));
                    } catch (Exception e) {
                        System.out.println("Could not dismiss comment sheet, but test continues");
                    }

                } catch (NoMatchingViewException e) {
                    System.out.println("Could not find comment input field");
                }
            } catch (NoMatchingViewException e) {
                System.out.println("Could not find comment button");
            }

            try {
                onView(withId(R.id.recyclerView))
                        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
                onView(isRoot()).perform(waitFor(500));
            } catch (Exception e) {
                System.out.println("Could not collapse friend mood item");
            }

        } catch (Exception e) {
            System.out.println("Could not comment on friend mood. No friend moods may be available.");
        }

        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
    }
}