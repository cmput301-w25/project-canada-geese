package com.example.canada_geese;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.canada_geese.Pages.SignUpActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SignUpActivityUITest {

    @Test
    public void testNavigateToLogin() {
        try (ActivityScenario<SignUpActivity> scenario = ActivityScenario.launch(SignUpActivity.class)) {
            onView(withId(R.id.sign_up_text)).perform(click());
        }
    }
}
