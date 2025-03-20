/*
package com.example.canada_geese.Fragments;

import static android.graphics.Insets.add;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

*/
/**
 * A dialog fragment that allows the user to add a new mood event.
 *//*

public class AddMoodEventDialogFragment extends DialogFragment {
    private Spinner moodSpinner;
    private Button addMoodButton;
    private CheckBox addLocationCheckbox;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap googleMap;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int GALLERY_PERMISSION_CODE = 200;
    private static final int GALLERY_REQUEST_CODE = 201;
    private ImageView imageView;

    // Add these class variables to store the current location
    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;
    private boolean locationRetrieved = false;

    private static final int LOCATION_PERMISSION_REQUEST = 100;

    */
/**
     * Interface for listening to mood event added events.
     *//*

    public interface OnMoodAddedListener {
        void onMoodAdded(MoodEventModel moodEvent);
    }

    */
/**
     * Listener for mood event added events.
     *//*

    private OnMoodAddedListener moodAddedListener;

    */
/**
     * Sets the listener for mood event added events.
     *
     * @param listener the listener to set.
     *//*

    public void setOnMoodAddedListener(OnMoodAddedListener listener) {
        this.moodAddedListener = listener;
    }
    */
/**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI, or null.
     *//*

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
    */
/**
     * Called when the fragment is visible to the user and actively running.
     *//*

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

    */
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
     *//*

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_mood, container, false);

        moodSpinner = view.findViewById(R.id.emotion_spinner);
        addMoodButton = view.findViewById(R.id.add_mood_button);
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

        addLocationCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            View mapContainer = getView().findViewById(R.id.map_container);
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

        // Modified click listener for the add mood button
        addMoodButton.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String selectedMood = moodSpinner.getSelectedItem().toString();
                String moodName = selectedMood.split(" ")[0];
                boolean hasLocation = addLocationCheckbox.isChecked();
                double latitude = 0.0;
                double longitude = 0.0;
                String description = "placeholder";

                if (hasLocation) {
                    // Use the stored location values instead of hardcoded ones
                    if (locationRetrieved) {
                        latitude = currentLatitude;
                        longitude = currentLongitude;

                        // Create and save the mood event with the retrieved location
                        saveMoodEventWithLocation(moodName, description, latitude, longitude);
                    } else {
                        // If location wasn't retrieved yet, try to get it now
                        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                                if (location != null) {
                                    // Create and save the mood event with the retrieved location
                                    saveMoodEventWithLocation(moodName, description, location.getLatitude(), location.getLongitude());
                                } else {
                                    // Create and save the mood event without location
                                    saveMoodEventWithLocation(moodName, description, 0.0, 0.0);
                                    Toast.makeText(requireContext(), "Could not get location", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(e -> {
                                // Handle location retrieval failure
                                Toast.makeText(requireContext(), "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                saveMoodEventWithLocation(moodName, description, 0.0, 0.0);
                            });
                            return; // Return early as we're handling the save in the callback
                        } else {
                            // No location permission, save without location
                            saveMoodEventWithLocation(moodName, description, 0.0, 0.0);
                            Toast.makeText(requireContext(), "Location permission not granted", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // No location requested, save without location
                    saveMoodEventWithLocation(moodName, description, 0.0, 0.0);
                }
            } else {
                Log.e("Auth", "User not logged in! Cannot add mood.");
                Toast.makeText(requireContext(), "Error: User not logged in!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    */
/**
     * Helper method to save mood event with location
     *//*

    private void saveMoodEventWithLocation(String moodName, String description, double latitude, double longitude) {
        MoodEventModel newEvent = new MoodEventModel(
                moodName,
                description,
                getCurrentTimestamp(),
                getEmojiForEmotion(moodName),
                getColorForEmotion(moodName),
                false,
                latitude != 0.0 || longitude != 0.0, // Only set hasLocation true if we have valid coordinates
                latitude,
                longitude
        );
        DatabaseManager.getInstance().addMoodEvent(newEvent);
        Toast.makeText(requireContext(), "Mood Added Successfully!", Toast.LENGTH_SHORT).show();

        if (moodAddedListener != null) {
            moodAddedListener.onMoodAdded(newEvent);
        }
        dismiss();
    }

    */
/**
     * Requests the location permission from the user.
     *//*

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        } else {
            showUserLocation();
        }
    }

    */
/**
     *
     * @param requestCode The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     *//*

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showUserLocation();
            } else {
                addLocationCheckbox.setChecked(false);
            }
        }
    }

    */
/**
     * Shows the user's current location on the map.
     *//*

    private void showUserLocation() {
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
                        // Store the location in class variables
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
    }

    */
/**
     * Returns the current timestamp in the format "yyyy-MM-dd HH:mm".
     *
     * @return the current timestamp.
     *//*

    private Date getCurrentTimestamp() {
        return new Date();
    }

    */
/**
     * Returns the emoji corresponding to the given emotion.
     *
     * @param emotion the emotion to get the emoji for.
     * @return the emoji corresponding to the given emotion.
     *//*

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

    */
/**
     *
     * @param emotion
     * @return
     *//*

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
}*/


package com.example.canada_geese.Fragments;

import static android.graphics.Insets.add;

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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A dialog fragment that allows the user to add a new mood event.
 */
public class AddMoodEventDialogFragment extends DialogFragment {
    private Spinner moodSpinner;
    private Button addMoodButton;
    private Button selectImageButton;
    private CheckBox addLocationCheckbox;
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

    private static final int LOCATION_PERMISSION_REQUEST = 100;

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
        imageView = view.findViewById(R.id.image_view);
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
                            selectedImage = (Bitmap) data.getExtras().get("data");
                            if (selectedImage != null) {
                                imageView.setVisibility(View.VISIBLE);
                                imageView.setImageBitmap(selectedImage);
                            }
                        }
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
            if (currentUser != null) {
                String selectedMood = moodSpinner.getSelectedItem().toString();
                String moodName = selectedMood.split(" ")[0];
                boolean hasLocation = addLocationCheckbox.isChecked();
                double latitude = 0.0;
                double longitude = 0.0;
                String description = "placeholder";

                if (hasLocation) {
                    // Use the stored location values instead of hardcoded ones
                    if (locationRetrieved) {
                        latitude = currentLatitude;
                        longitude = currentLongitude;

                        // Create and save the mood event with the retrieved location
                        saveMoodEventWithLocationAndImage(moodName, description, latitude, longitude, selectedImage);
                    } else {
                        // If location wasn't retrieved yet, try to get it now
                        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                                if (location != null) {
                                    // Create and save the mood event with the retrieved location
                                    saveMoodEventWithLocationAndImage(moodName, description, location.getLatitude(), location.getLongitude(), selectedImage);
                                } else {
                                    // Create and save the mood event without location
                                    saveMoodEventWithLocationAndImage(moodName, description, 0.0, 0.0, selectedImage);
                                    Toast.makeText(requireContext(), "Could not get location", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(e -> {
                                // Handle location retrieval failure
                                Toast.makeText(requireContext(), "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                saveMoodEventWithLocationAndImage(moodName, description, 0.0, 0.0, selectedImage);
                            });
                            return;
                        } else {
                            // No location permission, save without location
                            saveMoodEventWithLocationAndImage(moodName, description, 0.0, 0.0, selectedImage);
                            Toast.makeText(requireContext(), "Location permission not granted", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // No location requested, save without location
                    saveMoodEventWithLocationAndImage(moodName, description, 0.0, 0.0, selectedImage);
                }
            } else {
                Log.e("Auth", "User not logged in! Cannot add mood.");
                Toast.makeText(requireContext(), "Error: User not logged in!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    /**
     * Shows a dialog to select between camera and gallery
     */
    private void showImageSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Image Source");
        builder.setItems(new CharSequence[]{"Camera", "Gallery"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // Camera option
                        askCameraPermission();
                        break;
                    case 1: // Gallery option
                        askGalleryPermission();
                        break;
                }
            }
        });
        builder.show();
    }

    /**
     * Requests camera permission and opens camera if granted
     */
   /* private void askCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }*/
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


    /**
     * Requests gallery permission and opens gallery if granted
     */
    private void askGalleryPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION_CODE);
        } else {
            openGallery();
        }
    }

    /**
     * Opens the device camera
     */
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }
    /*private ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getExtras() != null) {
                        selectedImage = (Bitmap) data.getExtras().get("data");
                        if (selectedImage != null) {
                            imageView.setVisibility(View.VISIBLE);
                            imageView.setImageBitmap(selectedImage);
                        }
                    }
                }
            }
    );

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            cameraLauncher.launch(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(), "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }*/

    /**
     * Opens the device gallery
     */
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    /**
     * Helper method to save mood event with location and image
     */
    private void saveMoodEventWithLocationAndImage(String moodName, String description, double latitude, double longitude, Bitmap image) {
        // You'll need to modify your MoodEventModel to include an image field
        // For now, we'll assume you've added this functionality

        MoodEventModel newEvent = new MoodEventModel(
                moodName,
                description,
                getCurrentTimestamp(),
                getEmojiForEmotion(moodName),
                getColorForEmotion(moodName),
                image != null, // hasImage flag
                latitude != 0.0 || longitude != 0.0, // Only set hasLocation true if we have valid coordinates
                latitude,
                longitude
                // You might need to add the image to your model or save it separately
        );

        // If your DatabaseManager supports saving images, you'll need to handle that here
        DatabaseManager.getInstance().addMoodEvent(newEvent);
        Toast.makeText(requireContext(), "Mood Added Successfully!", Toast.LENGTH_SHORT).show();

        if (moodAddedListener != null) {
            moodAddedListener.onMoodAdded(newEvent);
        }
        dismiss();
    }

    /**
     * Requests the location permission from the user.
     */
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        } else {
            showUserLocation();
        }
    }

    /**
     * Handle permission results for camera, gallery, and location
     * @param requestCode The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     */
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
     * Handle results from camera or gallery intents
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    this.selectedImage = (Bitmap) extras.get("data");
                    if (this.selectedImage != null) {
                        ImageView imageView = getView().findViewById(R.id.image_view);
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setImageBitmap(this.selectedImage);
                    }
                }

            } else if (requestCode == GALLERY_REQUEST_CODE && data != null) {
                // Gallery image is returned as a URI
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    ImageView imageView = getView().findViewById(R.id.image_view);
                    imageView.setVisibility(View.VISIBLE); // Show the image view
                    imageView.setImageURI(selectedImageUri); // Set the gallery image to the ImageView
                }
            }
        } else {
            Log.w(TAG, "Result code was not OK: " + resultCode);
        }
    }



    /**
     * Shows the user's current location on the map.
     */
    private void showUserLocation() {
        CheckBox locationCheckbox = getView().findViewById(R.id.attach_location_checkbox);
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
}