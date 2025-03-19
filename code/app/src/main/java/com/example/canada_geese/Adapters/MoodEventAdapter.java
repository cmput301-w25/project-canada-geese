package com.example.canada_geese.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.canada_geese.Models.MoodEventModel;
import com.example.canada_geese.R;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.os.Looper;

/**
 * Adapter for displaying a list of mood events in a RecyclerView.
 * Supports expandable items with inline detail view.
 */
public class MoodEventAdapter extends RecyclerView.Adapter<MoodEventAdapter.ViewHolder> {
    private List<MoodEventModel> moodEventList;
    private List<MoodEventModel> moodEventListFull;
    private Context context;
    private String currentQuery = "";
    private OnMoodEventClickListener clickListener;
    private OnMoodEventLongClickListener longClickListener;
    private OnMoodEventEditListener editListener;
    private int expandedPosition = -1; // Track which item is expanded
    private boolean isInEditMode = false; // Track if an item is in edit mode

    /**
     * Listener interface for handling click events on mood items.
     */
    public interface OnMoodEventClickListener {
        void onMoodEventClick(MoodEventModel moodEvent);
    }

    /**
     * Listener interface for handling long click events on mood items.
     */
    public interface OnMoodEventLongClickListener {
        boolean onMoodEventLongClick(MoodEventModel moodEvent);
    }

    /**
     * Listener interface for handling edit events on mood items.
     */
    public interface OnMoodEventEditListener {
        void onMoodEventEdit(MoodEventModel moodEvent, int position);
    }

    /**
     * Constructor for MoodEventAdapter.
     *
     * @param moodEventList Initial list of mood events.
     * @param context       Context for accessing resources.
     */
    public MoodEventAdapter(List<MoodEventModel> moodEventList, Context context) {
        this.moodEventList = new ArrayList<>(moodEventList);
        this.moodEventListFull = new ArrayList<>(moodEventList);
        this.context = context;
    }

    /**
     * Set the click listener for mood event items.
     *
     * @param listener The listener to set.
     */
    public void setOnMoodEventClickListener(OnMoodEventClickListener listener) {
        this.clickListener = listener;
    }

    /**
     * Set the long click listener for mood event items.
     *
     * @param listener The listener to set.
     */
    public void setOnMoodEventLongClickListener(OnMoodEventLongClickListener listener) {
        this.longClickListener = listener;
    }

