package com.example.canada_geese.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.canada_geese.Managers.DatabaseManager;
import com.example.canada_geese.Models.CommentModel;
import com.example.canada_geese.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<CommentModel> commentList;
    private Context context;
    private String moodEventId;  // The mood event ID that these comments belong to

    public CommentAdapter(List<CommentModel> commentList, Context context, String moodEventId) {
        this.commentList = commentList;
        this.context = context;
        this.moodEventId = moodEventId;
    }

    public void updateComments(List<CommentModel> newComments) {
        this.commentList = newComments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentModel comment = commentList.get(position);
        holder.tvAuthor.setText(comment.getAuthor());
        holder.tvCommentText.setText(comment.getText());
        String relativeTime = getRelativeTime(comment.getTimestamp());
        holder.tvCommentDate.setText(relativeTime);

        // Set long-press listener if the current user posted this comment
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

    @Override
    public int getItemCount() {
        return commentList != null ? commentList.size() : 0;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthor, tvCommentText, tvCommentDate;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tv_comment_author);
            tvCommentText = itemView.findViewById(R.id.tv_comment_text);
            tvCommentDate = itemView.findViewById(R.id.tv_comment_date);
        }
    }

    // Helper method to calculate relative time
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