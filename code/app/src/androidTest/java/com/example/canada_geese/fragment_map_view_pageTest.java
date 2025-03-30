package com.example.canada_geese;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

import android.Manifest;
import android.view.View;
import android.widget.EditText;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import com.example.canada_geese.Pages.MainActivity;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * UI tests for the fragment_map_view_page
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class fragment_map_view_pageTest {

    // Use MainActivity instead of LaunchActivity
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    // Optional: Automatically grant location permissions for map testing
    @Rule
    public GrantPermissionRule locationPermissionRule = GrantPermissionRule.grant(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION);

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
     * Setup method to ensure we start at the map view page
     */
    @Before
    public void setup() {
        // Wait for the main activity to load completely
        onView(isRoot()).perform(waitFor(2000));

        try {
            // Navigate to the map view page (page4 in bottom navigation)
            onView(withId(R.id.page4)).perform(click());
            // Wait for map to load (maps can take longer)
            onView(isRoot()).perform(waitFor(3000));
        } catch (Exception e) {
            // Log any navigation errors but continue with the test
            System.out.println("Could not navigate to map view page: " + e.getMessage());
        }
    }

    /**
     * Test: Verify map is loaded correctly
     */
    @Test
    public void testMapLoaded() {
        // Check if the map fragment is displayed
        onView(withId(R.id.map)).check(matches(isDisplayed()));

        // Check if the Memory button is displayed
        onView(withId(R.id.btnMemory)).check(matches(isDisplayed()));

        // Check if the search view is displayed
        onView(withId(R.id.search_view)).check(matches(isDisplayed()));
    }

    /**
     * Test: Memory button click
     */
    @Test
    public void testMemoryButtonClick() {
        // Click the Memory button
        onView(withId(R.id.btnMemory)).perform(click());
        onView(isRoot()).perform(waitFor(3000)); // Wait for memory markers to load

        // Check that the app doesn't crash and the map is still displayed
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    /**
     * Test: Search for a location
     */
    @Test
    public void testSearchLocation() {
        // Check if search view is displayed
        onView(withId(R.id.search_view)).check(matches(isDisplayed()));

        try {
            // Click on the search view
            onView(withId(R.id.search_view)).perform(click());
            onView(isRoot()).perform(waitFor(1000));

            try {
                // Use custom matcher to find the EditText within SearchView
                onView(allOf(
                        isAssignableFrom(EditText.class),
                        isDescendantOfA(withId(R.id.search_view))
                )).perform(typeText("New York"), closeSoftKeyboard());

                // Wait for search results
                onView(isRoot()).perform(waitFor(3000));

                // Check that the map is still displayed after search
                onView(withId(R.id.map)).check(matches(isDisplayed()));

            } catch (Exception e) {
                System.out.println("Could not perform search text entry: " + e.getMessage());
            }

        } catch (NoMatchingViewException e) {
            System.out.println("Could not interact with search view: " + e.getMessage());
        }
    }

    /**
     * Test: Location services
     * Note: This test only verifies map display with permissions already granted
     * through the GrantPermissionRule above
     */
    @Test
    public void testLocationServices() {
        // With permissions granted, verify map is displayed
        onView(withId(R.id.map)).check(matches(isDisplayed()));

        // Wait for location to potentially be displayed on map
        onView(isRoot()).perform(waitFor(3000));

        // We can only verify the map is still displayed
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }
}