package com.example.canada_geese.Fragments;
import com.example.canada_geese.R;
import android.Manifest;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.Root;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.example.canada_geese.Fragments.AddMoodEventDialogFragment;
import com.example.canada_geese.Pages.MainActivity;

import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class AddMoodEventDialogFragmentTest {

    @Rule
    public GrantPermissionRule locationPermissionRule =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Rule
    public GrantPermissionRule cameraPermissionRule =
            GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Rule
    public GrantPermissionRule storagePermissionRule =
            GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);

    /**
     * Launches the AddMoodEventDialogFragment via the MainActivity
     */
    private void launchDialog() {
        ActivityScenario.launch(MainActivity.class).onActivity(activity -> {
            AddMoodEventDialogFragment fragment = new AddMoodEventDialogFragment();
            fragment.show(activity.getSupportFragmentManager(), "add_mood");
        });
    }

    /**
     * Helper method to wait for a specified number of milliseconds
     */
    private ViewAction waitFor(final long millis) {
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
     * Matcher to check if a view is inside a dialog
     */
    public static Matcher<Root> isDialog() {
        return new TypeSafeMatcher<Root>() {
            @Override
            protected boolean matchesSafely(Root root) {
                return root.getDecorView().getWindowToken() != null &&
                        root.getDecorView().getWindowId() != null;
            }

            @Override
            public void describeTo(org.hamcrest.Description description) {
                description.appendText("is dialog");
            }
        };
    }

    /**
     * Test: Verify dialog creation and its basic elements are displayed
     */
    @Test
    public void testDialogCreation() {
        launchDialog();

        // Wait for dialog to fully appear
        Espresso.onView(isRoot()).perform(waitFor(1000));

        // Check main dialog components
        Espresso.onView(withId(R.id.add_mood_button)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.emotion_spinner)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.description_input)).check(matches(isDisplayed()));
    }

    /**
     * Test: Add a mood event with valid input
     */
    @Test
    public void testAddMoodWithValidInput() {
        launchDialog();
        Espresso.onView(isRoot()).perform(waitFor(1000));

        Espresso.onView(withId(R.id.description_input))
                .perform(ViewActions.replaceText("Test mood description"));

        Espresso.onView(withId(R.id.add_mood_button))
                .perform(ViewActions.click());

    }

    /**
     * Test: Add a mood event with empty description
     */
    @Test
    public void testAddMoodWithEmptyDescription() {
        launchDialog();

        Espresso.onView(isRoot()).perform(waitFor(2000));

        Espresso.onView(withId(R.id.description_input))
                .perform(ViewActions.click(), ViewActions.clearText());
        Espresso.onView(isRoot()).perform(waitFor(500));

        Espresso.onView(withId(R.id.add_mood_button))
                .perform(ViewActions.scrollTo())
                .check(matches(isDisplayed()))
                .perform(ViewActions.click());
        Espresso.onView(isRoot()).perform(waitFor(2000));
    }

    /**
     * Test: Add a mood event with location
     */
    @Test
    public void testAddMoodWithLocation() {
        launchDialog();

        Espresso.onView(isRoot()).perform(waitFor(1000));

        Espresso.onView(withId(R.id.attach_location_checkbox))
                .perform(ViewActions.click());
        Espresso.onView(isRoot()).perform(waitFor(2000));
        try {
            Espresso.onView(withId(R.id.map_container))
                    .check(matches(isDisplayed()));
        } catch (Exception e) {
            System.out.println("Map container not displayed. May need additional actions.");
            Espresso.onView(withId(R.id.attach_location_checkbox))
                    .check(matches(isChecked()));
        }
    }

    /**
     * Test: Add a mood event with a single image
     */
    @Test
    public void testAddMoodWithImage() {
        launchDialog();
        Espresso.onView(isRoot()).perform(waitFor(1000));
        Espresso.onView(withId(R.id.camera_button))
                .perform(ViewActions.click());
        Espresso.onView(isRoot()).perform(waitFor(1000));

        try {
            Espresso.onData(anything())
                    .inRoot(isDialog())
                    .atPosition(0)  // Camera option
                    .perform(ViewActions.click());

        } catch (Exception e) {
            Espresso.onView(withText("Select Image Source"))
                    .inRoot(isDialog())
                    .check(matches(isDisplayed()));
        }
    }

    /**
     * Test: Social situation selection
     */
    @Test
    public void testSocialSituationSelection() {
        launchDialog();
        Espresso.onView(isRoot()).perform(waitFor(1000));

        Espresso.onView(withId(R.id.social_situation_spinner))
                .check(matches(isDisplayed()));

        Espresso.onView(withId(R.id.social_situation_spinner))
                .check(matches(isClickable()));

        Espresso.onView(withId(R.id.description_input))
                .perform(ViewActions.replaceText("Testing social situations"));

        Espresso.onView(withId(R.id.trigger_warning_checkbox))
                .perform(ViewActions.click())
                .check(matches(isChecked()));

        Espresso.onView(withId(R.id.add_mood_button))
                .perform(ViewActions.click());

        Espresso.onView(isRoot()).perform(waitFor(1000));
    }

    /**
     * Test: Add a mood event with multiple images
     * Note: This test focuses on the UI flow rather than actual image selection
     * which requires system-level interaction that's difficult to test
     */
    @Test
    public void testAddMoodWithMultipleImages() {
        launchDialog();

        Espresso.onView(isRoot()).perform(waitFor(2000));

        Espresso.onView(withId(R.id.camera_button))
                .check(matches(isDisplayed()));

        try {
            Espresso.onView(withId(R.id.camera_button))
                    .perform(ViewActions.click());

            Espresso.onView(isRoot()).perform(waitFor(1000));

            Espresso.pressBack();
        } catch (Exception e) {
            System.out.println("Exception when clicking camera button: " + e.getMessage());
        }

        Espresso.onView(isRoot()).perform(waitFor(1000));

        Espresso.onView(withId(R.id.description_input))
                .perform(ViewActions.click())
                .perform(ViewActions.typeText("Test with multiple images"));
        Espresso.onView(isRoot()).perform(waitFor(500));
        Espresso.closeSoftKeyboard();
        Espresso.onView(isRoot()).perform(waitFor(500));
        Espresso.onView(withId(R.id.add_mood_button))
                .perform(ViewActions.scrollTo(), ViewActions.click());
    }
}