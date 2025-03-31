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

/**
 * Adapter for displaying and handling follow requests in a RecyclerView.
 */
public class FollowRequestAdapter extends RecyclerView.Adapter<FollowRequestAdapter.ViewHolder> {

    private List<FollowRequestModel> requestList;
    private final Context context;
    private final OnRequestActionListener listener;

    /**
     * Listener interface to handle accept/reject actions on follow requests.
     */
    public interface OnRequestActionListener {
        /**
         * Callback for follow request actions.
         *
         * @param username the username associated with the request
         * @param action   either "accepted" or "rejected"
         */
        void onRequestAction(String username, String action);
    }

    /**
     * Constructs a FollowRequestAdapter.
     *
     * @param requestList the list of follow requests
     * @param context     the context used for inflating views
     * @param listener    the listener to handle request actions
     */
    public FollowRequestAdapter(List<FollowRequestModel> requestList, Context context, OnRequestActionListener listener) {
        this.requestList = requestList;
        this.context = context;
        this.listener = listener;
    }

    /**
     * Updates the list of follow requests and refreshes the RecyclerView.
     *
     * @param newRequests the new list of follow requests
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_follow_request, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds the data to the ViewHolder for a specific position.
     *
     * @param holder   the ViewHolder to bind data to
     * @param position the position of the item in the list
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FollowRequestModel request = requestList.get(position);

        holder.usernameText.setText(request.getUsername());

        holder.acceptButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRequestAction(request.getUsername(), "accepted");
            }
        });

        holder.rejectButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRequestAction(request.getUsername(), "rejected");
            }
        });
    }

    /**
     * Returns the number of items in the list.
     *
     * @return the size of the request list
     */
    @Override
    public int getItemCount() {
        return requestList != null ? requestList.size() : 0;
    }

    /**
     * ViewHolder for follow request items.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        Button acceptButton;
        Button rejectButton;

        /**
         * Constructs a ViewHolder for a follow request item.
         *
         * @param itemView the inflated layout for the item
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.username_text);
            acceptButton = itemView.findViewById(R.id.accept_button);
            rejectButton = itemView.findViewById(R.id.reject_button);
        }
    }
}
