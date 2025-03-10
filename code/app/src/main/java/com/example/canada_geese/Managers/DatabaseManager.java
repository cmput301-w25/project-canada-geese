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

/**
 * Manages interactions with Firebase Firestore for mood events.
 * Implements the singleton pattern to ensure a single instance for database operations.
 */
public class DatabaseManager {
    private static final String TAG = "DatabaseManager";
    private static DatabaseManager instance;
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    /**
     * Private constructor for singleton pattern.
     * Initializes Firebase Firestore and FirebaseAuth instances.
     */
    private DatabaseManager() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        Log.d(TAG, "DatabaseManager initialized.");
    }

    /**
     * Returns the singleton instance of DatabaseManager.
     *
     * @return the singleton instance of DatabaseManager.
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Adds a mood event to the Firestore database under the logged-in user's collection.
     * If the user is not logged in, logs an error.
     *
     * @param moodEvent the MoodEventModel object containing mood event details.
     */
    public void addMoodEvent(MoodEventModel moodEvent) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Create a map for mood event data
            Map<String, Object> moodMap = new HashMap<>();
            moodMap.put("userId", userId);  // Associate with current user
            moodMap.put("emotion", moodEvent.getEmotion());
            moodMap.put("description", moodEvent.getDescription());
            moodMap.put("timestamp", moodEvent.getTimestamp());
            moodMap.put("emoji", moodEvent.getEmoji());
            moodMap.put("color", moodEvent.getColor());
            moodMap.put("triggerWarning", moodEvent.hasTriggerWarning());
            if (moodEvent.HasLocation()) {
                moodMap.put("latitude", moodEvent.getLatitude());
                moodMap.put("longitude", moodEvent.getLongitude());
            }

            // Reference to "moodEvents" collection under the user ID
            CollectionReference moodEventsRef = db.collection("users").document(userId).collection("moodEvents");

            // Write to Firestore
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

    /**
     * Fetches all mood events for the logged-in user from Firestore.
     * Orders the results by timestamp in descending order.
     * If the user is not logged in, logs an error.
     *
     * @param listener the OnCompleteListener to handle the query result.
     */
    public void fetchMoodEvents(OnCompleteListener<QuerySnapshot> listener) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Reference to "moodEvents" collection under the user ID
            CollectionReference moodEventsRef = db.collection("users").document(userId).collection("moodEvents");

            // Fetch all documents in the "moodEvents" collection
            moodEventsRef.orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(listener);
        } else {
            Log.e(TAG, "User is not logged in.");
        }
    }

    /**
     * Placeholder method for updating a mood event.
     * Currently not implemented.
     */
    public void updateMoodEvent() {
        Log.d(TAG, "updateMoodEvent() called but not implemented yet.");
    }

    /**
     * Placeholder method for deleting a mood event.
     * Currently not implemented.
     */
    public void deleteMoodEvent() {
        Log.d(TAG, "deleteMoodEvent() called but not implemented yet.");
    }
}