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
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.os.Looper;

public class MoodEventAdapter extends RecyclerView.Adapter<MoodEventAdapter.ViewHolder> {
    private List<MoodEventModel> moodEventList;
    private List<MoodEventModel> moodEventListFull;
    private Context context;
    private String currentQuery = "";

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
            holder.moodText.setText(event.getEmotion());
            holder.timestamp.setText(event.getTimestamp());
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView moodText, timestamp, moodEmoji;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            moodText = itemView.findViewById(R.id.mood_name);
            timestamp = itemView.findViewById(R.id.timestamp);
            moodEmoji = itemView.findViewById(R.id.mood_emoji);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }


    public void updateData(List<MoodEventModel> newData) {
        this.moodEventListFull.clear();
        this.moodEventListFull.addAll(newData);  // 🔄 Update the full list for filtering

        this.moodEventList.clear();
        this.moodEventList.addAll(newData);       // 🔄 Update the list displayed in RecyclerView

        filter(currentQuery);                     // 🔄 Apply current filter if any
    }


    public void addItem(MoodEventModel newItem) {

        moodEventListFull.add(0, newItem);
        moodEventList.add(0, newItem);


        new Handler(Looper.getMainLooper()).post(() -> {
            notifyItemInserted(0);
            notifyDataSetChanged();
        });
    }



    public void updateList(List<MoodEventModel> newList) {
        this.moodEventListFull.clear();
        this.moodEventListFull.addAll(newList);

        this.moodEventList.clear();
        this.moodEventList.addAll(newList);
        notifyDataSetChanged();
    }


    public void filter(String query) {
        this.currentQuery = query;
        moodEventList.clear();

        if (query.isEmpty()) {
            moodEventList.addAll(moodEventListFull);
        } else {
            for (MoodEventModel event : moodEventListFull) {
                if (event.getEmotion().toLowerCase().contains(query.toLowerCase())) {
                    moodEventList.add(event);
                }
            }
        }
        notifyDataSetChanged();
    }
}