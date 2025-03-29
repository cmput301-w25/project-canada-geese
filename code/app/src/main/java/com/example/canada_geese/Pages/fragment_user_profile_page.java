package com.example.canada_geese.Pages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.canada_geese.Adapters.UserSearchAdapter;
import com.bumptech.glide.Glide;
import com.example.canada_geese.Adapters.FollowRequestAdapter;
import com.example.canada_geese.Adapters.UsersAdapter;
import com.example.canada_geese.Fragments.RequestsDialogFragment;
import com.example.canada_geese.Managers.DatabaseManager;

import com.example.canada_geese.Models.Users;
import com.example.canada_geese.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Fragment to display and manage the user's profile page.
 * Includes user information such as username and profile picture,
 * and provides a sign-out functionality.
 */
public class fragment_user_profile_page extends Fragment{

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView usernameText;
    private TextView userBioText;
    private TextView followersCountText;
    private TextView followingCountText;
    private ImageButton menuImageButton;
    private ImageView profileImage;
    private ImageButton returnButton;
    private SearchView searchView;
    private ListView followersListView;
    private LinearLayout followersSection;
    private LinearLayout followingSection;
    private ArrayAdapter<String> userAdapter;
    private RecyclerView searchResultsList;
    private RecyclerView followRequestsList;
    LinearLayout searchResultsContainer;
    LinearLayout profileContentContainer;
    private List<String> userList;
    private List<Users> AllUsers;
    private UsersAdapter usersAdapter;


    /**
     * Required empty public constructor.
     */
    public fragment_user_profile_page() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of this fragment.
     *
     * @return A new instance of fragment_user_profile_page.
     */
    public static fragment_user_profile_page newInstance() {
        fragment_user_profile_page fragment = new fragment_user_profile_page();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Inflates the layout for this fragment and initializes Firebase authentication and UI components.
     *
     * @param inflater           The LayoutInflater object to inflate views.
     * @param container          The parent view to attach the fragment's UI.
     * @param savedInstanceState A previous saved state of the fragment, if available.
     * @return The root view for this fragment's layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile_page, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Find Views for the UI components of the profile content container
        usernameText = rootView.findViewById(R.id.username_text);
        profileImage = rootView.findViewById(R.id.profile_image);
        userBioText = rootView.findViewById(R.id.about_text);
        followersSection = rootView.findViewById(R.id.followers_section);
        followingSection = rootView.findViewById(R.id.following_section);
        followersCountText = rootView.findViewById(R.id.followers_count);
        followingCountText = rootView.findViewById(R.id.following_count);
        followersListView = rootView.findViewById(R.id.followers_list);

        // Find Views for the search bar, menu and return buttons
        searchView = rootView.findViewById(R.id.searchView);
        menuImageButton = rootView.findViewById(R.id.menu_button);
        returnButton = rootView.findViewById(R.id.back_button);

        // Find views for the search list view recycler view in the search results container
        searchResultsList = rootView.findViewById(R.id.search_results_list);
        searchResultsList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the search results container AND profile content container
        searchResultsContainer = rootView.findViewById(R.id.search_results_container);
        profileContentContainer = rootView.findViewById(R.id.profile_content_container);

        // Initialize the list of users this is for profile container (this is the list on user profile page)
        userList = new ArrayList<>();
        userAdapter= new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, userList);
        followersListView.setAdapter(userAdapter);

        // Initialize the list of users for search results page container (this is for the list of all users when you click search)
        AllUsers = new ArrayList<>();
        usersAdapter = new UsersAdapter(AllUsers, getContext(), mAuth.getCurrentUser().getUid());
        // Make the list clickable and show user details
        usersAdapter.setOnItemClickListener(new UsersAdapter.onItemClickListener() {

            //GET USER HERE
            @Override
            public void onItemClick(Users users) {
                // Shows user dialog
            }

            @Override
            public void onFollowRequest(Users users) {
                sendFollowRequest(users);
            }

            @Override
            public void onUnfollowRequest(Users users) { unFollowRequest(users);}
        });
        searchResultsList.setAdapter(usersAdapter);

