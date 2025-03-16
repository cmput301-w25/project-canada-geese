package com.example.canada_geese.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
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

/**
 * A dialog fragment that allows the user to add a new mood event.
 */
public class AddMoodEventDialogFragment extends DialogFragment {
    private Spinner moodSpinner;
    private Spinner socialSituationSpinner;
    private EditText descriptionInput;
    private Button addMoodButton;
    private CheckBox privateMoodCheckbox;
    private CheckBox addLocationCheckbox;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap googleMap;
    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;

    private static final int LOCATION_PERMISSION_REQUEST = 100;
    private static final String TAG = "AddMoodEventDialog";

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

        // Initialize views
        moodSpinner = view.findViewById(R.id.emotion_spinner);
        socialSituationSpinner = view.findViewById(R.id.social_situation_spinner);
        descriptionInput = view.findViewById(R.id.description_input);
        addMoodButton = view.findViewById(R.id.add_mood_button);
        privateMoodCheckbox = view.findViewById(R.id.private_mood_checkbox);
        addLocationCheckbox = view.findViewById(R.id.attach_location_checkbox);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Set up the mood spinner
        String[] moodArray = new String[]{
                "Happiness 😊", "Anger 😠", "Sadness 😢", "Fear 😨",
                "Calm 😌", "Confusion 😕", "Disgust 🤢", "Shame 😳", "Surprise 😮"
        };

        ArrayAdapter<String> moodAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                moodArray
        );
        moodSpinner.setAdapter(moodAdapter);

        // Set up the social situation spinner
        String[] socialSituationArray = new String[]{
                "Alone", "With one person", "With two to several people", "With a crowd"
        };

        ArrayAdapter<String> socialAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                socialSituationArray
        );
        socialSituationSpinner.setAdapter(socialAdapter);

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

        // Click listener for the add mood button
        addMoodButton.setOnClickListener(v -> {
            addMoodEvent();
        });

        return view;
    }

    /**
     * Adds the mood event to the database.
     */
    private void addMoodEvent() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String selectedMood = moodSpinner.getSelectedItem().toString();
            String moodName = selectedMood.split(" ")[0];

            final boolean hasLocation = addLocationCheckbox.isChecked();
            final boolean isPrivate = privateMoodCheckbox.isChecked();

            final String description = descriptionInput.getText().toString().trim();
            Log.d(TAG, "Description entered: " + description);

            final String finalDescription = description.isEmpty() ? "No description provided" : description;

            if (hasLocation) {
                // If we have permission and location is enabled, use the current location
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                        if (location != null) {
                            currentLatitude = location.getLatitude();
                            currentLongitude = location.getLongitude();
                        }
                        createAndSaveMoodEvent(moodName, finalDescription, hasLocation, isPrivate);
                    });
                } else {
                    // Default coordinates if permission not granted
                    createAndSaveMoodEvent(moodName, finalDescription, hasLocation, isPrivate);
                }
            } else {
                createAndSaveMoodEvent(moodName, finalDescription, false, isPrivate);
            }
        } else {
            Log.e("Auth", "User not logged in! Cannot add mood.");
            Toast.makeText(requireContext(), "Error: User not logged in!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Creates and saves a mood event with the provided information.
     *
     * @param moodName The name of the mood.
     * @param description The description of the mood event.
     * @param hasLocation Whether the mood event has location data.
     * @param isPrivate Whether the mood event is private.
     */
    private void createAndSaveMoodEvent(String moodName, String description, boolean hasLocation, boolean isPrivate) {
        // Create a new mood event with all the required information
        MoodEventModel newEvent = new MoodEventModel(
                moodName,                  // emotion
                description,               // description
                getCurrentTimestamp(),     // timestamp
                getEmojiForEmotion(moodName), // emoji
                getColorForEmotion(moodName), // color
                isPrivate,                 // isPrivate
                hasLocation,               // hasLocation
                currentLatitude,           // latitude
                currentLongitude           // longitude
        );

        // Log the mood event details before saving
        Log.d(TAG, "Creating mood event: " + moodName +
                ", Description: " + description +
                ", Private: " + isPrivate +
                ", HasLocation: " + hasLocation);

        // Save the mood event to the database
        DatabaseManager.getInstance().addMoodEvent(newEvent);
        Toast.makeText(requireContext(), "Mood Added Successfully!", Toast.LENGTH_SHORT).show();

        // Notify the listener if one is set
        if (moodAddedListener != null) {
            moodAddedListener.onMoodAdded(newEvent);
        }

        // Dismiss the dialog
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
     *
     * @param requestCode The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
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
        }
    }

    /**
     * Shows the user's current location on the map.
     */
    private void showUserLocation() {
        View mapContainer = getView() != null ? getView().findViewById(R.id.map_container) : null;
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

                        LatLng userLocation = new LatLng(currentLatitude, currentLongitude);
                        googleMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                    }
                });
            }
        });
    }

    /**
     * Returns the current timestamp in the format "yyyy-MM-dd HH:mm".
     *
     * @return the current timestamp.
     */
    private String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
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
     *
     * @param emotion the emotion to get the color for.
     * @return the color resource ID corresponding to the given emotion.
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