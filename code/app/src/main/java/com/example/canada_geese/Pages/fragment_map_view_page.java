package com.example.canada_geese.Pages;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A fragment that displays a map view with mood event markers.
 */
public class fragment_map_view_page extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Button memoryButton;
    private List<MoodEventModel> moodEventList;

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

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        memoryButton = rootView.findViewById(R.id.btnMemory);
        memoryButton.setOnClickListener(v -> onMemoryButtonClick());

        return rootView;
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

        mMap.setMyLocationEnabled(true);

        fusedLocationClient.getCurrentLocation(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
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
        if (moodEventList == null) {
            moodEventList = new ArrayList<>();
        } else {
            moodEventList.clear(); // Clear existing data to prevent duplicates
        }

        DatabaseManager.getInstance().fetchMoodEvents(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    // Manually extract fields from Firestore document
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
                            timestamp,  // ✅ Directly passing Date object
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
            } else {
                Log.e("FetchError", "Error getting documents: ", task.getException());
            }
        });
    }

    /**
     * Gets the emoji representation for a given emotion.
     *
     * @param emotion The emotion string.
     * @return The corresponding emoji.
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
     * Adds mood event markers to the map.
     */
    private void addMoodEventMarkers() {
        if (moodEventList != null && !moodEventList.isEmpty()) {
            for (MoodEventModel moodEvent : moodEventList) {
                if (!moodEvent.HasLocation()) {
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

    /**
     * Retrieves a sample list of mood events for testing.
     *
     * @return A list of sample MoodEventModel objects.
     */
    private List<MoodEventModel> getSampleMoodEvents() {
        List<MoodEventModel> list = new ArrayList<>();
        list.add(new MoodEventModel("Happiness", "test", new Date(), "😊", R.color.color_happiness, false, true, 51.0447, -114.0719));
        list.add(new MoodEventModel("Anger", "test", new Date(), "😠", R.color.color_anger, false, false, 0.0, 0.0));
        list.add(new MoodEventModel("Fear", "test", new Date(), "😢", R.color.color_sadness, false, false, 0.0, 0.0));
        return list;
    }
}