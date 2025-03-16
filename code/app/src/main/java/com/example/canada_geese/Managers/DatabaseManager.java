package com.example.canada_geese.Managers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.canada_geese.Models.MoodEventModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    protected DatabaseManager() {
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
                moodMap.put("hasLocation", moodEvent.HasLocation());
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
     * Updates an existing mood event in the database.
     *
     * @param moodEvent The updated mood event
     * @param documentId The Firestore document ID of the mood event to update
     * @param listener Optional listener to handle completion events
     */
    public void updateMoodEvent(MoodEventModel moodEvent, String documentId, OnCompleteListener<Void> listener) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null && documentId != null && !documentId.isEmpty()) {
            String userId = user.getUid();

            // Create a map for mood event data
            Map<String, Object> moodMap = new HashMap<>();
            moodMap.put("emotion", moodEvent.getEmotion());
            moodMap.put("description", moodEvent.getDescription());
            moodMap.put("timestamp", moodEvent.getTimestamp());
            moodMap.put("emoji", moodEvent.getEmoji());
            moodMap.put("color", moodEvent.getColor());
            moodMap.put("triggerWarning", moodEvent.hasTriggerWarning());
            moodMap.put("hasLocation", moodEvent.HasLocation());
            if (moodEvent.HasLocation()) {
                moodMap.put("latitude", moodEvent.getLatitude());
                moodMap.put("longitude", moodEvent.getLongitude());
            }

            // Update the document
            db.collection("users").document(userId)
                    .collection("moodEvents").document(documentId)
                    .update(moodMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Mood event updated successfully");
                        } else {
                            Log.e(TAG, "Failed to update mood event", task.getException());
                        }

                        if (listener != null) {
                            listener.onComplete(task);
                        }
                    });
        } else {
            Log.e(TAG, "Cannot update mood: User not logged in or document ID not provided");
        }
    }

    /**
     * Deletes a mood event from the database.
     *
     * @param documentId The Firestore document ID of the mood event to delete
     * @param listener Optional listener to handle completion events
     */
    public void deleteMoodEvent(String documentId, OnCompleteListener<Void> listener) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null && documentId != null && !documentId.isEmpty()) {
            String userId = user.getUid();

            // Delete the document
            db.collection("users").document(userId)
                    .collection("moodEvents").document(documentId)
                    .delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Mood event deleted successfully");
                        } else {
                            Log.e(TAG, "Failed to delete mood event", task.getException());
                        }

                        if (listener != null) {
                            listener.onComplete(task);
                        }
                    });
        } else {
            Log.e(TAG, "Cannot delete mood: User not logged in or document ID not provided");
        }
    }

    /**
     * Finds the document ID for a mood event based on its timestamp and emotion.
     * This is useful when you don't store the document ID directly.
     *
     * @param timestamp The timestamp of the mood event
     * @param emotion The emotion of the mood event
     * @param listener Listener that receives the document ID or null if not found
     */
    public void findMoodEventDocumentId(String timestamp, String emotion, OnCompleteListener<DocumentSnapshot> listener) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            db.collection("users").document(userId).collection("moodEvents")
                    .whereEqualTo("timestamp", timestamp)
                    .whereEqualTo("emotion", emotion)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            if (listener != null) {
                                listener.onComplete(Tasks.forResult(document));
                            }
                        } else {
                            if (listener != null) {
                                listener.onComplete(Tasks.forException(
                                        new Exception("Document not found or query failed")));
                            }
                        }
                    });
        } else {
            if (listener != null) {
                listener.onComplete(Tasks.forException(
                        new Exception("User not logged in")));
            }
        }
    }
}