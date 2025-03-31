package com.example.canada_geese.Fragments;
import com.example.canada_geese.R;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import static org.hamcrest.Matchers.allOf;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.canada_geese.Fragments.RequestsDialogFragment;
import com.example.canada_geese.Pages.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class RequestsDialogFragmentTest {

    @Before
    public void setupEmulator() {
        FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
        FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8080);
    }

    private void signInAndLaunchFragment(Runnable whenReady) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            addMockRequestAndLaunch(auth.getCurrentUser().getUid(), whenReady);
            return;
        }

        auth.signInWithEmailAndPassword("testuser@example.com", "password123")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = auth.getCurrentUser().getUid();
                        addMockRequestAndLaunch(uid, whenReady);
                    } else {
                        auth.createUserWithEmailAndPassword("testuser@example.com", "password123")
                                .addOnCompleteListener(createTask -> {
                                    if (createTask.isSuccessful()) {
                                        String uid = auth.getCurrentUser().getUid();
                                        addMockRequestAndLaunch(uid, whenReady);
                                    } else {
                                        throw new RuntimeException("Login and user creation both failed.");
                                    }
                                });
                    }
                });
    }

    private void addMockRequestAndLaunch(String currentUserUid, Runnable whenReady) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Insert a mock request into Firestore
        Map<String, Object> mockRequest = new HashMap<>();
        mockRequest.put("username", "mock_user");
        mockRequest.put("status", "pending");

        db.collection("users")
                .document(currentUserUid)
                .collection("requests")
                .add(mockRequest)
                .addOnSuccessListener(docRef -> whenReady.run())
                .addOnFailureListener(e -> {
                    throw new RuntimeException("Failed to insert mock follow request.", e);
                });
    }

    private void launchFragment() {
        ActivityScenario.launch(MainActivity.class).onActivity(activity -> {
            Fragment fragment = new RequestsDialogFragment();
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commitNow();
        });
    }

    @Test
    public void testRequestsDisplayed() {
        signInAndLaunchFragment(() -> {
            launchFragment();
            onView(withText("mock_user")).check(matches(isDisplayed()));
        });
    }

    @Test
    public void testAcceptRequest() {
        signInAndLaunchFragment(() -> {
            launchFragment();
            onView(allOf(withId(R.id.accept_button), withText("Accept"))).perform(click());
        });
    }

    @Test
    public void testRejectRequest() {
        signInAndLaunchFragment(() -> {
            launchFragment();
            onView(allOf(withId(R.id.reject_button), withText("Reject"))).perform(click());
        });
    }
}
