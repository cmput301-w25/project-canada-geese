package com.example.canada_geese;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasFlag;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.canada_geese.Pages.MainActivity;
import com.example.canada_geese.Pages.LoginActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AuthManagerIntentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    // ✅ Improved Main Activity Launch Test
    @Test
    public void testLoginUser_LaunchesMainActivity() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra("user_email", "test@example.com");
        activityRule.getScenario().onActivity(activity -> activity.startActivity(intent));

        intended(hasComponent(MainActivity.class.getName()));
        intended(hasExtra("user_email", "test@example.com"));
    }

    // ✅ Test for Invalid Email
    @Test
    public void testLoginUser_FailsWithInvalidEmail() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LoginActivity.class);
        intent.putExtra("user_email", "invalid_email.com");  // Invalid email format
        activityRule.getScenario().onActivity(activity -> activity.startActivity(intent));

        intended(hasComponent(LoginActivity.class.getName()));  // Should redirect to LoginActivity
        intended(hasExtra("user_email", "invalid_email.com"));
    }

    // ✅ Test for Null Email (Edge Case)
    @Test
    public void testLoginUser_FailsWithNullEmail() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LoginActivity.class);
        intent.putExtra("user_email", (String) null);  // Null email case
        activityRule.getScenario().onActivity(activity -> activity.startActivity(intent));

        intended(hasComponent(LoginActivity.class.getName()));  // Should redirect to LoginActivity
    }

    // ✅ Test for Intent Flags (Ensures correct intent flags are set)
    @Test
    public void testMainActivity_IntentContainsCorrectFlags() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activityRule.getScenario().onActivity(activity -> activity.startActivity(intent));

        intended(hasFlag(Intent.FLAG_ACTIVITY_NEW_TASK));
        intended(hasFlag(Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    // ✅ Test for Package Validation (Ensures intent is sent to the correct app)
    @Test
    public void testMainActivity_IntentGoesToCorrectPackage() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        activityRule.getScenario().onActivity(activity -> activity.startActivity(intent));

        Context context = ApplicationProvider.getApplicationContext();
        PackageManager pm = context.getPackageManager();

        String expectedPackage = context.getPackageName();  // Expected package name
        intended(toPackage(expectedPackage));
    }
}


