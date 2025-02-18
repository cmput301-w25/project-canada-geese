package com.example.canada_geese;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {
    Button logoutButton;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        logoutButton = findViewById(R.id.btn_logout);
        auth = FirebaseAuth.getInstance();

        logoutButton.setOnClickListener(view -> {
            auth.signOut(); // Firebase logout
            Toast.makeText(UserProfileActivity.this, "Logged out!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserProfileActivity.this, LoginActivity.class));
            finish(); // Close profile page
        });
    }

    
    Button deleteAccountButton;
    FirebaseFirestore db;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        deleteAccountButton = findViewById(R.id.btn_delete_account);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        deleteAccountButton.setOnClickListener(view -> showDeleteConfirmationDialog());
    }

    // Show Confirmation Dialog Before Deletion
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("This action is irreversible. Do you want to proceed?")
                .setPositiveButton("Delete", (dialog, which) -> deleteAccount())
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Delete User Account
    private void deleteAccount() {
        if (user != null) {
            String userId = user.getUid();

            // Delete Firestore Data
            db.collection("users").document(userId).delete()
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "User data deleted"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Error deleting user data", e));

            // Delete Authentication Data
            user.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(UserProfileActivity.this, "Account Deleted", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UserProfileActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(UserProfileActivity.this, "Account deletion failed!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}
