package com.example.canada_geese.activities_ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.example.canada_geese.Pages.EditProfileActivity;
import com.example.canada_geese.R;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EditProfileActivityUITest {

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
    );

    @Rule
    public ActivityScenarioRule<EditProfileActivity> activityRule =
            new ActivityScenarioRule<>(EditProfileActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    private ViewAction waitAndClick() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Wait for animations and click";
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadForAtLeast(500);
                view.performClick();
            }
        };
    }

    @Test
    public void testShowImageOptionsDialog() {
        onView(ViewMatchers.withId(R.id.edit_profile_image)).perform(waitAndClick());
        onView(withText("Choose from Gallery")).check(matches(isDisplayed()));
    }

    @Test
    public void testChooseFromGallery() {
        Intent resultData = new Intent();
        resultData.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        Instrumentation.ActivityResult result =
                new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        Intents.intending(IntentMatchers.anyIntent()).respondWith(result);

        onView(withId(R.id.edit_profile_image)).perform(waitAndClick());
        onView(withText("Choose from Gallery")).perform(waitAndClick());
    }

    @Test
    public void testTakeNewPicture() {
        Intent resultData = new Intent();
        resultData.putExtra("data", android.graphics.Bitmap.createBitmap(100, 100, android.graphics.Bitmap.Config.ARGB_8888));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        Intents.intending(IntentMatchers.anyIntent()).respondWith(result);

        onView(withId(R.id.edit_profile_image)).perform(waitAndClick());
        onView(withText("Take a New Picture")).perform(waitAndClick());
    }

    @Test
    public void testRemoveProfilePicture() {
        onView(withId(R.id.edit_profile_image)).perform(waitAndClick());
        onView(withText("Remove Picture")).perform(waitAndClick());
    }

    @Test
    public void testSaveProfileWithImageChanges() {
        Intent galleryIntent = new Intent();
        galleryIntent.setData(Uri.parse("content://media/external/images/media/1"));
        Instrumentation.ActivityResult result =
                new Instrumentation.ActivityResult(Activity.RESULT_OK, galleryIntent);
        Intents.intending(IntentMatchers.anyIntent()).respondWith(result);

        onView(withId(R.id.edit_profile_image)).perform(waitAndClick());

        onView(withText("Choose from Gallery")).perform(waitAndClick());

        onView(isRoot()).perform(waitFor(1000));
        Espresso.pressBack();
        onView(isRoot()).perform(waitFor(1000));

        onView(withId(R.id.save_button)).perform(waitAndClick());
    }


    @Test
    public void testUpdateProfileInformation() {
        onView(withId(R.id.about_edit_text)).check(matches(isDisplayed()));
    }

    @Test
    public void testChangeProfilePicture() {
        onView(withId(R.id.edit_profile_image)).perform(waitAndClick());
        onView(withText("Choose from Gallery")).check(matches(isDisplayed()));
        onView(withText("Take a New Picture")).check(matches(isDisplayed()));
        onView(withText("Remove Picture")).check(matches(isDisplayed()));
    }

    @Test
    public void testProfileImageUploadFlow() {
        Intent galleryIntent = new Intent();
        galleryIntent.setData(Uri.parse("content://media/external/images/media/1"));
        Instrumentation.ActivityResult result =
                new Instrumentation.ActivityResult(Activity.RESULT_OK, galleryIntent);
        Intents.intending(IntentMatchers.anyIntent()).respondWith(result);
        onView(withId(R.id.edit_profile_image)).perform(waitAndClick());

        onView(withText("Choose from Gallery")).perform(waitAndClick());

        onView(isRoot()).perform(waitFor(1000));
        Espresso.pressBack();
        onView(isRoot()).perform(waitFor(1000));

        onView(withId(R.id.save_button)).perform(waitAndClick());
    }



    @Test
    public void testSaveProfileWithMultipleChanges() {
        onView(withId(R.id.edit_profile_image)).perform(waitAndClick());

        onView(withText("Remove Picture")).perform(waitAndClick());

        onView(isRoot()).perform(waitFor(1000));
        Espresso.pressBack();

        onView(isRoot()).perform(waitFor(1000));

        onView(withId(R.id.save_button)).perform(waitAndClick());
    }

    public static ViewAction waitFor(long delay) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + delay + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadForAtLeast(delay);
            }
        };
    }
}
