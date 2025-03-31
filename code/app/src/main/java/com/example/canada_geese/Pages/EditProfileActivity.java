package com.example.canada_geese.Pages;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.canada_geese.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity that allows users to edit their profile including bio and profile image.
 */
public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST_CODE = 2;

    private ImageView profileImageView;
    private EditText aboutEditText;
    private Button saveButton;
    private ImageButton pencilThingy;
    private Uri imageUri;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<String> requestGalleryPermissionLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;

    /**
     * Initializes the activity and sets up Firebase, UI components, and event listeners.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_profile);

        profileImageView = findViewById(R.id.profile_image_view);
        aboutEditText = findViewById(R.id.about_edit_text);
        saveButton = findViewById(R.id.save_button);
        pencilThingy = findViewById(R.id.edit_profile_image);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("profile_images");

        requestCameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCamera();
                    } else {
                        Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getExtras() != null) {
                            Bitmap photo = (Bitmap) data.getExtras().get("data");
                            if (photo != null) {
                                profileImageView.setImageBitmap(photo);
                                imageUri = getImageUri(photo);
                            }
                        }
                    }
                }
        );

        requestGalleryPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        chooseFromGallery();
                    } else {
                        Toast.makeText(this, "Allow permissions to access gallery", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        if (data.getExtras() != null && data.getExtras().containsKey("data")) {
                            Bitmap photo = (Bitmap) data.getExtras().get("data");
                            if (photo != null) {
                                profileImageView.setImageBitmap(photo);
                                imageUri = getImageUri(photo);
                                Log.d("EditProfileActivity", "Camera photo captured");
                            }
                        } else if (data.getData() != null) {
                            imageUri = data.getData();
                            Glide.with(this)
                                    .load(imageUri)
                                    .circleCrop()
                                    .into(profileImageView);
                            Log.d("EditProfileActivity", "Gallery image selected: " + imageUri);
                        }
                    }
                }
        );

        pencilThingy.setOnClickListener(v -> showImageOptionsDialog());
        saveButton.setOnClickListener(v -> saveProfile());

        loadProfile();
    }

    /**
     * Loads the user's current profile details from Firestore.
     */
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
                                Glide.with(this)
                                        .load(profileImageUrl)
                                        .circleCrop()
                                        .into(profileImageView);
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e("EditProfile", "Failed to load user profile", e));
        }
    }

    /**
     * Displays options for choosing a new profile picture.
     */
    private void showImageOptionsDialog() {
        String[] options = {"Choose from Gallery", "Take a New Picture", "Remove Picture"};

        new android.app.AlertDialog.Builder(this)
                .setTitle("Profile Picture Options")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        chooseFromGallery();
                    } else if (which == 1) {
                        takeNewPicture();
                    } else if (which == 2) {
                        removeProfilePicture();
                    }
                })
                .show();
    }

    /**
     * Opens the gallery to allow the user to pick an image.
     */
    private void chooseFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    /**
     * Checks for camera permission and opens the camera if granted.
     * If permission is not granted, it requests the permission.
     */
    private void takeNewPicture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    /**
     * Launches the camera app to take a new picture.
     */
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imagePickerLauncher.launch(cameraIntent);
    }

    /**
     * Handles the result of permission requests.
     *
     * @param requestCode  The request code passed in requestPermissions
     * @param permissions  The requested permissions
     * @param grantResults The grant results for the corresponding permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Prompts the user to confirm removal of the profile picture.
     * If confirmed, it resets the image and updates Firestore.
     */
    private void removeProfilePicture() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Remove Profile Picture")
                .setMessage("Are you sure you want to delete your profile picture?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    profileImageView.setImageResource(R.drawable.profile);
                    imageUri = null;

                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        db.collection("users").document(user.getUid())
                                .update("image_profile", null)
                                .addOnSuccessListener(aVoid ->
                                        Toast.makeText(this, "Profile picture removed", Toast.LENGTH_SHORT).show()
                                )
                                .addOnFailureListener(e ->
                                        Log.e("EditProfile", "Failed to remove profile picture", e)
                                );
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Converts a Bitmap to a Uri by inserting it into the MediaStore.
     *
     * @param bitmap the bitmap to convert
     * @return the Uri of the saved image
     */
    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    /**
     * Saves the user's profile information, including uploading the profile image if one is selected.
     */
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
                            finish();
                        }))
                        .addOnFailureListener(e -> {
                            Log.e("EditProfile", "Failed to upload image", e);
                            Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        });
            } else {
                updateUserProfile(userId, userUpdates);
                finish();
            }
        }
    }

    /**
     * Updates the user's profile document in Firestore with the given data.
     *
     * @param userId      the ID of the user
     * @param userUpdates the map of profile fields to update
     */
    private void updateUserProfile(String userId, Map<String, Object> userUpdates) {
        db.collection("users").document(userId).update(userUpdates)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(EditProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Log.e("EditProfile", "Failed to update profile", e));
    }
}