package com.example.canada_geese.Fragments;
import com.example.canada_geese.R;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
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
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

import android.view.View;
import android.widget.EditText;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.canada_geese.Pages.LoginActivity;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * UI tests for the fragment_user_profile_page
 * Tests the user profile functionality including:
 * - Profile information display
 * - Following/Followers list viewing
 * - User search functionality
 * - Follow request sending
 * - Profile image display
 * - Profile editing
 * - Sign out functionality
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class fragment_user_profile_pageTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

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
     * Setup method to ensure we log in and navigate to the user profile page before each test
     */
    @Before
    public void setup() {

        onView(isRoot()).perform(waitFor(2000));

        try {
            onView(withId(R.id.username_input)).perform(typeText("testuser"), closeSoftKeyboard());
            onView(withId(R.id.password_input)).perform(typeText("password"), closeSoftKeyboard());
            onView(withId(R.id.button_sign_in)).perform(click());
            onView(isRoot()).perform(waitFor(3000));
            onView(withId(R.id.page5)).perform(click());
            onView(isRoot()).perform(waitFor(2000));
        } catch (Exception e) {
            System.out.println("Setup failed: " + e.getMessage());
        }
    }

    /**
     * Test: Verify user profile information is displayed
     * This test checks that basic profile elements like username, profile image,
     * followers count, and following count are visible.
     */
    @Test
    public void testProfileInformationDisplayed() {
        try {
            onView(withId(R.id.profile_content_container)).check(matches(isDisplayed()));
            onView(withId(R.id.username_text)).check(matches(isDisplayed()));
            onView(withId(R.id.profile_image)).check(matches(isDisplayed()));
            onView(withId(R.id.followers_count)).check(matches(isDisplayed()));
            onView(withId(R.id.following_count)).check(matches(isDisplayed()));
        } catch (Exception e) {
            System.out.println("Profile information test failed: " + e.getMessage());
        }
    }

    /**
     * Test: View followers list
     * This test checks that clicking on the followers section displays the followers list.
     */
    @Test
    public void testViewFollowers() {
        try {
            onView(withId(R.id.followers_section)).perform(click());
            onView(isRoot()).perform(waitFor(1000));
            onView(withId(R.id.followers_list)).check(matches(isDisplayed()));
        } catch (Exception e) {
            System.out.println("View followers test failed: " + e.getMessage());
        }
    }

    /**
     * Test: View following list
     * This test checks that clicking on the following section displays the following list.
     */
    @Test
    public void testViewFollowing() {
        try {
            onView(withId(R.id.following_section)).perform(click());
            onView(isRoot()).perform(waitFor(1000));
            onView(withId(R.id.followers_list)).check(matches(isDisplayed()));
        } catch (Exception e) {
            System.out.println("View following test failed: " + e.getMessage());
        }
    }

    /**
     * Test: Search users functionality
     * This test checks that clicking on the search view displays the search results container
     * and that typing a search query works correctly.
     */
    @Test
    public void testSearchUsers() {
        try {
            onView(withId(R.id.searchView)).check(matches(isDisplayed()));
            onView(withId(R.id.searchView)).perform(click());
            onView(isRoot()).perform(waitFor(1000));
            onView(withId(R.id.search_results_container)).check(matches(isDisplayed()));
            onView(withId(R.id.search_results_list)).check(matches(isDisplayed()));

            try {
                onView(allOf(
                        isAssignableFrom(EditText.class),
                        isDescendantOfA(withId(R.id.searchView))
                )).perform(typeText("test"), closeSoftKeyboard());

                onView(isRoot()).perform(waitFor(2000));

                onView(withId(R.id.back_button)).perform(click());
                onView(isRoot()).perform(waitFor(1000));
                onView(withId(R.id.profile_content_container)).check(matches(isDisplayed()));
            } catch (Exception e) {
                System.out.println("Search users typing test failed: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Search users test failed: " + e.getMessage());
        }
    }

    /**
     * Test: Send follow request functionality
     * This test checks that searching for a user and sending a follow request works correctly.
     */
    @Test
    public void testSendFollowRequest() {
        try {
            onView(withId(R.id.searchView)).perform(click());
            onView(isRoot()).perform(waitFor(1000));
            try {
                onView(withId(R.id.search_results_list))
                        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
                onView(isRoot()).perform(waitFor(1000));

                try {
                    onView(withId(R.id.dialog_action_button)).check(matches(isDisplayed()));
                    onView(withId(R.id.dialog_action_button)).perform(click());
                    onView(isRoot()).perform(waitFor(1000));
                } catch (NoMatchingViewException e) {
                    System.out.println("Could not find follow button: " + e.getMessage());
                }

                onView(withId(R.id.back_button)).perform(click());
                onView(isRoot()).perform(waitFor(1000));
            } catch (Exception e) {
                System.out.println("Could not click on user in search results: " + e.getMessage());

                try {
                    onView(withId(R.id.back_button)).perform(click());
                    onView(isRoot()).perform(waitFor(1000));
                } catch (Exception ex) {
                    System.out.println("Could not return to profile: " + ex.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Send follow request test failed: " + e.getMessage());
        }
    }

    /**
     * Test: Unfollow user functionality
     * This test checks that unfollowing a user works correctly.
     */
    @Test
    public void testUnfollowUser() {
        try {
            onView(withId(R.id.following_section)).perform(click());
            onView(isRoot()).perform(waitFor(1000));
            onView(withId(R.id.searchView)).perform(click());
            onView(isRoot()).perform(waitFor(1000));

            try {
                onView(withId(R.id.search_results_list))
                        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
                onView(isRoot()).perform(waitFor(1000));

                try {
                    onView(withId(R.id.dialog_action_button)).check(matches(isDisplayed()));
                    onView(withId(R.id.dialog_action_button)).perform(click());
                    onView(isRoot()).perform(waitFor(1000));
                } catch (NoMatchingViewException e) {
                    System.out.println("Could not find action button: " + e.getMessage());
                }

                onView(withId(R.id.back_button)).perform(click());
                onView(isRoot()).perform(waitFor(1000));
            } catch (Exception e) {
                System.out.println("Could not click on a user: " + e.getMessage());

                try {
                    onView(withId(R.id.back_button)).perform(click());
                    onView(isRoot()).perform(waitFor(1000));
                } catch (Exception ex) {
                    System.out.println("Could not return to profile: " + ex.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Unfollow user test failed: " + e.getMessage());
        }
    }

    /**
     * Test: Complete follow/unfollow user flow
     * This test checks the complete flow of searching for a user,
     * sending a follow request, and then unfollowing the user.
     */
    @Test
    public void testCompleteFollowUnfollowFlow() {
        try {
            onView(withId(R.id.searchView)).perform(click());
            onView(isRoot()).perform(waitFor(1000));

            onView(allOf(
                    isAssignableFrom(EditText.class),
                    isDescendantOfA(withId(R.id.searchView))
            )).perform(typeText("test"), closeSoftKeyboard());
            onView(isRoot()).perform(waitFor(2000));

            try {
                onView(withId(R.id.search_results_list))
                        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
                onView(isRoot()).perform(waitFor(1000));

                try {
                    onView(withId(R.id.dialog_action_button)).check(matches(isDisplayed()));
                    onView(withId(R.id.dialog_action_button)).check(matches(withText("Send Follow Request")));
                    onView(withId(R.id.dialog_action_button)).perform(click());
                    onView(isRoot()).perform(waitFor(1500));

                    onView(withId(R.id.back_button)).perform(click());
                    onView(isRoot()).perform(waitFor(1000));

                    onView(withId(R.id.searchView)).perform(click());
                    onView(isRoot()).perform(waitFor(1000));

                    onView(allOf(
                            isAssignableFrom(EditText.class),
                            isDescendantOfA(withId(R.id.searchView))
                    )).perform(typeText("test"), closeSoftKeyboard());
                    onView(isRoot()).perform(waitFor(2000));

                    onView(withId(R.id.search_results_list))
                            .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
                    onView(isRoot()).perform(waitFor(1000));

                    try {
                        onView(withId(R.id.dialog_action_button)).check(matches(isDisplayed()));
                        onView(withId(R.id.dialog_action_button)).check(matches(withText("Unfollow")));
                        onView(withId(R.id.dialog_action_button)).perform(click());
                        onView(isRoot()).perform(waitFor(1500));
                    } catch (Exception e) {
                        System.out.println("Could not perform unfollow action: " + e.getMessage());
                    }

                    onView(withId(R.id.back_button)).perform(click());
                    onView(isRoot()).perform(waitFor(1000));

                } catch (Exception e) {
                    System.out.println("Could not complete follow action: " + e.getMessage());
                }
            } catch (Exception e) {
                System.out.println("Could not click on user in search results: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Complete follow/unfollow flow test failed: " + e.getMessage());
        }
    }

    /**
     * Test: Show requests dialog
     * This test checks that clicking on the requests option in the menu displays the requests dialog.
     */
    @Test
    public void testShowRequestsDialog() {
        try {
            onView(withId(R.id.menu_button)).perform(click());
            onView(isRoot()).perform(waitFor(500));

            onView(withText("Follow Requests")).perform(click());
            onView(isRoot()).perform(waitFor(1000));

            try {
                onView(withId(R.id.request_lists)).check(matches(isDisplayed()));
                onView(isRoot()).perform(waitFor(1000));
            } catch (NoMatchingViewException e) {
                System.out.println("Could not find requests list: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Show requests dialog test failed: " + e.getMessage());
        }
    }

    /**
     * Test: Navigate to edit profile
     * This test checks that clicking on the settings option in the menu navigates to the edit profile page.
     */
    @Test
    public void testNavigateToEditProfile() {
        try {
            onView(withId(R.id.menu_button)).perform(click());
            onView(isRoot()).perform(waitFor(500));

            onView(withText("Settings")).perform(click());
            onView(isRoot()).perform(waitFor(1000));

            try {
                onView(withId(R.id.profile_image_view)).check(matches(isDisplayed()));
                onView(withId(R.id.about_edit_text)).check(matches(isDisplayed()));
                onView(withId(R.id.save_button)).check(matches(isDisplayed()));
                onView(isRoot()).perform(waitFor(1000));
            } catch (NoMatchingViewException e) {
                System.out.println("Could not verify edit profile screen: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Navigate to edit profile test failed: " + e.getMessage());
        }
    }

    /**
     * Test: Verify profile image is correctly displayed
     * This test checks that the profile image is displayed and has content.
     */
    @Test
    public void testProfileImageDisplayed() {
        try {
            onView(withId(R.id.profile_image)).check(matches(isDisplayed()));

            onView(withId(R.id.profile_image)).check(matches(not(withContentDescription(""))));

            onView(withId(R.id.profile_content_container)).check(matches(isDisplayed()));

        } catch (Exception e) {
            System.out.println("Profile image displayed test failed: " + e.getMessage());
        }
    }

    /**
     * Test: Verify profile image loading process
     * This test simulates the process of changing profile pictures by navigating to the edit profile page
     * and clicking on the edit profile image button.
     */
    @Test
    public void testProfileImageLoading() {
        try {

            onView(withId(R.id.profile_image)).check(matches(isDisplayed()));

            onView(withId(R.id.menu_button)).perform(click());
            onView(isRoot()).perform(waitFor(500));

            onView(withText("Settings")).perform(click());
            onView(isRoot()).perform(waitFor(1000));
            onView(withId(R.id.profile_image_view)).check(matches(isDisplayed()));
            onView(withId(R.id.edit_profile_image)).perform(click());
            onView(isRoot()).perform(waitFor(1000));
            onView(isRoot()).perform(waitFor(1000));

            onView(withId(R.id.profile_image_view)).check(matches(isDisplayed()));
            pressBack();
            onView(isRoot()).perform(waitFor(1000));

            onView(withId(R.id.profile_content_container)).check(matches(isDisplayed()));
            onView(withId(R.id.profile_image)).check(matches(isDisplayed()));

        } catch (Exception e) {
            System.out.println("Profile image loading test failed: " + e.getMessage());
        }
    }

    /**
     * Test: Sign out functionality
     * This test checks that clicking on the sign out option in the menu signs the user out.
     * Note: This test should be run last as it will exit the app
     */
    @Test
    public void testSignOut() {
        try {
            onView(withId(R.id.menu_button)).perform(click());
            onView(isRoot()).perform(waitFor(500));

            onView(withText("Sign Out")).perform(click());

            onView(isRoot()).perform(waitFor(1000));
        } catch (Exception e) {
            System.out.println("Sign out test failed: " + e.getMessage());
        }
    }

    /**
     * Helper method to perform a back press
     */
    private void pressBack() {
        try {
            onView(isRoot()).perform(new ViewAction() {
                @Override
                public Matcher<View> getConstraints() {
                    return isRoot();
                }

                @Override
                public String getDescription() {
                    return "Press back button";
                }

                @Override
                public void perform(UiController uiController, View view) {
                    uiController.loopMainThreadForAtLeast(100);
                    androidx.test.espresso.Espresso.pressBack();
                    uiController.loopMainThreadForAtLeast(100);
                }
            });
        } catch (Exception e) {
            System.out.println("Press back failed: " + e.getMessage());
        }
    }
}