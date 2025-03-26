package com.example.canada_geese.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.canada_geese.Pages.fragment_user_profile_page;
import com.example.canada_geese.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EditProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private ImageView editProfileImage;
    private EditText aboutEditText;
    private EditText currentPasswordEditText;
    private EditText newPasswordEditText;
    private Button saveButton;
    private Uri imageUri;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        profileImageView = view.findViewById(R.id.profile_image_view);
        editProfileImage = view.findViewById(R.id.edit_profile_image);
        aboutEditText = view.findViewById(R.id.about_edit_text);
        saveButton = view.findViewById(R.id.save_button);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("profile_images");

        editProfileImage.setOnClickListener(v -> openFileChooser());
        saveButton.setOnClickListener(v -> saveProfile());

        loadUserProfile();

        return view;
    }

    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        System.out.println(user);
        if (user != null) {
            String userId = user.getUid();
            Log.d("EditProfile", "Loading profile for user: " + userId);

            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Load about text
                            String about = documentSnapshot.getString("about");
                            if (about != null && !about.isEmpty()) {
                                aboutEditText.setText(about);
                                Log.d("EditProfile", "Loaded about: " + about);
                            } else {
                                Log.d("EditProfile", "No about text found");
                            }

                            // Load profile image
                            String imageUrl = documentSnapshot.getString("image_profile");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Log.d("EditProfile", "Profile image URL found: " + imageUrl);

                                // Load image using Glide or Picasso if you have them
                                // If you're using Glide:
                                // Glide.with(this).load(imageUrl).into(profileImageView);

                                // If you have no image loading library, you'd need to implement
                                // an AsyncTask to download the image

                                // For now, just set a placeholder
                                profileImageView.setImageResource(R.drawable.profile);
                            } else {
                                profileImageView.setImageResource(R.drawable.profile);
                                Log.d("EditProfile", "No profile image URL found");
                            }
                        } else {
                            Log.d("EditProfile", "User document doesn't exist");
                        }
                    })
                    .addOnFailureListener(e -> Log.e("EditProfile", "Failed to load user profile", e));
        } else {
            Log.e("EditProfile", "No user is logged in");
        }
    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
        }
    }

   // Save profile function to save changed to users about and Profile picture of changes accquired and close the activity and return to fragment user profile page
    private void saveProfile() {
        String aboutText = aboutEditText.getText().toString().trim();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();
            Map<String, Object> userUpdates = new HashMap<>();
            userUpdates.put("about", aboutText);

            if (imageUri != null) {
                // Upload image to Firebase Storage
                StorageReference fileRef = storageRef.child(userId + ".jpg");
                fileRef.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            fileRef.getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        String imageUrl = uri.toString();
                                        userUpdates.put("image_profile", imageUrl);
                                        saveUserUpdates(userId, userUpdates);

                                    })
                                    .addOnFailureListener(e -> Log.e("EditProfile", "Failed to get download URL", e));
                        })
                        .addOnFailureListener(e -> Log.e("EditProfile", "Failed to upload image", e));

            } else {
                saveUserUpdates(userId, userUpdates);
            }
        }

    }

    // Save user updates to Firestore
    private void saveUserUpdates(String userId, Map<String, Object> userUpdates) {
        db.collection("users").document(userId).update(userUpdates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("EditProfile", "User profile updated successfully");
                    Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    navigateBackToUserProfile();
                })
                .addOnFailureListener(e -> {
                    Log.e("EditProfile", "Failed to update user profile", e);
                    Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                });
    }

    // Navigate back to main activity
    private void navigateBackToUserProfile() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.action_requests, new fragment_user_profile_page());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}