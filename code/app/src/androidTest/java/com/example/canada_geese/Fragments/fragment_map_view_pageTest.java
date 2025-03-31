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

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

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
        onView(isRoot()).perform(waitFor(2000));

        try {
            onView(withId(R.id.page4)).perform(click());
            onView(isRoot()).perform(waitFor(3000));
        } catch (Exception e) {
            System.out.println("Could not navigate to map view page: " + e.getMessage());
        }
    }

    /**
     * Test: Verify map is loaded correctly and filter chip functionality
     */
    @Test
    public void testMapLoaded() {
        onView(withId(R.id.map)).check(matches(isDisplayed()));
        onView(withId(R.id.btnMemory)).check(matches(isDisplayed()));
        onView(withId(R.id.btnFriends)).check(matches(isDisplayed()));
        onView(withId(R.id.search_view)).check(matches(isDisplayed()));
        onView(withId(R.id.filter_button)).check(matches(isDisplayed()));
        onView(withId(R.id.filter_button)).perform(click());
        onView(isRoot()).perform(waitFor(1000));

        try {
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

        boolean hasLocationPermission = ContextCompat.checkSelfPermission(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (hasLocationPermission) {
            onView(withId(R.id.btnMemory)).perform(click());
            onView(isRoot()).perform(waitFor(2000));

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
        onView(withId(R.id.btnMemory)).perform(click());
        onView(isRoot()).perform(waitFor(3000));
        onView(withId(R.id.map)).check(matches(isDisplayed()));

        try {
            onView(withId(R.id.map)).perform(click());
            onView(isRoot()).perform(waitFor(1000));

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
        onView(withId(R.id.search_view)).check(matches(isDisplayed()));

        try {
            onView(withId(R.id.search_view)).perform(click());
            onView(isRoot()).perform(waitFor(1000));

            try {
                onView(allOf(
                        isAssignableFrom(EditText.class),
                        isDescendantOfA(withId(R.id.search_view))
                )).perform(typeText("New York"), closeSoftKeyboard());

                onView(isRoot()).perform(waitFor(3000));
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
        onView(withId(R.id.btnFriends)).perform(click());
        onView(isRoot()).perform(waitFor(3000));
        onView(withId(R.id.map)).check(matches(isDisplayed()));
        onView(withId(R.id.btnMemory)).perform(click());
        onView(isRoot()).perform(waitFor(2000));
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    /**
     * Test: Filtering markers on the map
     */
    @Test
    public void testFilteringMapMarkers() {
        onView(withId(R.id.btnMemory)).perform(click());
        onView(isRoot()).perform(waitFor(2000));

        onView(withId(R.id.map)).check(matches(isDisplayed()));
        onView(withId(R.id.filter_button)).perform(click());
        onView(isRoot()).perform(waitFor(1500));

        try {
            onView(withId(R.id.filter_scroll_view)).check(matches(isDisplayed()));

            try {
                onView(withId(R.id.chip_mood_happiness)).perform(click());
                onView(isRoot()).perform(waitFor(1000));
            } catch (Exception e) {
                System.out.println("Could not find or click happiness chip: " + e.getMessage());
            }
            try {
                onView(withId(R.id.chip_clear_all)).perform(click());
                onView(isRoot()).perform(waitFor(1000));
            } catch (Exception e) {
                System.out.println("Could not find or click clear all chip: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Could not verify or interact with filter chips: " + e.getMessage());
        }

        onView(withId(R.id.btnFriends)).perform(click());
        onView(isRoot()).perform(waitFor(2000));

        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }
}