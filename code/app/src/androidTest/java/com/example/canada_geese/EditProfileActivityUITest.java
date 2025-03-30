package com.example.canada_geese;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.not;

import android.Manifest;
import android.app.Instrumentation;
import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.espresso.intent.matcher.IntentMatchers;



import com.example.canada_geese.Pages.EditProfileActivity;

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

    @Test
    public void testShowImageOptionsDialog() {
        onView(withId(R.id.edit_profile_image)).perform(click());
        onView(withId(android.R.id.content)).check(matches(isDisplayed()));
    }

    @Test
    public void testChooseFromGallery() {
        Intent resultData = new Intent();
        resultData.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        Instrumentation.ActivityResult result =
                new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        Intents.intending(hasAction(Intent.ACTION_PICK)).respondWith(result);


        onView(withId(R.id.edit_profile_image)).perform(click());
        onView(withText("Choose from Gallery")).perform(click());
    }

    @Test
    public void testTakeNewPicture() {
        Intent resultData = new Intent();
        resultData.putExtra("data", android.graphics.Bitmap.createBitmap(100, 100, android.graphics.Bitmap.Config.ARGB_8888));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(result);

        onView(withId(R.id.edit_profile_image)).perform(click());
    }

    @Test
    public void testRemoveProfilePicture() {
        onView(withId(R.id.edit_profile_image)).perform(click());
    }

    @Test
    public void testSaveProfileWithImageChanges() {
        onView(withId(R.id.save_button)).perform(click());
    }

    @Test
    public void testUpdateProfileInformation() {
        onView(withId(R.id.about_edit_text)).check(matches(isDisplayed()));
    }

    @Test
    public void testChangeProfilePicture() {
        onView(withId(R.id.edit_profile_image)).check(matches(isDisplayed()));
    }
}
