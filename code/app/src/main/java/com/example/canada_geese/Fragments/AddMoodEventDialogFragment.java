package com.example.canada_geese.Fragments;
import static android.graphics.Insets.add;

import static androidx.core.util.TypedValueCompat.dpToPx;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.TextView;
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
import com.example.canada_geese.Managers.OfflineDatabase;
import com.example.canada_geese.Models.MoodEventModel;
import com.example.canada_geese.Models.PendingMoodEvent;
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
import java.io.File;
import java.io.FileOutputStream;
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
    private ArrayList<String> retainedImageUrls = new ArrayList<>();

    private HorizontalScrollView imagesScrollView;
    private LinearLayout imagesContainer;

    private static final int LOCATION_PERMISSION_REQUEST = 100;
    private ActivityResultLauncher<PickVisualMediaRequest> photoPickerLauncher;
    private ActivityResultLauncher<String> requestGalleryPermissionLauncher;

    private MoodEventModel moodToEdit = null;
    private boolean isEditMode = false;
    private String documentIdToUpdate = null;
    private ImageView closeButton;

    // Interface for listening to dialog dismiss events
    public interface OnDismissListener {
        void onDismiss();
    }
    private OnDismissListener dismissListener;

    /**
     * Sets the listener for dialog dismiss events.
     *
     * @param listener the listener to set.
     */
    public void setOnDismissListener(OnDismissListener listener) {
        this.dismissListener = listener;
    }

    /**
     * Called when the dialog is dismissed.
     * @param dialog the dialog that was dismissed will be passed into the
     *               method
     */
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
        closeButton = view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> dismiss());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        EditText descriptionInput = view.findViewById(R.id.description_input);
        TextView descriptionCounter = view.findViewById(R.id.description_counter);
        setupDescriptionCharacterCounter(descriptionInput, descriptionCounter);

        String[] moodArray = new String[]{
                "Happy 😊", "Angry 😠", "Sad 😢", "Scared 😨",
                "Calm 😌", "Confused 😕", "Disgusted 🤢", "Ashamed 😳", "Surprised 😮"
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
            descriptionInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    int currentLength = s.length();
                    if (currentLength > 200) {
                        descriptionInput.setText(s.subSequence(0, 200));
                        descriptionInput.setSelection(200); // Move cursor to end
                        Toast.makeText(requireContext(), "Max 200 characters allowed", Toast.LENGTH_SHORT).show();
                    } else {
                        descriptionCounter.setText(currentLength + " / 200");
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {}
            });
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
                showUserLocation(view);
            }

            // Load image URLs
            List<String> imageUrls = moodToEdit.getImageUrls();
            if (imageUrls != null && !imageUrls.isEmpty()) {
                retainedImageUrls.clear(); // Reset when editing
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
                requestLocationPermission(view);
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

            //EditText descriptionInput = requireView().findViewById(R.id.description_input);
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

                ArrayList<String> finalImageUrls = new ArrayList<>(retainedImageUrls); // Keep retained
                uploadImagesThenUpdateMood(updatedMood);

            } else {
                // ➕ ADD MODE LOGIC (your original code)
                ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnected();

                if (isConnected) {
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
                } else {
                    // 📥 Offline mode: save locally
                    saveMoodLocally(moodName, description, triggerWarning, socialSituation, latitude, longitude, selectedImages);
                }
            }
        });




        return view;
    }

    private void saveMoodLocally(String moodName, String description, boolean triggerWarning, String socialSituation,
                                 double latitude, double longitude, ArrayList<Bitmap> images) {
        List<String> imagePaths = new ArrayList<>();

        for (Bitmap bitmap : images) {
            String filename = "mood_" + System.currentTimeMillis() + ".jpg";
            File file = new File(requireContext().getFilesDir(), filename);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                imagePaths.add(file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        PendingMoodEvent event = new PendingMoodEvent();
        event.emotion = moodName;
        event.description = description;
        event.emoji = getEmojiForEmotion(moodName);
        event.color = getColorForEmotion(moodName);
        event.isPrivate = triggerWarning;
        event.hasLocation = (latitude != 0 || longitude != 0);
        event.latitude = latitude;
        event.longitude = longitude;
        event.socialSituation = socialSituation;
        event.timestamp = new Date().getTime();
        event.imagePaths = imagePaths;

        new Thread(() -> {
            OfflineDatabase.getInstance(requireContext()).pendingMoodDao().insert(event);
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Mood saved offline. Will upload when back online.", Toast.LENGTH_LONG).show();
                dismiss();
            });
        }).start();
    }

    /**
     * Called when the view is created.
     *
     * @param view The view created.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Move this call here to avoid getView() being null
        if (isEditMode && moodToEdit != null && moodToEdit.HasLocation()) {
            showUserLocation(view);;
        }
    }

    /**
     * Sets up a character counter for the description input field.
     *
     * @param descriptionInput The EditText for the description input.
     * @param descriptionCounter The TextView for displaying the character count.
     */
    private void setupDescriptionCharacterCounter(EditText descriptionInput, TextView descriptionCounter) {
        descriptionInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int currentLength = s.length();
                if (currentLength > 200) {
                    descriptionInput.setText(s.subSequence(0, 200));
                    descriptionInput.setSelection(200);
                    Toast.makeText(requireContext(), "Max 200 characters allowed", Toast.LENGTH_SHORT).show();
                } else {
                    descriptionCounter.setText(currentLength + " / 200");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * Uploads images to Firebase Storage and updates the mood event in the database.
     *
     * @param updatedMood The updated mood event model.
     */
    private void uploadImagesThenUpdateMood(MoodEventModel updatedMood) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        ArrayList<String> finalUrls = new ArrayList<>(retainedImageUrls);

        if (selectedImages.isEmpty()) {
            updatedMood.setImageUrls(finalUrls);
            updateMoodInDatabase(updatedMood);
            return;
        }

        ArrayList<String> uploadedUrls = new ArrayList<>();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        for (int i = 0; i < selectedImages.size(); i++) {
            Bitmap bitmap = selectedImages.get(i);
            byte[] data = compressBitmapToLimit(bitmap, 65536);

            String filename = "mood_images/" + userId + "/" + System.currentTimeMillis() + "_edit" + i + ".jpg";
            StorageReference imgRef = storageRef.child(filename);

            int finalI = i;
            imgRef.putBytes(data)
                    .addOnSuccessListener(taskSnapshot ->
                            imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                uploadedUrls.add(uri.toString());
                                if (uploadedUrls.size() == selectedImages.size()) {
                                    finalUrls.addAll(uploadedUrls);
                                    updatedMood.setImageUrls(finalUrls);
                                    updateMoodInDatabase(updatedMood);
                                }
                            }))
                    .addOnFailureListener(e ->
                            Toast.makeText(requireContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        }
    }

    /**
     * Updates the mood event in the database.
     *
     * @param mood The mood event model to update.
     */
    private void updateMoodInDatabase(MoodEventModel mood) {
        DatabaseManager.getInstance().updateMoodEvent(mood, documentIdToUpdate, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireContext(), "Mood updated successfully", Toast.LENGTH_SHORT).show();
                dismiss();
            } else {
                Toast.makeText(requireContext(), "Failed to update mood", Toast.LENGTH_SHORT).show();
                Log.e("UpdateMood", "Error updating mood event", task.getException());
            }
        });
    }

    /**
     * Loads an image from a URL and adds it to the images container.
     *
     * @param imageUrl The URL of the image to load.
     */
    private void loadImageFromUrl(String imageUrl) {
        retainedImageUrls.add(imageUrl); // Track what's kept

        View imageLayout = LayoutInflater.from(requireContext()).inflate(R.layout.item_image, imagesContainer, false);
        ImageView imageView = imageLayout.findViewById(R.id.image_view);
        ImageButton deleteButton = imageLayout.findViewById(R.id.btn_delete);

        Glide.with(requireContext()).load(imageUrl).into(imageView);

        deleteButton.setOnClickListener(v -> {
            imagesContainer.removeView(imageLayout);
            retainedImageUrls.remove(imageUrl);
            if (imagesContainer.getChildCount() == 0) {
                imagesScrollView.setVisibility(View.GONE);
            }
        });

        imagesContainer.addView(imageLayout);
        imagesScrollView.setVisibility(View.VISIBLE);
    }

    /**
     * Shows a dialog for selecting the image source (camera or gallery).
     */
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

    /**
     * Adds the selected image to the gallery.
     *
     * @param bitmap The selected image as a Bitmap.
     */
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

    /**
     * Asks for camera permission.
     */
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

    /**
     * Launches the camera to take a photo.
     */
    private void launchCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            cameraLauncher.launch(cameraIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(), "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Asks for gallery permission.
     */
    private String getGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            return Manifest.permission.READ_EXTERNAL_STORAGE;
        }
    }

    /**
     * Asks for gallery permission and opens the photo picker.
     */
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
    /**
     * Opens the photo picker to select an image.
     */
    private void openPhotoPicker() {
        PickVisualMediaRequest request = new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build();
        photoPickerLauncher.launch(request);
    }
    /**
     * Opens the camera to take a photo.
     */
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }
    /**
     * Opens the gallery to select an image.
     */
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }
    /**
     * Saves the mood event with location and image.
     *
     * @param moodName The name of the mood.
     * @param description The description of the mood.
     * @param triggerWarning Whether to trigger a warning.
     * @param socialSituation The social situation.
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @param images The list of images.
     */
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

    /**
     * Uploads images to Firebase Storage and then saves the mood event.
     *
     * @param images The list of images to upload.
     * @param moodEvent The mood event model to save.
     */
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
            byte[] data = compressBitmapToLimit(bitmap, 65536); // 64 KB

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

    /**
     * Finishes adding the mood event and notifies the listener.
     *
     * @param moodEvent The mood event model that was added.
     */
    private void finishAddMood(MoodEventModel moodEvent) {
        Toast.makeText(requireContext(), "Mood Added Successfully!", Toast.LENGTH_SHORT).show();
        if (moodAddedListener != null) {
            moodAddedListener.onMoodAdded(moodEvent);
        }
        dismiss();
    }

    /**
     * Requests location permission from the user.
     *
     * @param rootView The root view of the fragment.
     */
    private void requestLocationPermission(View rootView) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        } else {
            showUserLocation(rootView);
        }
    }

    /**
     * Called when the user interacts with the dialog.
     *
     * @param requestCode The request code passed in requestPermissions().
     * @param permissions The requested permissions.
     * @param grantResults The results of the permission requests.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showUserLocation(requireView());
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
    private void showUserLocation(View rootView) {
        Switch locationCheckbox = rootView.findViewById(R.id.attach_location_checkbox);
        if (locationCheckbox != null && locationCheckbox.isChecked()) { // ✅ Only show map if checked
            View mapContainer = rootView.findViewById(R.id.map_container);
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
            case "Happy": return "😊";
            case "Angry": return "😠";
            case "Sad": return "😢";
            case "Scared": return "😨";
            case "Calm": return "😌";
            case "Confused": return "😕";
            case "Disgusted": return "🤢";
            case "Ashamed": return "😳";
            case "Surprised": return "😮";
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
            case "Happy": return R.color.color_happiness;
            case "Angry": return R.color.color_anger;
            case "Sad": return R.color.color_sadness;
            case "Scared": return R.color.color_fear;
            case "Calm": return R.color.color_calm;
            case "Confused": return R.color.color_confusion;
            case "Disgusted": return R.color.color_disgust;
            case "Ashamed": return R.color.color_shame;
            case "Surprised": return R.color.color_surprise;
            default: return R.color.colorPrimaryDark;
        }
    }

    /**
     * Sets the mood event to edit.
     *
     * @param mood The mood event model to edit.
     * @param documentId The document ID of the mood event in the database.
     */
    public void setMoodToEdit(MoodEventModel mood, String documentId) {
        this.moodToEdit = mood;
        this.isEditMode = true;
        this.documentIdToUpdate = documentId;
    }

    /**
     * Compresses a bitmap to a specified byte limit.
     *
     * @param bitmap The bitmap to compress.
     * @param maxBytes The maximum size in bytes.
     * @return The compressed bitmap as a byte array.
     */
    private byte[] compressBitmapToLimit(Bitmap bitmap, int maxBytes) {
        int quality = 100;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);

        while (baos.toByteArray().length > maxBytes && quality > 10) {
            baos.reset();
            quality -= 5;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        }

        return baos.toByteArray();
    }
}