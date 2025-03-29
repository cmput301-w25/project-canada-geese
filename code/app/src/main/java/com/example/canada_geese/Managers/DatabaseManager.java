package com.example.canada_geese.Managers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.canada_geese.Models.CommentModel;
import com.example.canada_geese.Models.MoodEventModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
            moodMap.put("userId", userId);
            moodMap.put("emotion", moodEvent.getEmotion());
            if(moodEvent.getDescription().isEmpty()){
                moodMap.put("description", "None provided");
            }else{
                moodMap.put("description", moodEvent.getDescription());
            }

            moodMap.put("timestamp", moodEvent.getTimestamp());
            moodMap.put("emoji", moodEvent.getEmoji());
            moodMap.put("color", moodEvent.getColor());
            moodMap.put("isPrivate", moodEvent.hasTriggerWarning());
            moodMap.put("socialSituation", moodEvent.getSocialSituation());

            if (moodEvent.HasLocation()) {
                moodMap.put("hasLocation", true);
                moodMap.put("latitude", moodEvent.getLatitude());
                moodMap.put("longitude", moodEvent.getLongitude());
            }

            if (moodEvent.getImageUrls() != null && !moodEvent.getImageUrls().isEmpty()) {
                moodMap.put("imageUrls", moodEvent.getImageUrls());
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
            moodMap.put("socialSituation", moodEvent.getSocialSituation());
            if (moodEvent.HasLocation()) {
                moodMap.put("latitude", moodEvent.getLatitude());
                moodMap.put("longitude", moodEvent.getLongitude());
            }

            if (moodEvent.getImageUrls() != null && !moodEvent.getImageUrls().isEmpty()) {
                moodMap.put("imageUrls", moodEvent.getImageUrls());
            } else {
                moodMap.put("imageUrls", new ArrayList<>()); // 🔄 Optional: clear all if none
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
            DocumentReference moodRef = db.collection("users").document(userId)
                    .collection("moodEvents").document(documentId);

            // Step 1: Fetch the mood document to get image URLs
            moodRef.get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    List<String> imageUrls = (List<String>) snapshot.get("imageUrls");

                    if (imageUrls != null && !imageUrls.isEmpty()) {
                        for (String url : imageUrls) {
                            // Convert download URL to storage path
                            StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                            ref.delete().addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Image deleted: " + url);
                            }).addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to delete image: " + url, e);
                            });
                        }
                    }
                }

                // Step 2: Delete the mood document from Firestore
                moodRef.delete().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Mood event deleted successfully");
                    } else {
                        Log.e(TAG, "Failed to delete mood event", task.getException());
                    }

                    if (listener != null) {
                        listener.onComplete(task);
                    }
                });

            }).addOnFailureListener(e -> {
                Log.e(TAG, "Failed to fetch mood document for deletion", e);
                if (listener != null) {
                    listener.onComplete(Tasks.forException(e));
                }
            });
        } else {
            Log.e(TAG, "Cannot delete mood: User not logged in or document ID not provided");
            if (listener != null) {
                listener.onComplete(Tasks.forException(new Exception("Invalid delete request")));
            }
        }
    }

    /**
     * Finds the document ID for a mood event based on its timestamp and emotion.
     * This is useful when you don't store the document ID directly.
     *
     * @param timestamp The timestamp of the mood event (Date)
     * @param emotion The emotion of the mood event
     * @param listener Listener that receives the document ID or null if not found
     */
    public void findMoodEventDocumentId(Date timestamp, String emotion, OnCompleteListener<DocumentSnapshot> listener) {
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

    public void fetchAllUsers(OnCompleteListener<QuerySnapshot> listener) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Log.e(TAG, "User is not logged in.");
            return;
        }
        CollectionReference usersRef = db.collection("users");
        usersRef.get().addOnCompleteListener(listener);
    }

    public void addComment(String ownerUserId, String moodEventId, CommentModel comment, OnCompleteListener<DocumentReference> listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && ownerUserId != null && moodEventId != null) {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(ownerUserId)
                    .collection("moodEvents")
                    .document(moodEventId)
                    .collection("comments")
                    .add(comment)
                    .addOnCompleteListener(listener);
        } else {
            if (listener != null) {
                listener.onComplete(Tasks.forException(new Exception("User not logged in or invalid mood reference")));
            }
        }
    }

    public void deleteComment(String moodEventId, String commentId, OnCompleteListener<Void> listener) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null && moodEventId != null && commentId != null) {
            String userId = user.getUid();
            db.collection("users").document(userId)
                    .collection("moodEvents").document(moodEventId)
                    .collection("comments").document(commentId)
                    .delete()
                    .addOnCompleteListener(listener);
        } else {
            if (listener != null) {
                listener.onComplete(Tasks.forException(new Exception("User not logged in or moodEventId/commentId missing")));
            }
        }
    }

    public void fetchFollowedUsersMoodEvents(OnCompleteListener<List<MoodEventModel>> finalListener) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            finalListener.onComplete(Tasks.forException(new Exception("User not logged in")));
            return;
        }

        String userId = currentUser.getUid();
        CollectionReference followingRef = db.collection("users").document(userId).collection("following");

        followingRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                finalListener.onComplete(Tasks.forException(task.getException()));
                return;
            }

            List<String> followedUsernames = new ArrayList<>();
            for (DocumentSnapshot doc : task.getResult()) {
                String username = doc.getString("username");
                if (username != null && !username.isEmpty()) {
                    followedUsernames.add(username);
                    Log.d(TAG, "Following username: " + username);
                }
            }

            if (followedUsernames.isEmpty()) {
                finalListener.onComplete(Tasks.forResult(new ArrayList<>()));
                return;
            }

            // Step 2: Get user IDs from usernames
            db.collection("users").get().addOnCompleteListener(usersTask -> {
                if (!usersTask.isSuccessful() || usersTask.getResult() == null) {
                    finalListener.onComplete(Tasks.forException(usersTask.getException()));
                    return;
                }

                List<String> followedUserIds = new ArrayList<>();
                for (DocumentSnapshot userDoc : usersTask.getResult()) {
                    String username = userDoc.getString("username");
                    if (username != null && followedUsernames.contains(username)) {
                        followedUserIds.add(userDoc.getId());
                        Log.d(TAG, "Matched username: " + username + " -> UID: " + userDoc.getId());
                    }
                }

                if (followedUserIds.isEmpty()) {
                    finalListener.onComplete(Tasks.forResult(new ArrayList<>()));
                    return;
                }

                // Step 3: Fetch mood events from each followed user
                List<Task<QuerySnapshot>> moodTasks = new ArrayList<>();
                for (String followedId : followedUserIds) {
                    Task<QuerySnapshot> moodTask = db.collection("users")
                            .document(followedId)
                            .collection("moodEvents")
                            .whereEqualTo("isPublic", true) // 👈 ONLY public moods
                            .orderBy("timestamp", Query.Direction.DESCENDING)
                            .limit(3)
                            .get();
                    moodTasks.add(moodTask);
                }

                Tasks.whenAllSuccess(moodTasks).addOnCompleteListener(moodEventsTask -> {
                    List<MoodEventModel> combinedMoods = new ArrayList<>();
                    for (Object result : moodEventsTask.getResult()) {
                        List<DocumentSnapshot> docs = ((QuerySnapshot) result).getDocuments();
                        for (DocumentSnapshot moodDoc : docs) {
                            MoodEventModel mood = moodDoc.toObject(MoodEventModel.class);
                            if (mood != null) {
                                mood.setDocumentId(moodDoc.getId());
                                combinedMoods.add(mood);
                            }
                        }
                    }

                    Log.d(TAG, "Fetched " + combinedMoods.size() + " mood events from followed users");
                    finalListener.onComplete(Tasks.forResult(combinedMoods));
                });
            });
        });
    }

    public void fetchAllUsernames(OnSuccessListener<Map<String, String>> listener) {
        db.collection("users").get().addOnSuccessListener(querySnapshot -> {
            Map<String, String> result = new HashMap<>();
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                String uid = doc.getId();
                String username = doc.getString("username");
                if (username != null) {
                    result.put(uid, username);
                }
            }
            listener.onSuccess(result);
        });
    }

}