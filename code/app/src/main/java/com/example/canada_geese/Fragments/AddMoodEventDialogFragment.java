package com.example.canada_geese.Fragments;
import static android.graphics.Insets.add;

import static androidx.core.util.TypedValueCompat.dpToPx;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.graphics.Bitmap;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.canada_geese.Managers.DatabaseManager;
import com.example.canada_geese.Models.MoodEventModel;
import com.example.canada_geese.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A dialog fragment that allows the user to add a new mood event.
 */


public class AddMoodEventDialogFragment extends DialogFragment {
    private Spinner moodSpinner;
    private Spinner socialSituationSpinner;
    private Button addMoodButton;
    private Button selectImageButton;
    private Switch addLocationCheckbox;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap googleMap;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int GALLERY_PERMISSION_CODE = 200;
    private static final int GALLERY_REQUEST_CODE = 201;
    private ImageView imageView;
    private static final String TAG = "AddMoodEventDialog";
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;

    // Add these class variables to store the current location
    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;
    private boolean locationRetrieved = false;

    private Bitmap selectedImage = null;
    private ArrayList<Bitmap> selectedImages = new ArrayList<>();

    private HorizontalScrollView imagesScrollView;
    private LinearLayout imagesContainer;

    private static final int LOCATION_PERMISSION_REQUEST = 100;
    private ActivityResultLauncher<PickVisualMediaRequest> photoPickerLauncher;
    private ActivityResultLauncher<String> requestGalleryPermissionLauncher;

