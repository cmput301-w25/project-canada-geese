package com.example.canada_geese.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canada_geese.Models.Users;
import com.example.canada_geese.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private List<Users> usersList;
    private List<Users> filteredUsersList;
    private Context context;
    private String currentQuery = "";
    private String currentUsername;
    private onItemClickListener listener;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // Constructor
    public UsersAdapter(List<Users> usersList, Context context, String currentUsername) {
        this.usersList = new ArrayList<>(usersList);
        this.filteredUsersList = new ArrayList<>(usersList);
        this.context = context;
        this.currentUsername = currentUsername;
        this.db = FirebaseFirestore.getInstance();  // Initialize Firestore
    }

    // Interface for click events
    public interface onItemClickListener {
        void onItemClick(Users users);
        void onFollowRequest(Users users);
        void onSendMessage(Users users);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users = filteredUsersList.get(position);
        if (users != null) {
            holder.textUsername.setText(users.getUsername());
            holder.profileImage.setImageResource(R.drawable.profile);
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(users);
                    showUserDetailsDialog(users);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return filteredUsersList.size();
    }

    private void showUserDetailsDialog(Users user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_send_request, null);
        builder.setView(dialogView);

        // Initialize views
        ImageView profileImage = dialogView.findViewById(R.id.dialog_profile_image);
        TextView username = dialogView.findViewById(R.id.dialog_username);
        TextView about = dialogView.findViewById(R.id.dialog_about);
        Button actionButton = dialogView.findViewById(R.id.dialog_action_button);

        profileImage.setImageResource(R.drawable.profile);
        username.setText(user.getUsername());
        about.setText(user.getAbout() != null ? user.getAbout() : "No description available");

        AlertDialog dialog = builder.create();
        dialog.show();

        // Check if user is following
        checkIfFollowing(user.getUserId(), isFollowing -> {
            if (isFollowing) {
                actionButton.setText("Send Message");
                actionButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onSendMessage(user);
                    }
                    dialog.dismiss();
                });
            } else {
                actionButton.setText("Send Follow Request");
                actionButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onFollowRequest(user);
                    }
                    dialog.dismiss();
                });
            }
        });
    }

    // Asynchronous check if current user is following this user shows follow or send message button
    private void checkIfFollowing(String userId, OnFollowCheckListener callback) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("users")
                    .document(user.getUid())
                    .collection("following")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            boolean isFollowing = !task.getResult().isEmpty();
                            callback.onResult(isFollowing);
                        } else {
                            callback.onResult(false);
                        }
                    });
        } else {
            callback.onResult(false);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textUsername;
        ImageView profileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textUsername = itemView.findViewById(R.id.text_username);
            profileImage = itemView.findViewById(R.id.profile_image);
        }
    }

    public void updateList(List<Users> newData) {
        this.usersList.clear();
        this.usersList.addAll(newData);
        filter(currentQuery);
    }

    public void filter(String query) {
        this.currentQuery = query;
        filteredUsersList.clear();

        for (Users user : usersList) {
            if (user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                filteredUsersList.add(user);
            }
        }
        notifyDataSetChanged();
    }

    // Callback interface for asynchronous follow check
    private interface OnFollowCheckListener {
        void onResult(boolean isFollowing);
    }
}
