package com.example.canada_geese.Managers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.canada_geese.Models.MoodEventModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {
    private static final String TAG = "DatabaseManager";
    private static DatabaseManager instance;
    protected FirebaseFirestore db;  // Changed to protected for test subclass
    protected FirebaseAuth auth;     // Changed to protected for test subclass

    // ---- Private Constructor for Singleton ----
    private DatabaseManager() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        Log.d(TAG, "DatabaseManager initialized.");
    }

    // ---- Protected Constructor for Test Subclass ----
    protected DatabaseManager(boolean isTestMode) {
        if (isTestMode) {
            db = null;   // Disable Firebase to avoid real interactions
            auth = null;
        } else {
            db = FirebaseFirestore.getInstance();
            auth = FirebaseAuth.getInstance();
        }
        Log.d(TAG, "DatabaseManager initialized (Test Mode: " + isTestMode + ").");
    }

    // ---- Get Singleton Instance ----
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    // ---- Add Mood Event to Firestore (Similar to Saving User Info) ----
    public void addMoodEvent(MoodEventModel moodEvent) {
        if (auth == null) {
            Log.e(TAG, "Test mode active: FirebaseAuth is disabled.");
            return;
        }

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            Map<String, Object> moodMap = new HashMap<>();
            moodMap.put("emotion", moodEvent.getEmotion());
            moodMap.put("timestamp", moodEvent.getTimestamp());
            moodMap.put("emoji", moodEvent.getEmoji());
            moodMap.put("color", moodEvent.getColor());
            moodMap.put("triggerWarning", moodEvent.hasTriggerWarning());
            moodMap.put("userId", userId);

            CollectionReference moodEventsRef = db.collection("users").document(userId).collection("moodEvents");

            moodEventsRef.add(moodMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Mood event added successfully: " + task.getResult().getId());
                    } else {
                        Log.e(TAG, "Failed to add mood event: ", task.getException());
                    }
                }
            });
        } else {
            Log.e(TAG, "User is not logged in.");
        }
    }

    // ---- Fetch Mood Events for Logged-in User ----
    public void fetchMoodEvents(OnCompleteListener<QuerySnapshot> listener) {
        if (auth == null) {
            Log.e(TAG, "Test mode active: FirebaseAuth is disabled.");
            return;
        }

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            db.collection("users").document(userId).collection("moodEvents")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(listener);
        } else {
            Log.e(TAG, "User is not logged in.");
        }
    }

    // ---- Placeholder for Updating a Mood Event ----
    public void updateMoodEvent() {
        Log.d(TAG, "updateMoodEvent() called but not implemented yet.");
    }

    // ---- Placeholder for Deleting a Mood Event ----
    public void deleteMoodEvent() {
        Log.d(TAG, "deleteMoodEvent() called but not implemented yet.");
    }
}
