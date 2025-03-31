package com.example.canada_geese.Pages;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.canada_geese.Adapters.MoodEventAdapter;
import com.example.canada_geese.Fragments.FilterBarFragment;
import com.example.canada_geese.Managers.DatabaseManager;
import com.example.canada_geese.Models.MoodEventModel;
import com.example.canada_geese.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentSnapshot;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * A fragment that displays a map view with mood event markers.
 */
public class fragment_map_view_page extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Chip memoryButton;
    private Chip friendsButton;
    private List<MoodEventModel> moodEventList;
    private List<MoodEventModel> friendsMoodEventList;
    private Map<String, String> usernamesMap = new HashMap<>();
    private MoodEventAdapter adapter;
    private Set<String> selectedMoods = new HashSet<>();
    private Chip happinessChip;
    private Chip sadnessChip;
    private Chip shameChip;
    private Chip surpriseChip;
    private Chip fearChip;
    private Chip disgustChip;
    private Chip confusionChip;
    private Chip angerChip;
    private boolean isFriendsMode = false;
    private LinearLayout filterButton;
    private HorizontalScrollView filterScrollView;
    private ChipGroup filterChipGroup;
    private double currentUserLatitude = 0.0;
    private double currentUserLongitude = 0.0;
    private boolean currentLocationAvailable = false;
    private SearchView searchView;

    /**
     * Required empty public constructor.
     */
    public fragment_map_view_page() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of this fragment.
     *
     * @return A new instance of fragment_map_view_page.
     */
    public static fragment_map_view_page newInstance() {
        return new fragment_map_view_page();
    }

    /**
     * Inflates the layout for this fragment and initializes views.
     *
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map_view_page, container, false);
        List<MoodEventModel> moodEventList = new ArrayList<>();
        adapter = new MoodEventAdapter(moodEventList, getContext(), false);


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        memoryButton = rootView.findViewById(R.id.btnMemory);
        memoryButton.setOnClickListener(v -> onMemoryButtonClick());
        friendsButton = rootView.findViewById(R.id.btnFriends);
        friendsButton.setOnClickListener(v -> onFriendsButtonClick());
        searchView = rootView.findViewById(R.id.search_view);

        //Filter Logic:
        happinessChip = rootView.findViewById(R.id.chip_mood_happiness);
        sadnessChip = rootView.findViewById(R.id.chip_mood_sadness);
        shameChip = rootView.findViewById(R.id.chip_mood_shame);
        surpriseChip = rootView.findViewById(R.id.chip_mood_surprise);
        fearChip = rootView.findViewById(R.id.chip_mood_fear);
        disgustChip = rootView.findViewById(R.id.chip_mood_disgust);
        confusionChip = rootView.findViewById(R.id.chip_mood_confusion);
        angerChip = rootView.findViewById(R.id.chip_mood_anger);
        filterButton = rootView.findViewById(R.id.filter_button);
        filterScrollView = rootView.findViewById(R.id.filter_scroll_view);
        filterChipGroup = rootView.findViewById(R.id.filter_chip_group);

        setupChipListener(happinessChip, "Happy");
        setupChipListener(sadnessChip, "Sad");
        setupChipListener(shameChip, "Ashamed");
        setupChipListener(surpriseChip, "Surprised");
        setupChipListener(fearChip, "Scared");
        setupChipListener(disgustChip, "Disgusted");
        setupChipListener(confusionChip, "Confused");
        setupChipListener(angerChip, "Angry");

        // FILTER ICON (triggers the chips)
        filterButton.setOnClickListener(v -> toggleFilterVisibility());

        // CLEAR ALL
        Chip clearAllChip = rootView.findViewById(R.id.chip_clear_all);
        clearAllChip.setOnClickListener(v -> clearAllFilters());

        setupSearchView();

        return rootView;
    }

    /**
     * Sets up the listener for mood chips to update the selected moods.
     *
     * @param chip The Chip view.
     * @param mood The mood label.
     */
    private void setupChipListener(Chip chip, String mood) {
        chip.setOnCheckedChangeListener((buttonView, isChecked) -> updateMoodFilter(mood, isChecked));
    }

    /**
     * Toggles the visibility of the filter scroll view
     */
    private void toggleFilterVisibility() {
        boolean isSelected = filterButton.isSelected();
        filterButton.setSelected(!isSelected);
        Log.d("FilterDebug", "Current visibility: " + filterScrollView.getVisibility());
        if (filterScrollView.getVisibility() == View.GONE) {
            filterScrollView.setVisibility(View.VISIBLE);
            Log.d("FilterDebug", "Setting to VISIBLE");
        } else {
            filterScrollView.setVisibility(View.GONE);
            Log.d("FilterDebug", "Setting to GONE");
        }
    }

    /**
     * Clears all mood filter chips
     */
    private void clearAllFilters() {
        // Uncheck all mood chips
        for (int i = 0; i < filterChipGroup.getChildCount(); i++) {
            View chipView = filterChipGroup.getChildAt(i);
            if (chipView instanceof Chip && chipView.getId() != R.id.chip_clear_all) {
                Chip chip = (Chip) chipView;
                chip.setChecked(false);
            }
        }
        selectedMoods.clear();
        filterMoodEvents();
    }

    /**
     * Updates the selected moods based on the chip selection.
     *
     * @param mood The mood label.
     * @param isSelected Whether the chip is selected or not.
     */
    public void updateMoodFilter(String mood, boolean isSelected) {
        if (isSelected) {
            selectedMoods.add(mood);
        } else {
            selectedMoods.remove(mood);
        }
        filterMoodEvents();
    }

    /**
     * Filters mood events based on selected moods and location.
     */
    private void filterMoodEvents() {
        List<MoodEventModel> filteredList = new ArrayList<>();
        List<MoodEventModel> sourceList = isFriendsMode ? friendsMoodEventList : moodEventList;

        for (MoodEventModel moodEvent : sourceList) {
            if ((selectedMoods.isEmpty() || selectedMoods.contains(moodEvent.getEmotion())) && moodEvent.HasLocation()) {
                if (currentLocationAvailable) {
                    double distance = calculateDistanceInKm(
                            currentUserLatitude, currentUserLongitude,
                            moodEvent.getLatitude(), moodEvent.getLongitude()
                    );

                    if (distance > 5.0) continue; // Skip events farther than 5 km
                }

                filteredList.add(moodEvent);
            }
        }

        mMap.clear();
        for (MoodEventModel filteredMoodEvent : filteredList) {
            if (filteredMoodEvent.HasLocation()) {
                LatLng location = new LatLng(filteredMoodEvent.getLatitude(), filteredMoodEvent.getLongitude());
                Bitmap emojiBitmap = createEmojiBitmap(getEmojiForEmotion(filteredMoodEvent.getEmotion()));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                String formattedTimestamp = dateFormat.format(filteredMoodEvent.getTimestamp());

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(location)
                        .title(filteredMoodEvent.getEmotion())
                        .snippet(formattedTimestamp)
                        .icon(BitmapDescriptorFactory.fromBitmap(emojiBitmap));

                mMap.addMarker(markerOptions);
            }
        }
    }
    /**
     * Called when the map is ready to be used.
     *
     * @param googleMap The GoogleMap object.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);
            return;
        }
        mMap.setOnMarkerClickListener(marker -> {
            showMoodDetailsDialog(marker.getTitle(), marker.getSnippet());
            return true; // Return true to override the default info window
        });


        mMap.setMyLocationEnabled(true);

        fusedLocationClient.getCurrentLocation(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        currentUserLatitude = location.getLatitude();
                        currentUserLongitude = location.getLongitude();
                        currentLocationAvailable = true;

                        LatLng userLocation = new LatLng(currentUserLatitude, currentUserLongitude);
                        mMap.addMarker(new MarkerOptions().position(userLocation).title("You are here"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                    }
                });
    }

    /**
     * Handles the result of permission requests.
     *
     * @param requestCode  The request code passed in requestPermissions().
     * @param permissions  The requested permissions.
     * @param grantResults The grant results for the requested permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onMapReady(mMap);
            }
        }
    }
    /**
     * Handles the "Memory" button click event to add mood event markers.
     */
    private void onMemoryButtonClick() {
        isFriendsMode = false;
        if (moodEventList == null) {
            moodEventList = new ArrayList<>();
        } else {
            moodEventList.clear();
        }

        DatabaseManager.getInstance().fetchMoodEvents(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    String emotion = document.getString("emotion");
                    String description = document.getString("description");
                    Date timestamp = document.getDate("timestamp"); // ✅ Get timestamp as Date
                    String emoji = document.getString("emoji");
                    Long colorLong = document.getLong("color");
                    boolean triggerWarning = Boolean.TRUE.equals(document.getBoolean("triggerWarning"));
                    boolean hasLocation = Boolean.TRUE.equals(document.getBoolean("hasLocation"));
                    Double latitude = document.getDouble("latitude");
                    Double longitude = document.getDouble("longitude");

                    // Convert color from Long to int
                    int color = (colorLong != null) ? colorLong.intValue() : 0;

                    // Ensure latitude & longitude are not null
                    double lat = (latitude != null) ? latitude : 0.0;
                    double lon = (longitude != null) ? longitude : 0.0;

                    // Create a new MoodEventModel object with Date timestamp
                    MoodEventModel moodEvent = new MoodEventModel(
                            emotion != null ? emotion : "Unknown",
                            description != null ? description : "No description",
                            timestamp,
                            emoji != null ? emoji : "😐",
                            color,
                            triggerWarning,
                            hasLocation,
                            lat,
                            lon
                    );

                    // Debug logs
                    Log.d("MoodEventDebug", "Emotion: " + moodEvent.getEmotion());
                    Log.d("MoodEventDebug", "Description: " + moodEvent.getDescription());
                    Log.d("MoodEventDebug", "Timestamp: " + timestamp);
                    Log.d("MoodEventDebug", "Emoji: " + moodEvent.getEmoji());
                    Log.d("MoodEventDebug", "Color: " + moodEvent.getColor());
                    Log.d("MoodEventDebug", "Trigger Warning: " + moodEvent.hasTriggerWarning());
                    Log.d("MoodEventDebug", "Has Location: " + moodEvent.HasLocation());
                    Log.d("MoodEventDebug", "Lat: " + moodEvent.getLatitude());
                    Log.d("MoodEventDebug", "Lon: " + moodEvent.getLongitude());

                    // Add the new object to the list
                    moodEventList.add(moodEvent);
                }
                addMoodEventMarkers();
                filterMoodEvents();

            } else {
                Log.e("FetchError", "Error getting documents: ", task.getException());
            }
        });
    }

    /**
     * Displays a dialog with mood details.
     *
     * @param title   The title of the dialog.
     * @param details The details to be displayed.
     */
    private void showMoodDetailsDialog(String title, String details) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(title);
        builder.setMessage(details);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    /**
     * Handles the "Friends" button click event to add friends' mood event markers.
     */
    private void onFriendsButtonClick() {
        isFriendsMode = true;
        if (friendsMoodEventList == null) {
            friendsMoodEventList = new ArrayList<>();
        } else {
            friendsMoodEventList.clear();
        }
        // Usernames from database:
        DatabaseManager.getInstance().fetchAllUsernames(usernames -> {
            usernamesMap = usernames;

            // Mood Events from database:
            DatabaseManager.getInstance().fetchFollowedUsersMoodEvents(task -> {
                if (task.isSuccessful()) {
                    List<MoodEventModel> moodEvents = task.getResult();
                    if (moodEvents != null) {
                        for (MoodEventModel moodEvent : moodEvents) {
                            if (moodEvent.HasLocation()) {
                                if (currentLocationAvailable) {
                                    double distance = calculateDistanceInKm(
                                            currentUserLatitude,
                                            currentUserLongitude,
                                            moodEvent.getLatitude(),
                                            moodEvent.getLongitude()
                                    );

                                    if (distance <= 5.0) {
                                        friendsMoodEventList.add(moodEvent);
                                    }
                                } else {
                                    friendsMoodEventList.add(moodEvent);
                                }
                            }
                        }
                        addFriendsMoodEventMarkers();
                        //filterFriendsMoodEvents();
                        filterMoodEvents();

                    }
                } else {
                    Log.e("FetchError", "Error getting mood events: ", task.getException());
                }
            });
        });
    }

    /**
     * Calculates the distance between two geographical points using the Haversine formula.
     *
     * @param lat1 Latitude of the first point.
     * @param lon1 Longitude of the first point.
     * @param lat2 Latitude of the second point.
     * @param lon2 Longitude of the second point.
     * @return The distance in kilometers.
     */
    private double calculateDistanceInKm(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371.0; // in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }










    /**
     * Gets the emoji representation for a given emotion.
     *
     * @param emotion The emotion string.
     * @return The corresponding emoji.
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
     * Adds mood event markers to the map.
     */
    private void addMoodEventMarkers() {
        if (moodEventList != null && !moodEventList.isEmpty()) {
            for (MoodEventModel moodEvent : moodEventList) {
                if (moodEvent.HasLocation()) {
                    LatLng location = new LatLng(moodEvent.getLatitude(), moodEvent.getLongitude());
                    Bitmap emojiBitmap = createEmojiBitmap(getEmojiForEmotion(moodEvent.getEmotion()));
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                    String formattedTimestamp = dateFormat.format(moodEvent.getTimestamp()); // Convert Date to String

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(location)
                            .title(moodEvent.getEmotion())
                            .snippet(formattedTimestamp)
                            .icon(BitmapDescriptorFactory.fromBitmap(emojiBitmap));

                    mMap.addMarker(markerOptions);
                }
            }
        }
    }

    private void addFriendsMoodEventMarkers() {
        if (friendsMoodEventList != null && !friendsMoodEventList.isEmpty()) {
            for (MoodEventModel moodEvent : friendsMoodEventList) {
                if (moodEvent.HasLocation()) {
                    LatLng location = new LatLng(moodEvent.getLatitude(), moodEvent.getLongitude());
                    Bitmap emojiBitmap = createEmojiBitmap(getEmojiForEmotion(moodEvent.getEmotion()));

                    // Username tingz
                    String username = usernamesMap.getOrDefault(moodEvent.getUserId(), "Unknown User");


                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                    String formattedTimestamp = dateFormat.format(moodEvent.getTimestamp());
                    String snippetText = "Posted by: " + username + "\n" + formattedTimestamp;

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(location)
                            .title(moodEvent.getEmotion())
                            .snippet(snippetText)
                            .icon(BitmapDescriptorFactory.fromBitmap(emojiBitmap));

                    mMap.addMarker(markerOptions);
                }
            }
        }
    }
    /**
     * Converts an emoji to a Bitmap to be used as a marker icon.
     *
     * @param emoji The emoji character.
     * @return A Bitmap representation of the emoji.
     */
    private Bitmap createEmojiBitmap(String emoji) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(100);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.CENTER);

        float baseline = -paint.ascent();
        int width = (int) (paint.measureText(emoji) + 10);
        int height = (int) (baseline + paint.descent() + 10);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(emoji, width / 2, baseline, paint);

        return bitmap;
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchLocation(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
    private void searchLocation(String location) {
        if (mMap == null){
            return;
        }
        Geocoder geocoder = new Geocoder(requireContext());
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocationName(location, 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}