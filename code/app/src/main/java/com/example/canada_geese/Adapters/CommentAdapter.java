package com.example.canada_geese.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canada_geese.Managers.DatabaseManager;
import com.example.canada_geese.Models.CommentModel;
import com.example.canada_geese.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;
import java.util.List;

/**
 * Adapter class for displaying comments related to a specific mood event in a RecyclerView.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<CommentModel> commentList;
    private final Context context;
    private final String moodEventId;

    /**
     * Constructs a CommentAdapter.
     *
     * @param commentList the list of comments to display
     * @param context     the context used for inflating views and dialogs
     * @param moodEventId the ID of the mood event associated with these comments
     */
    public CommentAdapter(List<CommentModel> commentList, Context context, String moodEventId) {
        this.commentList = commentList;
        this.context = context;
        this.moodEventId = moodEventId;
    }

    /**
     * Updates the list of comments and refreshes the RecyclerView.
     *
     * @param newComments the new list of comments
     */
    public void updateComments(List<CommentModel> newComments) {
        this.commentList = newComments;
        notifyDataSetChanged();
    }

    /**
     * Sets the list of comments.
     *
     * @param commentList the list of comments to set
     */
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    /**
     * Binds the data to the views in the ViewHolder.
     *
     * @param holder   the ViewHolder for the comment item
     * @param position the position of the comment in the list
     */
    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentModel comment = commentList.get(position);

        holder.tvAuthor.setText(comment.getAuthor());
        holder.tvCommentText.setText(comment.getText());
        holder.tvCommentDate.setText(getRelativeTime(comment.getTimestamp()));

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null && comment.getUserId() != null &&
                comment.getUserId().equals(currentUser.getUid())) {

            holder.itemView.setOnLongClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setMessage("Do you want to delete this comment?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            DatabaseManager.getInstance().deleteComment(moodEventId, comment.getDocumentId(), task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Comment deleted", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Failed to delete comment", Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                return true;
            });

        } else {
            holder.itemView.setOnLongClickListener(null);
        }
    }

    /**
     * Returns the number of comments in the list.
     *
     * @return the size of the comment list
     */
    @Override
    public int getItemCount() {
        return commentList != null ? commentList.size() : 0;
    }

    /**
     * ViewHolder class for individual comment items.
     */
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthor, tvCommentText, tvCommentDate;

        /**
         * Constructs a new CommentViewHolder.
         *
         * @param itemView the view for a single comment item
         */
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tv_comment_author);
            tvCommentText = itemView.findViewById(R.id.tv_comment_text);
            tvCommentDate = itemView.findViewById(R.id.tv_comment_date);
        }
    }

    /**
     * Returns a human-readable relative time string from the given timestamp.
     *
     * @param timestamp the timestamp to evaluate
     * @return a relative time string (e.g., "2 hours", "3 days")
     */
    private String getRelativeTime(Date timestamp) {
        if (timestamp == null) return "";

        long diff = System.currentTimeMillis() - timestamp.getTime();
        long seconds = diff / 1000;
        if (seconds < 60) return seconds + " seconds";
        long minutes = seconds / 60;
        if (minutes < 60) return minutes + " minutes";
        long hours = minutes / 60;
        if (hours < 24) return hours + " hours";
        long days = hours / 24;
        if (days < 7) return days + " days";
        long weeks = days / 7;
        if (weeks < 4) return weeks + " weeks";
        long months = days / 30;
        if (months < 12) return months + " months";
        long years = days / 365;
        return years + " years";
    }
}
