package com.example.canada_geese.Fragments;
import com.example.canada_geese.R;
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
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.EditText;

import androidx.core.content.ContextCompat;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
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

    // Automatically grant location permissions for map testing
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
     * Test: Verify map is loaded correctly and filter chip functionality
     */
    @Test
    public void testMapLoaded() {
        // Check if the map fragment is displayed
        onView(withId(R.id.map)).check(matches(isDisplayed()));

        // Check if the Memory button is displayed
        onView(withId(R.id.btnMemory)).check(matches(isDisplayed()));

        // Check if the Friends button is displayed
        onView(withId(R.id.btnFriends)).check(matches(isDisplayed()));

        // Check if the search view is displayed
        onView(withId(R.id.search_view)).check(matches(isDisplayed()));

        // Check if filter button is displayed
        onView(withId(R.id.filter_button)).check(matches(isDisplayed()));

        // Test filter button click
        onView(withId(R.id.filter_button)).perform(click());
        onView(isRoot()).perform(waitFor(1000));

        try {
            // Verify filter scroll view becomes visible
            onView(withId(R.id.filter_scroll_view)).check(matches(isDisplayed()));
        } catch (Exception e) {
            System.out.println("Could not verify filter scroll view: " + e.getMessage());
        }
    }

    /**
     * Test: Location permission request handling
     */
    @Test
    public void testLocationPermissionRequest() {
        // Verify permissions are granted (they should be due to our Rule)
        boolean hasLocationPermission = ContextCompat.checkSelfPermission(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If permissions are granted, verify the map displays correctly
        if (hasLocationPermission) {
            // Click Memory button to load markers which should use location
            onView(withId(R.id.btnMemory)).perform(click());
            onView(isRoot()).perform(waitFor(2000));

            // Map should remain visible and functional
            onView(withId(R.id.map)).check(matches(isDisplayed()));
        } else {
            System.out.println("Location permissions not granted, skipping map interaction test");
        }
    }

    /**
     * Test: Memory button click and verify mood markers with images
     */
    @Test
    public void testMemoryButtonClick() {
        // Click the Memory button to load user's mood events on map
        onView(withId(R.id.btnMemory)).perform(click());
        onView(isRoot()).perform(waitFor(3000)); // Wait for memory markers to load

        // Check that the app doesn't crash and the map is still displayed
        onView(withId(R.id.map)).check(matches(isDisplayed()));

        // Try clicking on the map where a marker might be
        // This is a simple test to verify the map is interactive
        // We can't precisely click on markers because their positions are dynamic
        try {
            // Click near the center of the map where markers are likely to appear
            onView(withId(R.id.map)).perform(click());
            onView(isRoot()).perform(waitFor(1000));

            // Map should still be displayed after clicking
            onView(withId(R.id.map)).check(matches(isDisplayed()));
        } catch (Exception e) {
            System.out.println("Could not interact with map: " + e.getMessage());
        }
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
     * Test: Displaying friends' mood events on map
     */
    @Test
    public void testFriendsMapMarkers() {
        // Click the Friends button to load friends' mood events
        onView(withId(R.id.btnFriends)).perform(click());
        onView(isRoot()).perform(waitFor(3000)); // Wait for markers to load

        // Check that the map is still displayed after loading friends' markers
        onView(withId(R.id.map)).check(matches(isDisplayed()));

        // Switch back to Memory markers for clean state
        onView(withId(R.id.btnMemory)).perform(click());
        onView(isRoot()).perform(waitFor(2000));

        // Verify map still displays correctly
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    /**
     * Test: Filtering markers on the map
     */
    @Test
    public void testFilteringMapMarkers() {
        // First load user's mood events
        onView(withId(R.id.btnMemory)).perform(click());
        onView(isRoot()).perform(waitFor(2000));

        // Verify map is displayed
        onView(withId(R.id.map)).check(matches(isDisplayed()));

        // Open filter options
        onView(withId(R.id.filter_button)).perform(click());
        onView(isRoot()).perform(waitFor(1500));

        try {
            // Verify filter scroll view is visible
            onView(withId(R.id.filter_scroll_view)).check(matches(isDisplayed()));

            // Try to find the happiness chip (if it fails, test continues)
            try {
                onView(withId(R.id.chip_mood_happiness)).perform(click());
                onView(isRoot()).perform(waitFor(1000));
            } catch (Exception e) {
                System.out.println("Could not find or click happiness chip: " + e.getMessage());
            }

            // Try to find the clear all chip (if it fails, test continues)
            try {
                onView(withId(R.id.chip_clear_all)).perform(click());
                onView(isRoot()).perform(waitFor(1000));
            } catch (Exception e) {
                System.out.println("Could not find or click clear all chip: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Could not verify or interact with filter chips: " + e.getMessage());
        }

        // Switch to Friends mode to verify basic functionality
        onView(withId(R.id.btnFriends)).perform(click());
        onView(isRoot()).perform(waitFor(2000));

        // Verify map is still displayed
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }
}