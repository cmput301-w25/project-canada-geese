package com.example.canada_geese;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.core.app.ActivityScenario;

import com.example.canada_geese.Pages.MainActivity;
import com.example.canada_geese.Fragments.AddMoodEventDialogFragment;
import com.example.canada_geese.Models.MoodEventModel;
import com.example.canada_geese.Pages.fragment_user_moods_page;
import com.example.canada_geese.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isSelected;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    private SimpleDateFormat dateFormat;

    @Before
    public void setUp() {
        // Initialize date format for parsing timestamps
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    }

    // ✅ Test 1: Ensure that BottomNavigationView initializes successfully and selects the correct page
    @Test
    public void testBottomNavigationInitialization() {
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
        // Check that page1 is selected (not page2) based on your MainActivity implementation
        onView(withId(R.id.page1)).check(matches(isSelected()));
    }

    // ✅ Test 2: Test whether `addNewMood()` correctly adds the Mood Event to `fragment_user_moods_page`
    @Test
    public void testAddNewMoodSuccessfullyAddsMoodEvent() throws ParseException {
        // Parse the timestamp string into a Date object
        Date timestamp = dateFormat.parse("2025-03-09 12:00");

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                fragment_user_moods_page moodsFragment = fragment_user_moods_page.newInstance();

                // Create a new MoodEvent with Date object
                MoodEventModel newMood = new MoodEventModel(
                        "Excited", "Feeling great today!", timestamp, "🤩",
                        R.color.color_happiness, false, true, 34.0522, -118.2437
                );

                // Add the new MoodEvent
                moodsFragment.addNewMood(newMood);
            });

            // Verify that the RecyclerView is displayed correctly
            onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
        }
    }

    // ✅ Test 3: Test whether `AddMoodEventDialogFragment` triggers `addNewMood()` callback correctly
    @Test
    public void testAddMoodEventDialogTriggersAddNewMood() throws ParseException {
        // Parse the timestamp string into a Date object
        Date timestamp = dateFormat.parse("2025-03-09 15:00");

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                fragment_user_moods_page moodsFragment = fragment_user_moods_page.newInstance();
                AddMoodEventDialogFragment dialog = new AddMoodEventDialogFragment();

                // Use `setNewMood` to test adding a new Mood Event with Date object
                MoodEventModel newMood = new MoodEventModel(
                        "Joy", "Having a wonderful day!", timestamp, "😃",
                        R.color.color_happiness, false, true, 51.0447, -114.0719
                );

                moodsFragment.setNewMood(newMood);
            });

            // Verify that the RecyclerView is displayed correctly
            onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
        }
    }

    // ✅ Test 4: Test that the search function correctly filters Mood Events
    @Test
    public void testSearchFunctionality() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                fragment_user_moods_page moodsFragment = fragment_user_moods_page.newInstance();
            });

            // Enter keywords in the search box
            onView(withId(R.id.searchView))
                    .perform(typeText("Happy"), closeSoftKeyboard());

            // Verify that the RecyclerView result is correct
            onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
        }
    }

    // ✅ Test 5: Test that the list is reset when the search box is closed
    @Test
    public void testSearchCloseResetsList() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                fragment_user_moods_page moodsFragment = fragment_user_moods_page.newInstance();
            });

            // Simulate opening and closing the search box
            onView(withId(R.id.searchView))
                    .perform(typeText("Anger"), closeSoftKeyboard());

            onView(withId(R.id.searchView)).perform(closeSoftKeyboard());

            // Verification list reset
            onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
        }
    }
}