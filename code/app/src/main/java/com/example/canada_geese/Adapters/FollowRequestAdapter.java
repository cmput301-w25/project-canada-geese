package com.example.canada_geese.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canada_geese.Models.FollowRequestModel;
import com.example.canada_geese.R;

import java.util.List;

public class FollowRequestAdapter extends RecyclerView.Adapter<FollowRequestAdapter.ViewHolder> {

    private List<FollowRequestModel> requestList;
    private Context context;
    private OnRequestActionListener listener;

    // Interface for handling request actions
    public interface OnRequestActionListener {
        void onRequestAction(String username, String action);
    }

    public FollowRequestAdapter(List<FollowRequestModel> requestList, Context context, OnRequestActionListener listener) {
        this.requestList = requestList;
        this.context = context;
        this.listener = listener;
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
        holder.usernameText.setText(request.getUsername()); // Updated field name

        // Handle Accept Button Click
        holder.acceptButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRequestAction(request.getUsername(), "accepted");
            }
        });

        // Handle Reject Button Click
        holder.rejectButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRequestAction(request.getUsername(), "rejected");
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    // ViewHolder Class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        Button acceptButton, rejectButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.username_text);
            acceptButton = itemView.findViewById(R.id.accept_button);
            rejectButton = itemView.findViewById(R.id.reject_button);
        }
    }
}
