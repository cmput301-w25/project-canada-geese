package com.example.canada_geese.activities_ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.canada_geese.Pages.SignUpActivity;
import com.example.canada_geese.R;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SignUpActivityUITest {

    @Test
    public void testNavigateToLogin() {
        try (ActivityScenario<SignUpActivity> scenario = ActivityScenario.launch(SignUpActivity.class)) {
            onView(ViewMatchers.withId(R.id.sign_up_text)).perform(click());
        }
    }
}
