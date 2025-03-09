package com.example.canada_geese.Pages;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
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
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List; // To handle the list of mood events

public class fragment_map_view_page extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Button memoryButton;

    private List<MoodEventModel> moodEventList;  // List of mood events (replace with actual data source)

    public fragment_map_view_page() {
        // Required empty public constructor
    }

    public static fragment_map_view_page newInstance() {
        return new fragment_map_view_page();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map_view_page, container, false);

        // Initialize the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize the location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Initialize and set the "Memory" button functionality
        memoryButton = rootView.findViewById(R.id.btnMemory);
        memoryButton.setOnClickListener(v -> onMemoryButtonClick());

        return rootView;
    }

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

        // Request the most recent location of the user
        fusedLocationClient.getCurrentLocation(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(userLocation).title("You are here"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                    }
                });
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onMapReady(mMap);
            }
        }
    }

    // This method will be called when the "Memory" button is clicked
    private void onMemoryButtonClick() {
        // This is just an example list. Replace this with actual data from your Firestore or other source.
        moodEventList = getSampleMoodEvents();  // Fetch the list of mood events (either from Firestore or your data source)

        // Add the markers to the map
        addMoodEventMarkers();
    }
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

    // Add MoodEventModel markers to the map
    private void addMoodEventMarkers() {
        if (moodEventList != null && !moodEventList.isEmpty()) {
            for (MoodEventModel moodEvent : moodEventList) {
                if (moodEvent.HasLocation()) {
                    LatLng location = new LatLng(moodEvent.getLatitude(), moodEvent.getLongitude());

                    // Convert emoji to Bitmap
                    Bitmap emojiBitmap = createEmojiBitmap(getEmojiForEmotion(moodEvent.getEmotion()));

                    // Create a marker with the emoji as an icon
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(location)
                            .title(moodEvent.getEmotion())
                            .snippet(moodEvent.getTimestamp())
                            .icon(BitmapDescriptorFactory.fromBitmap(emojiBitmap));

                    mMap.addMarker(markerOptions);
                }
            }
        }
    }
    private Bitmap createEmojiBitmap(String emoji) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(100);  // Adjust size as needed
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



    // Example method to simulate fetching mood events (replace with actual data fetching logic)
    private List<MoodEventModel> getMoodEvents() {
        // Fetch your data from Firestore, SQLite, etc. and return it
        // This is just a placeholder.
        return null;
    }
    private List<MoodEventModel> getSampleMoodEvents() {
        List<MoodEventModel> list = new ArrayList<>();
        list.add(new MoodEventModel("Happiness","test", "2025-02-12 08:15", "😊", R.color.color_happiness, false, true, 51.0437725025775, -114.06965767333628 ));
        list.add(new MoodEventModel("Anger","test", "2025-02-11 03:42", "😠", R.color.color_anger, false, true, 51.043920903413614, -114.07465731086558));
        list.add(new MoodEventModel("Fear","test", "2025-02-07 21:16", "😢", R.color.color_sadness, false, false, 0.0, 0.0));
        return list;
    }
}
