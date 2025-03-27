package com.example.canada_geese.Pages;



import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.canada_geese.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
    private ImageButton pencilThingy;
    private static final int TAKE_PHOTO_REQUEST = 2;
    //private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<String> requestGalleryPermissionLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private static final int CAMERA_REQUEST_CODE = 2;


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
                        // Permission granted, launch camera
                        openCamera();
                    } else {
                        // Permission denied
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
                        // Permission granted, open gallery
                        chooseFromGallery();
                    } else {
                        // Permission denied
                        Toast.makeText(this, "Allow permissions to access gallery", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();

                        // Camera or Gallery, that is the question
                        if (data.getExtras() != null && data.getExtras().containsKey("data")) {
                            // For Camera
                            Bitmap photo = (Bitmap) data.getExtras().get("data");
                            if (photo != null) {
                                profileImageView.setImageBitmap(photo);
                                imageUri = getImageUri(photo);
                                Log.d("EditProfileActivity", "Camera photo captured");
                            }
                        } else if (data.getData() != null) {
                            // For Gallery:
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

        //pencilThingy.setOnClickListener(v -> openFileChooser());
        pencilThingy.setOnClickListener(v -> showImageOptionsDialog());

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

    /*private void chooseFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
        imagePickerLauncher.launch(intent);
    }*/
    private void chooseFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void takeNewPicture() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       // startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        imagePickerLauncher.launch(cameraIntent);
    }




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


    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
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
                            finish(); // Move this inside the success callback
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

    private void updateUserProfile(String userId, Map<String, Object> userUpdates) {
        db.collection("users").document(userId).update(userUpdates)
                .addOnSuccessListener(aVoid -> Toast.makeText(EditProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.e("EditProfile", "Failed to update profile", e));
    }
}