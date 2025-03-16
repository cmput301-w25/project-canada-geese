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


import com.example.canada_geese.Models.FollowRequest;
import com.example.canada_geese.R;

import java.util.List;
public class FollowRequestAdapter extends RecyclerView.Adapter<FollowRequestAdapter.ViewHolder> {
    private List<FollowRequest> followRequestList;
    private Context context;

    public FollowRequestAdapter(List<FollowRequest> followRequestList, Context context) {
        this.followRequestList = followRequestList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follow_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FollowRequest followRequest = followRequestList.get(position);
        holder.username.setText(followRequest.getUsername());
        holder.acceptButton.setOnClickListener(v -> {
            Toast.makeText(context, "Accepted " + followRequest.getUsername(), Toast.LENGTH_SHORT).show();
        });
        holder.declineButton.setOnClickListener(v -> {
            Toast.makeText(context, "Declined " + followRequest.getUsername(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return followRequestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public Button acceptButton;
        public Button declineButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.request_username);
            acceptButton = itemView.findViewById(R.id.btn_accept);
            declineButton = itemView.findViewById(R.id.btn_decline);
        }
    }
}
