package com.example.canada_geese.Pages;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.canada_geese.R;

/**
 * Launch screen activity shown briefly when the app is first opened.
 * Transitions to the login screen after a short delay.
 */
public class LaunchActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY_MS = 3000; // Duration of splash screen in milliseconds
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable launchRunnable = () -> {
        startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
        finish();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        handler.postDelayed(launchRunnable, SPLASH_DELAY_MS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(launchRunnable); // Prevent memory leaks
    }
}
