package com.example.canada_geese.Pages;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.canada_geese.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private EditText aboutEditText;
    private Button saveButton;
    private Uri imageUri;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_profile);

        profileImageView = findViewById(R.id.profile_image_view);
        aboutEditText = findViewById(R.id.about_edit_text);
        saveButton = findViewById(R.id.save_button);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("profile_images");

        profileImageView.setOnClickListener(v -> openFileChooser());
        saveButton.setOnClickListener(v -> saveProfile());

        loadProfile();
    }

    private void loadProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String about = documentSnapshot.getString("about");
                            String profileImageUrl = documentSnapshot.getString("image_profile");

                            if (about != null) {
                                aboutEditText.setText(about);
                            }

                            if (profileImageUrl != null) {
                                Glide.with(this).load(profileImageUrl).into(profileImageView);
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e("EditProfile", "Failed to load user profile", e));
        }
    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
        }
    }

    private void saveProfile() {
        String aboutText = aboutEditText.getText().toString().trim();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();
            Map<String, Object> userUpdates = new HashMap<>();
            userUpdates.put("about", aboutText);

            if (imageUri != null) {
                StorageReference fileReference = storageRef.child(userId + ".jpg");
                fileReference.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            userUpdates.put("image_profile", uri.toString());
                            updateUserProfile(userId, userUpdates);
                        }))
                        .addOnFailureListener(e -> Log.e("EditProfile", "Failed to upload image", e));
                finish();
            } else {
                updateUserProfile(userId, userUpdates);
                finish();
            }
        }
    }

    private void updateUserProfile(String userId, Map<String, Object> userUpdates) {
        db.collection("users").document(userId).update(userUpdates)
                .addOnSuccessListener(aVoid -> Toast.makeText(EditProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.e("EditProfile", "Failed to update profile", e));
    }
}