    /**
     * Set the edit listener for mood event items.
     *
     * @param listener The listener to set.
     */
    public void setOnMoodEventEditListener(OnMoodEventEditListener listener) {
        this.editListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mood_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MoodEventModel event = moodEventList.get(position);

        if (event != null) {
            holder.moodText.setText(event.getEmotion());
            // Use the formatted timestamp string for display
            holder.timestamp.setText(event.getFormattedTimestamp());
            holder.moodEmoji.setText(event.getEmoji());

            int color = event.getColor();
            if (color != 0) {
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(color));
            } else {
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
            }
            // Check if this position is the expanded one
            final boolean isExpanded = position == expandedPosition;

            // Show or hide the details container based on expanded state
            if (holder.detailsContainer != null) {
                holder.detailsContainer.setVisibility(isExpanded && !isInEditMode ? View.VISIBLE : View.GONE);
            }

            // Show or hide the edit container based on edit mode
            if (holder.editContainer != null) {
                holder.editContainer.setVisibility(isExpanded && isInEditMode ? View.VISIBLE : View.GONE);
            }

            if (isExpanded) {
                // Populate details or edit section
                if (isInEditMode) {
                    populateEditFields(holder, event);
                } else {
                    populateDetails(holder, event);
                }
            }

            // Set click listener for expanding/collapsing
            holder.itemView.setOnClickListener(v -> {
                // If this position is already expanded, collapse it
                if (isExpanded) {
                    if (!isInEditMode) { // Don't collapse if in edit mode
                        expandedPosition = -1;
                    }
                } else {
                    // Collapse any currently expanded item
                    int previousExpandedPosition = expandedPosition;
                    expandedPosition = position;

                    // Reset edit mode when expanding a new item
                    isInEditMode = false;

                    // Notify the previous item to collapse (if any)
                    if (previousExpandedPosition != -1) {
                        notifyItemChanged(previousExpandedPosition);
                    }
                }
                // Notify this item to update its expanded state
                notifyItemChanged(position);
            });

            // Long press for delete
            holder.itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    return longClickListener.onMoodEventLongClick(event);
                }
                return false;
            });
        }
    }

    /**
     * Populate the details section of a ViewHolder.
     *
     * @param holder The ViewHolder to populate.
     * @param event The mood event containing the data.
     */
    private void populateDetails(ViewHolder holder, MoodEventModel event) {
        // Set privacy checkbox
        if (holder.privateMoodCheck != null) {
            holder.privateMoodCheck.setChecked(event.isPrivate());
        }

        // Set description
        if (holder.description != null) {
            holder.description.setText(event.getDescription());
        }

        // Setup edit button
        if (holder.editButton != null) {
            holder.editButton.setOnClickListener(v -> {
                isInEditMode = true;
                notifyItemChanged(expandedPosition);

                if (editListener != null) {
                    final int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        editListener.onMoodEventEdit(event, position);
                    }
                }
            });
        }

        // Setup delete button
        if (holder.deleteButton != null) {
            holder.deleteButton.setOnClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onMoodEventLongClick(event);
                }
            });
        }
    }

    /**
     * Populate the edit fields of a ViewHolder.
     *
     * @param holder The ViewHolder to populate.
     * @param event The mood event containing the data.
     */
    // Modified populateEditFields to handle date objects
    private void populateEditFields(ViewHolder holder, MoodEventModel event) {
        // Set privacy checkbox
        if (holder.privateMoodEdit != null) {
            holder.privateMoodEdit.setChecked(event.isPrivate());
        }

        // Set description edit field
        if (holder.descriptionEdit != null) {
            holder.descriptionEdit.setText(event.getDescription());
        }

        // Setup save button
        if (holder.saveButton != null) {
            holder.saveButton.setOnClickListener(v -> {
                // Create an updated mood with the edited values
                String newDescription = holder.descriptionEdit != null ?
                        holder.descriptionEdit.getText().toString() : event.getDescription();
                boolean isPrivate = holder.privateMoodEdit != null ?
                        holder.privateMoodEdit.isChecked() : event.isPrivate();

                MoodEventModel updatedMood = new MoodEventModel(
                        event.getEmotion(),
                        newDescription,
                        event.getTimestamp(), // Keep the original timestamp
                        event.getEmoji(),
                        event.getColor(),
                        isPrivate,
                        event.HasLocation(),
                        event.getLatitude(),
                        event.getLongitude()
                );

                // Call the click listener with the updated mood
                if (clickListener != null) {
                    clickListener.onMoodEventClick(updatedMood);
                }

                // Exit edit mode
                isInEditMode = false;
                notifyItemChanged(expandedPosition);
            });
        }

        // Setup cancel button
        if (holder.cancelButton != null) {
            holder.cancelButton.setOnClickListener(v -> {
                // Exit edit mode without saving
                isInEditMode = false;
                notifyItemChanged(expandedPosition);
            });
        }
    }

    @Override
    public int getItemCount() {
        return moodEventList != null ? moodEventList.size() : 0;
    }

    /**
     * ViewHolder class for managing individual mood event items.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView moodText, timestamp, moodEmoji, description;
        EditText descriptionEdit;
        CardView cardView;
        View detailsContainer, editContainer;
        CheckBox privateMoodCheck, privateMoodEdit;
        Button editButton, deleteButton, saveButton, cancelButton;

        /**
         * Constructor for ViewHolder.
         *
         * @param itemView The view for a single item.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            moodText = itemView.findViewById(R.id.mood_name);
            timestamp = itemView.findViewById(R.id.timestamp);
            moodEmoji = itemView.findViewById(R.id.mood_emoji);
            cardView = itemView.findViewById(R.id.card_view);

            // Details section views
            detailsContainer = itemView.findViewById(R.id.details_container);
            description = itemView.findViewById(R.id.tv_description);
            privateMoodCheck = itemView.findViewById(R.id.cb_private_mood);
            editButton = itemView.findViewById(R.id.btn_edit);
            deleteButton = itemView.findViewById(R.id.btn_delete);

            // Edit section views
            editContainer = itemView.findViewById(R.id.edit_container);
            descriptionEdit = itemView.findViewById(R.id.et_description_edit);
            privateMoodEdit = itemView.findViewById(R.id.cb_private_mood_edit);
            saveButton = itemView.findViewById(R.id.btn_save);
            cancelButton = itemView.findViewById(R.id.btn_cancel);
        }
    }

    /**
     * Adds a new mood event to the list.
     *
     * @param newItem The new mood event to add.
     */
    public void addItem(MoodEventModel newItem) {
        moodEventListFull.add(0, newItem);
        moodEventList.add(0, newItem);

        new Handler(Looper.getMainLooper()).post(() -> {
            notifyItemInserted(0);
            notifyDataSetChanged();
        });
    }

    /**
     * Replaces the current list of mood events with a new list.
     *
     * @param newList The new list of mood events.
     */
    public void updateList(List<MoodEventModel> newList) {
        this.moodEventListFull.clear();
        this.moodEventListFull.addAll(newList);

        this.moodEventList.clear();
        this.moodEventList.addAll(newList);

        // Reset expanded position when list is updated
        expandedPosition = -1;
        isInEditMode = false;
        notifyDataSetChanged();
    }

    /**
     * Filters the mood events based on a query string.
     *
     * @param query The search query.
     */
    public void filter(String query, boolean last7Days, String selectedMood) {
        this.currentQuery = query;
        moodEventList.clear();
        long cutoffTime = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000);

        for (MoodEventModel event : moodEventListFull) {
            boolean matchesQuery = query.isEmpty() || event.getEmotion().toLowerCase().contains(query.toLowerCase());
            boolean matchesMood = selectedMood.isEmpty() || event.getEmotion().equalsIgnoreCase(selectedMood);
            boolean matchesDate = !last7Days || event.getTimestamp().getTime() >= cutoffTime;

            if (matchesQuery && matchesMood && matchesDate) {
                moodEventList.add(event);
            }
        }

        // Reset expanded position when filter changes
        expandedPosition = -1;
        isInEditMode = false;
        notifyDataSetChanged();
    }

    /**
     * Collapse any expanded item.
     */
    public void collapseExpandedItem() {
        if (expandedPosition != -1) {
            int position = expandedPosition;
            expandedPosition = -1;
            isInEditMode = false;
            notifyItemChanged(position);
        }
    }
}