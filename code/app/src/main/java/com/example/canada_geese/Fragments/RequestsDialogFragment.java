package com.example.canada_geese.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canada_geese.Adapters.FollowRequestAdapter;
import com.example.canada_geese.Models.FollowRequestModel;
import com.example.canada_geese.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestsDialogFragment extends DialogFragment {
    private RecyclerView recyclerView;
    private FollowRequestAdapter adapter;
    private List<FollowRequestModel> requestList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String currentUser;

    public RequestsDialogFragment() {}

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_dialog, container, false);

        recyclerView = view.findViewById(R.id.request_lists);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();

        requestList = new ArrayList<>();
        adapter = new FollowRequestAdapter(requestList, getContext(), (username, action) -> handleRequestAction(username, action));

        recyclerView.setAdapter(adapter);

        loadFollowRequests();

        return view;
    }

    private void handleRequestAction(String username, String action) {
        db.collection("users").document(currentUser).collection("requests")
                .whereEqualTo("username", username)
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String docId = document.getId(); // Get the document ID

                        if (action.equals("accepted")) {
                            // Add the requesting user to the current user's Followers collection
                            Map<String, Object> followerData = new HashMap<>();
                            followerData.put("username", username);

                            db.collection("users").document(currentUser)
                                    .collection("followers").document(username)
                                    .set(followerData)
                                    .addOnSuccessListener(aVoid -> Log.d("TAG", "Added to current user's Followers collection"))
                                    .addOnFailureListener(e -> Log.d("TAG", "Error adding to current user's Followers collection", e));

                            // Add the current user to the requesting user's Following collection
                            Map<String, Object> followingData = new HashMap<>();
                            followingData.put("username", currentUser);

                            db.collection("users").document(username)
                                    .collection("following").document(currentUser)
                                    .set(followingData)
                                    .addOnSuccessListener(aVoid -> Log.d("TAG", "Added to requesting user's Following collection"))
                                    .addOnFailureListener(e -> Log.d("TAG", "Error adding to requesting user's Following collection", e));
                        }

                        // Delete the follow request document after processing
                        db.collection("users").document(currentUser)
                                .collection("requests").document(docId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("TAG", "Request removed successfully");
                                    removeRequestFromList(username);
                                })
                                .addOnFailureListener(e -> Log.d("TAG", "Error deleting request", e));
                    }
                })
                .addOnFailureListener(e -> Log.d("TAG", "Error processing follow request", e));
    }

    private void removeRequestFromList(String username) {
        for (FollowRequestModel request : requestList) {
            if (request.getUsername().equals(username)) {
                requestList.remove(request);
                adapter.notifyDataSetChanged();
                break;
            }
        }
    }

    private void loadFollowRequests() {
        db.collection("users").document(currentUser).collection("requests")
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    requestList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String username = document.getString("username");
                        requestList.add(new FollowRequestModel(username, "pending"));
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.d("TAG", "Error getting requests", e));
    }
}
