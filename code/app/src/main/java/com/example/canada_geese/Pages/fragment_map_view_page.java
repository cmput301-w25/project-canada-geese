package com.example.canada_geese.Pages;

import android.Manifest;
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
import android.widget.SearchView;
import android.widget.Toast;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
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

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        memoryButton = rootView.findViewById(R.id.btnMemory);
        memoryButton.setOnClickListener(v -> onMemoryButtonClick());
        searchView = rootView.findViewById(R.id.search_view);
        setupSearchView();

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
        // Use the existing fetchMoodEvents method from your DatabaseManager
        DatabaseManager.getInstance().fetchMoodEvents(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    List<MoodEventModel> events = new ArrayList<>();

                    // Convert the query documents to MoodEventModel objects
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        MoodEventModel event = document.toObject(MoodEventModel.class);
                        events.add(event);
                    }

                    // Update your list and add markers
                    moodEventList = events;
                    addMoodEventMarkers();
                }
            } else {
                Exception e = task.getException();
                Toast.makeText(requireContext(),
                        "Error loading mood events: " + (e != null ? e.getMessage() : "Unknown error"),
                        Toast.LENGTH_SHORT).show();
                Log.e("MoodMap", "Error loading mood events", e);
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
                if (moodEvent.HasLocation()
                )Log.d("MoodMap", "Checking mood event: " + moodEvent.getEmotion() + " | Has Location: " + moodEvent.HasLocation());
                {
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
    /*private List<MoodEventModel> getSampleMoodEvents() {
        List<MoodEventModel> list = new ArrayList<>();
        list.add(new MoodEventModel("Happiness", "test", new Date(), "😊", R.color.color_happiness, false, true, 51.0447, -114.0719));
        list.add(new MoodEventModel("Anger", "test", new Date(), "😠", R.color.color_anger, false, false, 0.0, 0.0));
        list.add(new MoodEventModel("Fear", "test", new Date(), "😢", R.color.color_sadness, false, false, 0.0, 0.0));
        return list;
    }*/
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