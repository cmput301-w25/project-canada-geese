package com.example.canada_geese.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canada_geese.Models.FollowRequestModel;
import com.example.canada_geese.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FollowRequestAdapter extends RecyclerView.Adapter<FollowRequestAdapter.ViewHolder> {

    private List<FollowRequestModel> requestList;
    private Context context;
    private FirebaseFirestore db;

    public FollowRequestAdapter(List<FollowRequestModel> requestList, Context context) {
        this.requestList = requestList;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_follow_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FollowRequestModel request = requestList.get(position);
        holder.usernameText.setText(request.getRequestUsername());
        holder.acceptButton.setOnClickListener(v -> updateRequestStatus(request, "accepted"));
        holder.rejectButton.setOnClickListener(v -> updateRequestStatus(request, "rejected"));
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    private void updateRequestStatus(FollowRequestModel request, String status) {
        db.collection("follow_requests").document(request.getRequestID())
                .update("status", status)
                .addOnSuccessListener(aVoid -> {
                    requestList.remove(request);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Request " + status, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error updating request", Toast.LENGTH_SHORT).show());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        Button acceptButton;
        Button rejectButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.username_text);
            acceptButton = itemView.findViewById(R.id.accept_button);
            rejectButton = itemView.findViewById(R.id.reject_button);
        }
    }
}