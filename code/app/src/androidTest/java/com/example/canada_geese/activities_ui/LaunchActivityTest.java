package com.example.canada_geese.activities_ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.canada_geese.Pages.LaunchActivity;
import com.example.canada_geese.R;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LaunchActivityTest {

    @Test
    public void testSplashScreenDisplayed() {
        try (ActivityScenario<LaunchActivity> scenario = ActivityScenario.launch(LaunchActivity.class)) {
            onView(withId(R.id.rootLayout)).check(matches(isDisplayed()));

            onView(withId(R.id.launch)).check(matches(isDisplayed()));

            onView(withId(R.id.goose)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testNavigatesToLoginAfterDelay() throws InterruptedException {
    }

    @Test
    public void testFinishesAfterNavigating() throws InterruptedException {
    }
}