        // Fetch the user details from Firebase
        loadUserProfile();
        // Show the list of followers
        showFollowersList();
        // Set up the menu button
        menuImageButton.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(requireContext(), menuImageButton);
            popup.getMenuInflater().inflate(R.menu.profile_menu, popup.getMenu());

            // Set click listener for menu items
            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_signout) {
                    signOutUser();
                    return true;
                } else if (id == R.id.action_requests) {
                    // Open the requests dialog fragment which is handled in the RequestsDialogFragment class, allows user to accept or reject requests
                    DialogFragment dialog = new RequestsDialogFragment();
                    dialog.show(getParentFragmentManager(), "RequestsDialogFragment");
                    return true;

                } else if (id == R.id.action_settings) {
                    // Navigate to the settings page managed by EditProfileFragment
                    Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            });

            popup.show();
        });

        // Click listener for the followers section
        followersSection.setOnClickListener(v -> {
            // make the list visible
            followersListView.setVisibility(View.VISIBLE);
            // Show the list of followers
            showFollowersList();
        });
        // Click listener for the following section
        followingSection.setOnClickListener(v -> {
            // make the list visible
            followersListView.setVisibility(View.VISIBLE);
            // Show the list of following
            showFollowingList();
        });



        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                menuImageButton.setVisibility(View.GONE);
                returnButton.setVisibility(View.VISIBLE);
                searchResultsContainer.setVisibility(View.VISIBLE);
                profileContentContainer.setVisibility(View.GONE);

                // Fetch all users from the database