    private MoodEventModel moodToEdit = null;
    private boolean isEditMode = false;
    private String documentIdToUpdate = null;
    public interface OnDismissListener {
        void onDismiss();
    }
    private OnDismissListener dismissListener;
    public void setOnDismissListener(OnDismissListener listener) {
        this.dismissListener = listener;
    }
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        // Call the listener when the dialog is dismissed
        if (dismissListener != null) {
            dismissListener.onDismiss();
        }
    }
    /**
     * Interface for listening to mood event added events.
     */

    public interface OnMoodAddedListener {
        void onMoodAdded(MoodEventModel moodEvent);
    }


    /**
     * Listener for mood event added events.
     */

    private OnMoodAddedListener moodAddedListener;


    /**
     * Sets the listener for mood event added events.
     *
     * @param listener the listener to set.
     */

    public void setOnMoodAddedListener(OnMoodAddedListener listener) {
        this.moodAddedListener = listener;
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI, or null.
     */

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_mood);

        dialog.setOnShowListener(dialogInterface -> {
            Window window = ((Dialog) dialogInterface).getWindow();
            if (window != null) {
                window.setBackgroundDrawableResource(R.drawable.dialog_background);
            }
        });

        return dialog;
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     */

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            if (window != null) {
                int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85);
                window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        }
    }


    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_mood, container, false);

        moodSpinner = view.findViewById(R.id.emotion_spinner);
        addMoodButton = view.findViewById(R.id.add_mood_button);
        selectImageButton = view.findViewById(R.id.camera_button);
        socialSituationSpinner = view.findViewById(R.id.social_situation_spinner);
        //imageView = view.findViewById(R.id.images_container);
        imagesScrollView = view.findViewById(R.id.images_scroll_view);
        imagesContainer = view.findViewById(R.id.images_container);
        addLocationCheckbox = view.findViewById(R.id.attach_location_checkbox);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());



        String[] moodArray = new String[]{
                "Happiness 😊", "Anger 😠", "Sadness 😢", "Fear 😨",
                "Calm 😌", "Confusion 😕", "Disgust 🤢", "Shame 😳", "Surprise 😮"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                moodArray
        );
        moodSpinner.setAdapter(adapter);

        String[] socialSituationArray = new String[]{
                "Alone", "With one person", "With two to several people", "With a crowd"
        };

        ArrayAdapter<String> socialAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                socialSituationArray
        );
        socialSituationSpinner.setAdapter(socialAdapter);

        if (isEditMode && moodToEdit != null) {
            // Change button text
            addMoodButton.setText("Save");

            // Description
            EditText descriptionInput = view.findViewById(R.id.description_input);
            descriptionInput.setText(moodToEdit.getDescription());

            // Trigger warning checkbox
            Switch triggerWarningCheckbox = view.findViewById(R.id.trigger_warning_checkbox);
            triggerWarningCheckbox.setChecked(moodToEdit.hasTriggerWarning());

            // Set mood spinner
            for (int i = 0; i < moodSpinner.getCount(); i++) {
                String moodItem = moodSpinner.getItemAtPosition(i).toString();
                if (moodItem.startsWith(moodToEdit.getEmotion())) {
                    moodSpinner.setSelection(i);
                    break;
                }
            }

            // Set social situation spinner
            for (int i = 0; i < socialSituationSpinner.getCount(); i++) {
                if (socialSituationSpinner.getItemAtPosition(i).toString().equalsIgnoreCase(moodToEdit.getSocialSituation())) {
                    socialSituationSpinner.setSelection(i);
                    break;
                }
            }

            // Location
            if (moodToEdit.HasLocation()) {
                addLocationCheckbox.setChecked(true);
                currentLatitude = moodToEdit.getLatitude();
                currentLongitude = moodToEdit.getLongitude();
                locationRetrieved = true;

                View mapContainer = view.findViewById(R.id.map_container);
                if (mapContainer != null) {
                    mapContainer.setVisibility(View.VISIBLE);
                }
                showUserLocation();
            }

            // Load image URLs
            List<String> imageUrls = moodToEdit.getImageUrls();
            if (imageUrls != null && !imageUrls.isEmpty()) {
                for (String url : imageUrls) {
                    loadImageFromUrl(url); // ⬅️ We'll define this next
                }
            }
        }

        requestCameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Permission granted, launch camera
                        launchCamera();
                    } else {
                        // Permission denied
                        Toast.makeText(requireContext(), "Camera permission is required to use camera", Toast.LENGTH_SHORT).show();
                    }
                });


        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getExtras() != null) {
                            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                            if (bitmap != null) {
                                addImageToGallery(bitmap);
                            }
                        }
                    }
                }
        );

        photoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                    requireContext().getContentResolver(), uri);
                            addImageToGallery(bitmap);
                        } catch (IOException e) {
                            Log.e(TAG, "Error loading image from URI", e);
                            Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d(TAG, "No media selected");
                    }
                }
        );


        requestGalleryPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Permission granted, launch photo picker
                        openPhotoPicker();
                    } else {
                        // Permission denied
                        Toast.makeText(requireContext(), "Allow permissions to access gallery", Toast.LENGTH_SHORT).show();
                    }
                }
        );



        // Set up image selection button
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSelectionDialog();
            }
        });

        addLocationCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            View mapContainer = view.findViewById(R.id.map_container);
            if (isChecked) {
                requestLocationPermission();
                if (mapContainer != null) {
                    mapContainer.setVisibility(View.VISIBLE);
                }
            } else {
                if (mapContainer != null) {
                    mapContainer.setVisibility(View.GONE);
                }
            }
        });

        // click listener for the add mood button
        addMoodButton.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                Log.e("Auth", "User not logged in! Cannot add/edit mood.");
                Toast.makeText(requireContext(), "Error: User not logged in!", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedMood = moodSpinner.getSelectedItem().toString();
            String moodName = selectedMood.split(" ")[0];
            Switch triggerWarningCheckbox = requireView().findViewById(R.id.trigger_warning_checkbox);
            boolean triggerWarning = triggerWarningCheckbox.isChecked();

            String socialSituation = socialSituationSpinner.getSelectedItem().toString();

            EditText descriptionInput = requireView().findViewById(R.id.description_input);
            String description = descriptionInput.getText().toString();

            boolean hasLocation = addLocationCheckbox.isChecked();
            double latitude = (hasLocation && locationRetrieved) ? currentLatitude : 0.0;
            double longitude = (hasLocation && locationRetrieved) ? currentLongitude : 0.0;

            if (isEditMode && moodToEdit != null && documentIdToUpdate != null) {
                // 🔁 EDIT MODE LOGIC
                MoodEventModel updatedMood = new MoodEventModel(
                        moodName,
                        description,
                        moodToEdit.getTimestamp(), // preserve original timestamp
                        getEmojiForEmotion(moodName),
                        getColorForEmotion(moodName),
                        triggerWarning,
                        hasLocation,
                        latitude,
                        longitude
                );
                updatedMood.setSocialSituation(socialSituation);
                updatedMood.setImageUrls(moodToEdit.getImageUrls()); // reuse old image URLs
                updatedMood.setUserId(moodToEdit.getUserId());       // reuse user ID

                DatabaseManager.getInstance().updateMoodEvent(updatedMood, documentIdToUpdate, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext(), "Mood updated successfully", Toast.LENGTH_SHORT).show();
                        dismiss(); // close the dialog
                    } else {
                        Toast.makeText(requireContext(), "Failed to update mood", Toast.LENGTH_SHORT).show();
                        Log.e("UpdateMood", "Error updating mood event", task.getException());
                    }
                });

            } else {
                // ➕ ADD MODE LOGIC (your original code)

                if (hasLocation) {
                    if (locationRetrieved) {
                        saveMoodEventWithLocationAndImage(moodName, description, triggerWarning, socialSituation, latitude, longitude, selectedImages);
                    } else {
                        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                                if (location != null) {
                                    saveMoodEventWithLocationAndImage(moodName, description, triggerWarning, socialSituation, location.getLatitude(), location.getLongitude(), selectedImages);
                                } else {
                                    saveMoodEventWithLocationAndImage(moodName, description, triggerWarning, socialSituation, 0, 0, selectedImages);
                                    Toast.makeText(requireContext(), "Could not get location", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(e -> {
                                Toast.makeText(requireContext(), "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                saveMoodEventWithLocationAndImage(moodName, description, triggerWarning, socialSituation, 0, 0, selectedImages);
                            });
                            return;
                        } else {
                            saveMoodEventWithLocationAndImage(moodName, description, triggerWarning, socialSituation, 0, 0, selectedImages);
                            Toast.makeText(requireContext(), "Location permission not granted", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    saveMoodEventWithLocationAndImage(moodName, description, triggerWarning, socialSituation, 0, 0, selectedImages);
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Move this call here to avoid getView() being null
        if (isEditMode && moodToEdit != null && moodToEdit.HasLocation()) {
            showUserLocation();
        }
    }

    private void loadImageFromUrl(String imageUrl) {
        ImageView imageView = new ImageView(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 16, 0, 16);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setAdjustViewBounds(true);

        Glide.with(requireContext()).load(imageUrl).into(imageView);
        imagesContainer.addView(imageView);
        imagesScrollView.setVisibility(View.VISIBLE);
    }

    private void showImageSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Image Source");
        builder.setItems(new CharSequence[]{"Camera", "Gallery"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // Camera
                        askCameraPermission();
                        break;
                    case 1: // Gallery
                        askGalleryPermission();
                        break;
                }
            }
        });
        builder.show();
    }

    private void addImageToGallery(Bitmap bitmap) {
        // Add to the collection
        selectedImages.add(bitmap);

        // Inflate the custom image layout
        View imageLayout = LayoutInflater.from(requireContext()).inflate(R.layout.item_image, imagesContainer, false);

        // Get references to views
        ImageView imageView = imageLayout.findViewById(R.id.image_view);
        ImageButton deleteButton = imageLayout.findViewById(R.id.btn_delete);

        // Set the image
        imageView.setImageBitmap(bitmap);

        // Set delete button click listener
        deleteButton.setOnClickListener(v -> {
            imagesContainer.removeView(imageLayout);
            selectedImages.remove(bitmap);
            if (selectedImages.isEmpty()) {
                imagesScrollView.setVisibility(View.GONE);
            }
        });

        // Add to the container
        imagesContainer.addView(imageLayout);

        // Make sure the scroll view is visible
        imagesScrollView.setVisibility(View.VISIBLE);
    }

    private void askCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            // Permission already granted, launch camera
            launchCamera();
        } else {
            // Request permission
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }
    private void launchCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            cameraLauncher.launch(cameraIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(), "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }



/*
private void askgalleryPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION_CODE);
        } else {
            openGallery();
        }
    }
*/


    private String getGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            return Manifest.permission.READ_EXTERNAL_STORAGE;
        }
    }

    private void askGalleryPermission() {
        String permission = getGalleryPermission();
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission) ==
                PackageManager.PERMISSION_GRANTED) {
            // Permission already granted, launch photo picker
            openPhotoPicker();
        } else {
            requestGalleryPermissionLauncher.launch(permission);
        }
    }
    private void openPhotoPicker() {
        PickVisualMediaRequest request = new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build();
        photoPickerLauncher.launch(request);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    private void saveMoodEventWithLocationAndImage(String moodName, String description,
                                                   boolean triggerWarning, String socialSituation,
                                                   double latitude, double longitude,
                                                   ArrayList<Bitmap> images) {
        boolean hasImages = images != null && !images.isEmpty();

        MoodEventModel newEvent = new MoodEventModel(
                moodName,
                description,
                getCurrentTimestamp(),
                getEmojiForEmotion(moodName),
                getColorForEmotion(moodName),
                triggerWarning,
                latitude != 0.0 || longitude != 0.0,
                latitude,
                longitude
        );

        newEvent.setSocialSituation(socialSituation);

        if (hasImages) {
            uploadImagesAndThenSave(images, newEvent); // ⬅ this handles everything
        } else {
            DatabaseManager.getInstance().addMoodEvent(newEvent);
            finishAddMood(newEvent);
        }
    }

    private void uploadImagesAndThenSave(ArrayList<Bitmap> images, MoodEventModel moodEvent) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        ArrayList<String> uploadedUrls = new ArrayList<>();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        for (int i = 0; i < images.size(); i++) {
            Bitmap bitmap = images.get(i);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] data = baos.toByteArray();

            String filename = "mood_images/" + userId + "/" + System.currentTimeMillis() + "_img" + i + ".jpg";
            StorageReference imgRef = storageRef.child(filename);

            UploadTask uploadTask = imgRef.putBytes(data);
            int finalI = i;

            uploadTask
                    .addOnSuccessListener(taskSnapshot -> {
                        // Now safe to request download URL
                        imgRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    uploadedUrls.add(uri.toString());

                                    if (uploadedUrls.size() == images.size()) {
                                        moodEvent.setImageUrls(uploadedUrls);
                                        DatabaseManager.getInstance().addMoodEvent(moodEvent);
                                        finishAddMood(moodEvent);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to get download URL for image " + finalI, e);
                                    Toast.makeText(requireContext(), "Failed to get image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Upload failed for image " + finalI, e);
                        Toast.makeText(requireContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
    private void finishAddMood(MoodEventModel moodEvent) {
        Toast.makeText(requireContext(), "Mood Added Successfully!", Toast.LENGTH_SHORT).show();
        if (moodAddedListener != null) {
            moodAddedListener.onMoodAdded(moodEvent);
        }
        dismiss();
    }
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        } else {
            showUserLocation();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showUserLocation();
            } else {
                addLocationCheckbox.setChecked(false);
            }
        } else if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(requireContext(), "Camera permission is required to use camera.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == GALLERY_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(requireContext(), "Storage permission is required to access gallery.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Shows the user's current location on the map.
     */

    private void showUserLocation() {
        Switch locationCheckbox = getView().findViewById(R.id.attach_location_checkbox);
        if (locationCheckbox != null && locationCheckbox.isChecked()) { // ✅ Only show map if checked
            View mapContainer = getView().findViewById(R.id.map_container);
            if (mapContainer != null) {
                mapContainer.setVisibility(View.VISIBLE);
            }

            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentByTag("map_fragment");

            if (mapFragment == null) {
                mapFragment = SupportMapFragment.newInstance();
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.map_container, mapFragment, "map_fragment")
                        .commit();
            }

            mapFragment.getMapAsync(googleMap -> {
                this.googleMap = googleMap;
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                        if (location != null) {
                            currentLatitude = location.getLatitude();
                            currentLongitude = location.getLongitude();
                            locationRetrieved = true;

                            LatLng userLocation = new LatLng(currentLatitude, currentLongitude);
                            googleMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                        }
                    });
                }
            });
        } else {
            Log.d(TAG, "Location checkbox not checked, skipping map setup.");
        }
    }



    /**
     * Returns the current timestamp in the format "yyyy-MM-dd HH:mm".
     *
     * @return the current timestamp.
     */

    private Date getCurrentTimestamp() {
        return new Date();
    }


    /**
     * Returns the emoji corresponding to the given emotion.
     *
     * @param emotion the emotion to get the emoji for.
     * @return the emoji corresponding to the given emotion.
     */

    private String getEmojiForEmotion(String emotion) {
        switch (emotion) {
            case "Happiness": return "😊";
            case "Anger": return "😠";
            case "Sadness": return "😢";
            case "Fear": return "😨";
            case "Calm": return "😌";
            case "Confusion": return "😕";
            case "Disgust": return "🤢";
            case "Shame": return "😳";
            case "Surprise": return "😮";
            default: return "😐";
        }
    }


    /**
     * Returns the color resource ID corresponding to the given emotion.
     * @param emotion the emotion to get the color for
     * @return the color resource ID
     */

    private int getColorForEmotion(String emotion) {
        switch (emotion) {
            case "Happiness": return R.color.color_happiness;
            case "Anger": return R.color.color_anger;
            case "Sadness": return R.color.color_sadness;
            case "Fear": return R.color.color_fear;
            case "Calm": return R.color.color_calm;
            case "Confusion": return R.color.color_confusion;
            case "Disgust": return R.color.color_disgust;
            case "Shame": return R.color.color_shame;
            case "Surprise": return R.color.color_surprise;
            default: return R.color.colorPrimaryDark;
        }
    }
    public void setMoodToEdit(MoodEventModel mood, String documentId) {
        this.moodToEdit = mood;
        this.isEditMode = true;
        this.documentIdToUpdate = documentId;
    }
}