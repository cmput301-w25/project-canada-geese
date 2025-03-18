package com.example.canada_geese.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canada_geese.Adapters.FollowRequestAdapter;
import com.example.canada_geese.Models.FollowRequestModel;
import com.example.canada_geese.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RequestsDialogFragment extends DialogFragment {
    private RecyclerView recyclerView;
    private FollowRequestAdapter adapter;
    private List<FollowRequestModel> requestList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_dialog, container, false);

        recyclerView = view.findViewById(R.id.request_lists);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        requestList = new ArrayList<>();
        adapter = new FollowRequestAdapter(requestList, getContext());
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        loadFollowRequests();

        return view;

    }


    private void loadFollowRequests() {
        FirebaseUser user = mAuth.getCurrentUser();
        db.collection("requests")
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    requestList.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        FollowRequestModel request = documentSnapshot.toObject(FollowRequestModel.class);
                        requestList.add(request);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("RequestsDialogFragment", "Error loading requests", e);
                });

        ;
    }
}
