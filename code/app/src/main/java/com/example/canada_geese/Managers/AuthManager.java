
/*
package com.example.canada_geese.Managers;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

public class AuthManager {
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    public AuthManager() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    // Register User (Email + Username)
    public void registerUser(String email, String password, String username, Context context, AuthCallback callback) {
        db.collection("users").whereEqualTo("username", username).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().isEmpty()) {
                        auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(authTask -> {
                                    if (authTask.isSuccessful()) {
                                        String userId = auth.getCurrentUser().getUid();
                                        User user = new User(username, email, userId);
                                        db.collection("users").document(userId).set(user);
                                        callback.onSuccess();
                                    } else {
                                        callback.onFailure(authTask.getException().getMessage());
                                    }
                                });
                    } else {
                        callback.onFailure("Username already exists!");
                    }
                });
    }

    // ✅ Login User (with Email or Username)
    public void loginUser(String identifier, String password, AuthCallback callback) {
        if (identifier.contains("@")) {
            signInWithEmail(identifier, password, callback);
        } else {
            db.collection("users").whereEqualTo("username", identifier).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            String email = task.getResult().getDocuments().get(0).getString("email");
                            signInWithEmail(email, password, callback);
                        } else {
                            callback.onFailure("Username not found!");
                        }
                    });
        }
    }

    // ✅ Firebase Email Login
    private void signInWithEmail(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    // ✅ Logout User
    public void logoutUser() {
        auth.signOut();
    }

    // ✅ Delete User & Remove Data
    public void deleteUser(AuthCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("users").document(userId).delete();
            user.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure(task.getException().getMessage());
                }
            });
        }
    }

    // ✅ Interface for Callbacks
    public interface AuthCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
}

 */