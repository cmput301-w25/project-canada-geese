package com.example.canada_geese.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.canada_geese.Models.MoodEventModel;
import com.example.canada_geese.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.os.Handler;
import android.os.Looper;

/**
 * Adapter for displaying a list of mood events in a RecyclerView.
 */
public class MoodEventAdapter extends RecyclerView.Adapter<MoodEventAdapter.ViewHolder> {
    private List<MoodEventModel> moodEventList;
    private List<MoodEventModel> moodEventListFull;
    private Context context;
    private String currentQuery = "";
    private String selectedEmotion = "All";
    private boolean filterLast7Days = false;

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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mood_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MoodEventModel event = moodEventList.get(position);

        if (event != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            holder.moodText.setText(event.getEmotion());
            holder.timestamp.setText(dateFormat.format(event.getTimestamp()));
            holder.moodEmoji.setText(event.getEmoji());

            int color = event.getColor();
            if (color != 0) {
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(color));
            } else {
                holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
            }
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
        TextView moodText, timestamp, moodEmoji;
        CardView cardView;

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
        }
    }

    public void setFilters(String selectedEmotion, boolean filterLast7Days) {
        this.selectedEmotion = selectedEmotion;
        this.filterLast7Days = filterLast7Days;
        filter(currentQuery);
    }

    /**
     * Updates the adapter with a new list of mood events.
     *
     * @param newData The new list of mood events.
     */
    public void updateData(List<MoodEventModel> newData) {
        this.moodEventListFull.clear();
        this.moodEventListFull.addAll(newData);

        this.moodEventList.clear();
        this.moodEventList.addAll(newData);

        filter(currentQuery);
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
        notifyDataSetChanged();
    }

    /**
     * Filters the mood events based on a query string.
     *
     * @param query The search query.
     */
    public void filter(String query) {
        this.currentQuery = query;
        moodEventList.clear();

        long cutoffTime = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000);

        for (MoodEventModel event : moodEventListFull) {
            boolean matchesQuery = query.isEmpty() || event.getEmotion().toLowerCase().contains(query.toLowerCase());
            boolean matchesEmotion = selectedEmotion == null || selectedEmotion.equals("All") || event.getEmotion().equalsIgnoreCase(selectedEmotion);
            boolean matchesDate = !filterLast7Days || event.getTimestamp().getTime() >= cutoffTime;

            if (matchesQuery && matchesEmotion && matchesDate) {
                moodEventList.add(event);
            }
        }
        notifyDataSetChanged();
    }
}