//                DatabaseManager.getInstance().fetchAllUsers(task -> {
//                    if (task.isSuccessful()) {
//                        List<Users> newList = new ArrayList<>();
//                        for (DocumentSnapshot document : task.getResult()) {
//                            Users user = document.toObject(Users.class);
//                            newList.add(user);
//                        }
//                        usersAdapter.updateList(newList);
//                    } else {
//                        Log.e("FetchError", "Error getting documents: ", task.getException());
//                    }
//                });
            }

        });

        // Search view to filter through database users
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterUsers(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // Clear the list and notify the adapter
                    usersAdapter.updateList(new ArrayList<>());
                } else {
                    filterUsers(newText);
                }
                return false;
            }
        });

        // Return button click listener
        returnButton.setOnClickListener(v -> {
            // remove focus from the search view
            searchView.clearFocus();
            // remove any text from the search view
            searchView.setQuery("", false);
            menuImageButton.setVisibility(View.VISIBLE);
            returnButton.setVisibility(View.GONE);
            searchResultsContainer.setVisibility(View.GONE);
            profileContentContainer.setVisibility(View.VISIBLE);
        });

        return rootView;
    }



    /**
     * Signs out the current user, clears stored login preferences, and redirects to the login screen.
     */
    private void signOutUser() {
        // Sign out activity on firebase
        mAuth.signOut();

        // Sign-out clears shared preference
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Navigate back to LoginActivity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        // Clear the back stack and start the new activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        // Finish the current activity so the user can't go back to the profile page
        requireActivity().finish(); // Close the profile activity
    }

    /**
     * Loads the user's profile information including username and profile picture.
     * Displays default information if user data is not available.
     */
    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        // Use the signed in user to pull up their profile information from the database
        if (user != null) {
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            usernameText.setText(username);
                             //firestore tingz
                            String profileImageUrl = documentSnapshot.getString("image_profile");
                            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                                Glide.with(this)
                                        .load(profileImageUrl)
                                        .circleCrop()
                                        .placeholder(R.drawable.profile)
                                        .error(R.drawable.profile)
                                        .into(profileImage);
                            } else {
                                // Use placeholder if no image URL
                                profileImage.setImageResource(R.drawable.profile);
                            }
                            String bio = documentSnapshot.getString("about");
                            if (bio != null && !bio.isEmpty()) {
                                userBioText.setText(bio);
                            } else {
                                userBioText.setVisibility(View.GONE);
                            }
                        }
                    });

            /*// Ignoring firestore storage setup for current implementation for user profile
            // Load profile picture if available
            Uri profileUri = user.getPhotoUrl();
            if (profileUri != null) {
                profileImage.setImageURI(profileUri);
            } else {
                profileImage.setImageResource(R.drawable.profile); // Placeholder image
            }
//            profileImage.setImageResource(R.drawable.profile);*/


            // Load Followers to the user profile
            db.collection("users").document(user.getUid()).collection("followers").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        int followersCount = queryDocumentSnapshots.size();
                        followersCountText.setText(String.valueOf(followersCount));
                    });
            // Load Following to the user profile
            db.collection("users").document(user.getUid()).collection("following").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        int followingCount = queryDocumentSnapshots.size();
                        followingCountText.setText(String.valueOf(followingCount));
                    });
        }
    }
    private void showFollowersList() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid()).collection("followers").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        userList.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            String username = document.getString("username");
                            userList.add(username); // Add only the username to the list
                        }
                        userAdapter.notifyDataSetChanged(); // Refresh the ListView
                    });

        }
    }
    private void showFollowingList() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid()).collection("following").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        userList.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            String username = document.getString("username");
                            userList.add(username); // Add only the username to the list
                        }
                        userAdapter.notifyDataSetChanged(); // Refresh the ListView
                    });
        }
    }
    // Then add this method to filter users based on search query
    private void filterUsers(String searchText) {
        if (AllUsers == null || AllUsers.isEmpty()) {
            // If users haven't been loaded yet, fetch them first
            DatabaseManager.getInstance().fetchAllUsers(task -> {
                if (task.isSuccessful()) {
                    List<Users> newList = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        Users user = document.toObject(Users.class);
                        if (user != null) {
                            newList.add(user);
                        }
                    }
                    AllUsers = newList;
                    performFiltering(searchText);
                } else {
                    Log.e("FilterError", "Error getting documents: ", task.getException());
                }
            });
        } else {
            // If users are already loaded, just filter them
            performFiltering(searchText);
        }
    }

    // Add this helper method to perform the actual filtering
    private void performFiltering(String searchText) {
        List<Users> filteredList = new ArrayList<>();
        List<Users> secondaryList = new ArrayList<>();
        for (Users user : AllUsers) {
            if (user.getUsername() != null ) {
                String username = user.getUsername().toLowerCase();
                String query = searchText.toLowerCase();

                if (username.startsWith(query)) {
                    filteredList.add(user);
                } else if (username.contains(query)) {
                    secondaryList.add(user);
                }
            }
        }

        // Add secondary matches to the end of the filtered list
        filteredList.addAll(secondaryList);

        // Update adapter with filtered results
        usersAdapter.updateList(filteredList);
    }

    private void sendFollowRequest(Users user) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e("Firestore", "User is not logged in.");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String senderId = currentUser.getUid();  // Current user ID
        String recipientUsername = user.getUsername();  // Clicked user's username

        // First, get sender's username
        db.collection("users").document(senderId).get()
                .addOnSuccessListener(senderDoc -> {
                    if (senderDoc.exists()) {
                        String senderName = senderDoc.getString("username");

                        // Now find the recipient's document ID by querying for their username
                        db.collection("users")
                                .whereEqualTo("username", recipientUsername)
                                .limit(1)  // We only need one result
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    if (!querySnapshot.isEmpty()) {
                                        // Get the recipient's document ID
                                        DocumentSnapshot recipientDoc = querySnapshot.getDocuments().get(0);
                                        String recipientId = recipientDoc.getId();

                                        Log.d("Firestore", "Found recipient ID: " + recipientId);

                                        // Create the request data
                                        Map<String, Object> requestData = new HashMap<>();
                                        requestData.put("username", senderName);
                                        requestData.put("status", "pending");

                                        // The key part: use document path to specify the subcollection
                                        db.collection("users")
                                                .document(recipientId)
                                                .collection("requests")  // This creates a subcollection
                                                .document(senderId)  // Use sender ID as document ID
                                                .set(requestData)
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("Firestore", "Follow request sent successfully to: " + recipientId);
                                                    // Add UI feedback here
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("Firestore", "Failed to send follow request", e);
                                                    // Add UI error feedback here
                                                });
                                    } else {
                                        Log.e("Firestore", "Recipient with username '" + recipientUsername + "' not found");
                                        // Add UI error feedback here
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Failed to query for recipient", e);
                                    // Add UI error feedback here
                                });
                    } else {
                        Log.e("Firestore", "Current user document does not exist");
                        // Add UI error feedback here
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Failed to get sender info", e);
                    // Add UI error feedback here
                });
    }

    private void unFollowRequest(Users user) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e("Firestore", "User is not logged in.");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String senderId = currentUser.getUid();  // Current user ID
        String recipientUsername = user.getUsername();  // Clicked user's username

        // First, get sender's username
        db.collection("users").document(senderId).get()
                .addOnSuccessListener(senderDoc -> {
                    if (senderDoc.exists()) {
                        String senderName = senderDoc.getString("username");
                        // Now find the recipient's document ID by querying for their username
                        db.collection("users")
                                .whereEqualTo("username", recipientUsername)
                                .limit(1)  // We only need one result
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    if (!querySnapshot.isEmpty()) {
                                        // Get the recipient's document ID
                                        DocumentSnapshot recipientDoc = querySnapshot.getDocuments().get(0);
                                        String recipientId = recipientDoc.getId();
                                        // Batch write to remove from following and followers collections
                                        WriteBatch batch = db.batch();

                                        // Reference to the document to remove from current user's following
                                        Query followingQuery = db.collection("users")
                                                .document(senderId)
                                                .collection("following")
                                                .whereEqualTo("username", recipientUsername);

                                        // Reference to the document to remove from recipient's followers
                                        Query followersQuery = db.collection("users")
                                                .document(recipientId)
                                                .collection("followers")
                                                .whereEqualTo("username", senderName);

                                        // Remove from current user's following collection
                                        followingQuery.get().addOnSuccessListener(followingSnapshot -> {
                                                    for (DocumentSnapshot doc : followingSnapshot.getDocuments()) {
                                                        batch.delete(doc.getReference());
                                                    }

                                                    // Remove from recipient's followers collection
                                                    followersQuery.get().addOnSuccessListener(followersSnapshot -> {
                                                                for (DocumentSnapshot doc : followersSnapshot.getDocuments()) {
                                                                    batch.delete(doc.getReference());
                                                                }

                                                                // Commit the batch
                                                                batch.commit()
                                                                        .addOnSuccessListener(aVoid -> {
                                                                            Log.d("UnfollowRequest", "Successfully unfollowed user");
                                                                            // Optionally update UI or show toast
                                                                        })
                                                                        .addOnFailureListener(e -> {
                                                                            Log.e("UnfollowRequest", "Failed to unfollow user", e);
                                                                            // Optionally show error to user
                                                                        });
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                Log.e("UnfollowRequest", "Failed to find followers document", e);
                                                            });
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("UnfollowRequest", "Failed to find following document", e);
                                                });
                                    } else {
                                        Log.e("UnfollowRequest", "Recipient user not found");
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("UnfollowRequest", "Failed to find recipient user", e);
                                });
                    } else {
                        Log.e("UnfollowRequest", "Sender document does not exist");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("UnfollowRequest", "Failed to get sender username", e);
                });
    }
}