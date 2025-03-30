package com.example.canada_geese;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.canada_geese.Pages.LoginActivity;
import com.example.canada_geese.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;

@RunWith(AndroidJUnit4.class)
public class LoginActivityUITest {

    private SharedPreferences sharedPreferences;

    @Before
    public void setUp() {
        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            scenario.onActivity(activity -> {
                sharedPreferences = activity.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();
            });
        }
    }

    @Test
    public void testRememberMeCheckbox() {
        final String PREF_NAME = "loginPrefs";
        final String KEY_USERNAME = "username";
        final String KEY_PASSWORD = "password";
        final String KEY_REMEMBER_ME = "rememberMe";

        String testUsername = "rememberUser";
        String testPassword = "rememberPass";

        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            scenario.onActivity(activity -> {
                CheckBox rememberMeCheckBox = activity.findViewById(R.id.remember_me);
                rememberMeCheckBox.setChecked(true);
                EditText usernameField = activity.findViewById(R.id.username_input);
                EditText passwordField = activity.findViewById(R.id.password_input);
                usernameField.setText(testUsername);
                passwordField.setText(testPassword);

                try {
                    Method saveMethod = LoginActivity.class.getDeclaredMethod(
                            "saveLoginInfo", String.class, String.class, String.class
                    );
                    saveMethod.setAccessible(true);
                    saveMethod.invoke(activity, "test@example.com", testPassword, testUsername);
                } catch (Exception e) {
                    fail("Failed to call saveLoginInfo: " + e.getMessage());
                }

                SharedPreferences prefs = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                assertEquals(testUsername, prefs.getString(KEY_USERNAME, ""));
                assertEquals(testPassword, prefs.getString(KEY_PASSWORD, ""));
                assertTrue(prefs.getBoolean(KEY_REMEMBER_ME, false));
            });
        }
    }

    @Test
    public void testNavigateToSignUp() {
        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class)) {
            scenario.onActivity(activity -> {
                TextView signUpText = activity.findViewById(R.id.sign_up_text);
                signUpText.performClick();

                assertTrue(activity.isFinishing());
            });
        }
    }
}