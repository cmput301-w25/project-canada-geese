package com.example.canada_geese.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

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

/**
 * DialogFragment for displaying and handling incoming follow requests.
 */
public class RequestsDialogFragment extends DialogFragment {

    private RecyclerView recyclerView;
    private FollowRequestAdapter adapter;
    private List<FollowRequestModel> requestList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String currentUser;
    private ImageView closeButton;

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
        adapter = new FollowRequestAdapter(requestList, getContext(), this::handleRequestAction);
        recyclerView.setAdapter(adapter);

        loadFollowRequests();

        return view;
    }

    /**
     * Handles accepting or rejecting a follow request.
     * @param username the username of the requesting user
     * @param action either "accepted" or "rejected"
     */
    private void handleRequestAction(String username, String action) {
        db.collection("users").document(currentUser).collection("requests")
                .whereEqualTo("username", username)
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String docId = document.getId();

                        if (action.equals("accepted")) {
                            Map<String, Object> followerData = new HashMap<>();
                            followerData.put("username", username);

                            db.collection("users").document(currentUser)
                                    .collection("followers")
                                    .add(followerData)
                                    .addOnSuccessListener(docRef ->
                                            Log.d("TAG", "Added to followers with ID: " + docRef.getId()))
                                    .addOnFailureListener(e ->
                                            Log.d("TAG", "Error adding to followers", e));

                            db.collection("users").document(currentUser).get()
                                    .addOnSuccessListener(currentUserDoc -> {
                                        if (currentUserDoc.exists()) {
                                            String currentUsername = currentUserDoc.getString("username");

                                            db.collection("users")
                                                    .whereEqualTo("username", username)
                                                    .limit(1)
                                                    .get()
                                                    .addOnSuccessListener(requestorQuery -> {
                                                        if (!requestorQuery.isEmpty()) {
                                                            String requestorId = requestorQuery.getDocuments().get(0).getId();

                                                            Map<String, Object> followingData = new HashMap<>();
                                                            followingData.put("username", currentUsername);

                                                            db.collection("users").document(requestorId)
                                                                    .collection("following")
                                                                    .add(followingData)
                                                                    .addOnSuccessListener(docRef ->
                                                                            Log.d("TAG", "Added to following with ID: " + docRef.getId()))
                                                                    .addOnFailureListener(e ->
                                                                            Log.d("TAG", "Error adding to following", e));
                                                        }
                                                    });
                                        }
                                    });
                        }

                        db.collection("users").document(currentUser)
                                .collection("requests").document(docId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("TAG", "Request removed successfully");
                                    removeRequestFromList(username);
                                });
                    }
                });
    }

    /**
     * Removes a follow request from the UI list.
     * @param username the username to remove
     */
    private void removeRequestFromList(String username) {
        for (FollowRequestModel request : requestList) {
            if (request.getUsername().equals(username)) {
                requestList.remove(request);
                adapter.notifyDataSetChanged();
                break;
            }
        }
    }

    /**
     * Loads pending follow requests from Firestore.
     */
    private void loadFollowRequests() {
        db.collection("users").document(currentUser).collection("requests")
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    requestList.clear();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        String username = document.getString("username");
                        requestList.add(new FollowRequestModel(username, "pending"));
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
