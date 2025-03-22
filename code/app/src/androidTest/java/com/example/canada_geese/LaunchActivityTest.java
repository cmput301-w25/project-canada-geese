package com.example.canada_geese;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.canada_geese.Pages.LaunchActivity;
import com.example.canada_geese.Pages.LoginActivity;
import com.example.canada_geese.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

@RunWith(AndroidJUnit4.class)
public class LaunchActivityTest {

    @Rule
    public ActivityTestRule<LaunchActivity> activityRule =
            new ActivityTestRule<>(LaunchActivity.class, true, false);

    @Before
    public void setUp() {
        // 初始化 Intents
        Intents.init();
    }

    @After
    public void tearDown() {
        // 释放 Intents
        Intents.release();
    }

    @Test
    public void testLaunchActivityNavigatesToLoginActivity() {
        // 启动 LaunchActivity
        ActivityScenario.launch(LaunchActivity.class);

        // 等待 3 秒
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 验证是否跳转到 LoginActivity
        intended(hasComponent(LoginActivity.class.getName()));
    }
}