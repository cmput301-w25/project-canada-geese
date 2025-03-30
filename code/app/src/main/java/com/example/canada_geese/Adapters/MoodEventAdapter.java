package com.example.canada_geese.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.canada_geese.Models.MoodEventModel;
import com.example.canada_geese.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.widget.PopupMenu;

import java.util.Set;


import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class MoodEventAdapter extends RecyclerView.Adapter<MoodEventAdapter.ViewHolder> {
    private List<MoodEventModel> moodEventList;
    private List<MoodEventModel> moodEventListFull;
    private Context context;
    private int expandedPosition = -1;
    private boolean isInEditMode = false;


    public interface OnMoodEventClickListener {
        void onMoodEventClick(MoodEventModel moodEvent);
    }
    public interface OnMoodEventLongClickListener {
        boolean onMoodEventLongClick(MoodEventModel moodEvent);
    }
    public interface OnMoodEventEditListener {
        void onMoodEventEdit(MoodEventModel moodEvent, int position);
    }
    public interface OnCommentClickListener {
        void onCommentClick(MoodEventModel moodEvent);
    }

    private OnMoodEventClickListener clickListener;
    private OnMoodEventLongClickListener longClickListener;
    private OnMoodEventEditListener editListener;
    private OnCommentClickListener commentClickListener;

    private boolean isFriendPage = false;
    private Map<String, String> uidToUsernameMap = new HashMap<>();

    public MoodEventAdapter(List<MoodEventModel> moodEventList, Context context, boolean isFriendPage) {
        this.moodEventList = new ArrayList<>(moodEventList);
        this.moodEventListFull = new ArrayList<>(moodEventList);
        this.context = context;
        this.isFriendPage = isFriendPage;
    }

    public void setUidToUsernameMap(Map<String, String> uidToUsernameMap) {
        this.uidToUsernameMap = uidToUsernameMap;
    }

    public void setOnMoodEventClickListener(OnMoodEventClickListener listener) {
        this.clickListener = listener;
    }
    public void setOnMoodEventLongClickListener(OnMoodEventLongClickListener listener) {
        this.longClickListener = listener;
    }
    public void setOnMoodEventEditListener(OnMoodEventEditListener listener) {
        this.editListener = listener;
    }
    public void setOnCommentClickListener(OnCommentClickListener listener) {
        this.commentClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mood_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    /*public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MoodEventModel event = moodEventList.get(position);
        if (event == null) return;

        holder.moodText.setText(event.getEmotion());
        holder.timestamp.setText(event.getFormattedTimestamp());
        holder.moodEmoji.setText(event.getEmoji());

        String desc = event.getDescription();
        if (desc == null || desc.trim().isEmpty()) {
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setText(desc);
            holder.description.setVisibility(View.VISIBLE);
        }

        if (isFriendPage && holder.usernameView != null) {
            holder.usernameView.setVisibility(View.VISIBLE);
            String displayName = uidToUsernameMap.get(event.getUserId());
            holder.usernameView.setText("Posted by: " + (displayName != null ? displayName : "Unknown"));
        } else if (holder.usernameView != null) {
            holder.usernameView.setVisibility(View.GONE);
        }

        int color = event.getColor();
        holder.cardView.setCardBackgroundColor(color != 0 ? context.getResources().getColor(color) : context.getResources().getColor(R.color.colorPrimaryDark));

        boolean isExpanded = position == expandedPosition;
        holder.detailsContainer.setVisibility(isExpanded && !isInEditMode ? View.VISIBLE : View.GONE);
        holder.editContainer.setVisibility(isInEditMode && isExpanded ? View.VISIBLE : View.GONE);

        if (isExpanded) {
            if (isInEditMode) populateEditFields(holder, event);
            else populateDetails(holder, event);
        }

        if (isFriendPage) {
            holder.optionsMenuButton.setVisibility(View.GONE);
        } else {
            holder.optionsMenuButton.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        }


        holder.itemView.setOnClickListener(v -> {
            expandedPosition = (isExpanded && !isInEditMode) ? -1 : position;
            isInEditMode = false;
            notifyDataSetChanged();
        });

        holder.itemView.setOnLongClickListener(v -> longClickListener != null && longClickListener.onMoodEventLongClick(event));

        holder.commentButton.setOnClickListener(v -> {
            if (commentClickListener != null) commentClickListener.onCommentClick(event);
        });

    }*/
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MoodEventModel event = moodEventList.get(position);
        if (event == null) return;

        holder.moodText.setText(event.getEmotion());
        holder.timestamp.setText(event.getFormattedTimestamp());
        holder.moodEmoji.setText(event.getEmoji());

        String desc = event.getDescription();
        if (desc == null || desc.trim().isEmpty()) {
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setText(desc);
            holder.description.setVisibility(View.VISIBLE);
        }

        if (isFriendPage && holder.usernameView != null) {
            holder.usernameView.setVisibility(View.VISIBLE);
            String displayName = uidToUsernameMap.get(event.getUserId());
            holder.usernameView.setText("Posted by: " + (displayName != null ? displayName : "Unknown"));
        } else if (holder.usernameView != null) {
            holder.usernameView.setVisibility(View.GONE);
        }

        int color = event.getColor();
        holder.cardView.setCardBackgroundColor(color != 0 ? context.getResources().getColor(color) : context.getResources().getColor(R.color.colorPrimaryDark));

        boolean isExpanded = position == expandedPosition;
        holder.detailsContainer.setVisibility(isExpanded && !isInEditMode ? View.VISIBLE : View.GONE);
        holder.editContainer.setVisibility(isInEditMode && isExpanded ? View.VISIBLE : View.GONE);

        if (isExpanded) {
            if (isInEditMode) populateEditFields(holder, event);
            else populateDetails(holder, event);
        }

        if (isFriendPage) {
            holder.optionsMenuButton.setVisibility(View.GONE);
        } else {
            holder.optionsMenuButton.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        }

        // **Toggle Expand/Collapse Icon**
        if (isExpanded) {
            holder.expandIcon.setImageResource(R.drawable.baseline_expand_less_24);
        } else {
            holder.expandIcon.setImageResource(R.drawable.baseline_expand_more_24);
        }

        // **Set Click Listener for Expanding/Collapsing**
        holder.itemView.setOnClickListener(v -> {
            expandedPosition = (isExpanded && !isInEditMode) ? -1 : position;
            isInEditMode = false;
            notifyDataSetChanged();
        });

        holder.itemView.setOnLongClickListener(v -> longClickListener != null && longClickListener.onMoodEventLongClick(event));

        holder.commentButton.setOnClickListener(v -> {
            if (commentClickListener != null) commentClickListener.onCommentClick(event);
        });
    }


    private void populateDetails(ViewHolder holder, MoodEventModel event) {


        holder.socialSituation.setText(event.getSocialSituation() != null ? event.getSocialSituation() : "Not specified");

        holder.imageContainer.removeAllViews();
        List<String> urls = event.getImageUrls();

        if (urls != null && !urls.isEmpty()) {
            holder.imageContainer.setVisibility(View.VISIBLE);

            for (String url : urls) {
                ImageView img = new ImageView(context);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 16, 0, 16);

                img.setLayoutParams(params);
                img.setScaleType(ImageView.ScaleType.FIT_CENTER);
                img.setAdjustViewBounds(true); // 🔥 key for portrait pics

                Glide.with(context)
                        .load(url)
                        .into(img);

                holder.imageContainer.addView(img);
            }
        } else {
            holder.imageContainer.setVisibility(View.GONE);
        }

        if (event.HasLocation()) {
            holder.mapView.setVisibility(View.VISIBLE);
            if (holder.locationLabel != null) holder.locationLabel.setVisibility(View.VISIBLE); // 👈 Show label

            holder.mapView.onCreate(null);
            holder.mapView.getMapAsync(googleMap -> {
                LatLng location = new LatLng(event.getLatitude(), event.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
                googleMap.addMarker(new MarkerOptions().position(location).title("Mood Location"));
            });
        } else {
            holder.mapView.setVisibility(View.GONE);
            if (holder.locationLabel != null) holder.locationLabel.setVisibility(View.GONE); // 👈 Hide label
        }

//        holder.editButton.setOnClickListener(v -> {
//            isInEditMode = true;
//            notifyItemChanged(expandedPosition);
//            if (editListener != null) editListener.onMoodEventEdit(event, holder.getAdapterPosition());
//        });
//
//        holder.deleteButton.setOnClickListener(v -> {
//            if (longClickListener != null) longClickListener.onMoodEventLongClick(event);
//        });

        holder.optionsMenuButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(holder.optionsMenuButton.getContext(), holder.optionsMenuButton);
            popup.getMenuInflater().inflate(R.menu.mood_options_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.option_edit) {
                    isInEditMode = true;
                    notifyItemChanged(holder.getAdapterPosition());
                    if (editListener != null) {
                        editListener.onMoodEventEdit(event, holder.getAdapterPosition());
                    }
                    return true;
                } else if (itemId == R.id.option_delete) {
                    if (longClickListener != null) {
                        longClickListener.onMoodEventLongClick(event);
                    }
                    return true;
                }
                return false;
            });

            popup.show();
        });

        if (isFriendPage) {
            holder.optionsMenuButton.setVisibility(View.GONE);
        } else {
            holder.optionsMenuButton.setVisibility(View.VISIBLE);
        }
    }

    private void populateEditFields(ViewHolder holder, MoodEventModel event) {
        holder.privateMoodEdit.setChecked(event.isPublic());
        holder.descriptionEdit.setText(event.getDescription());

        holder.saveButton.setOnClickListener(v -> {
            MoodEventModel updatedMood = new MoodEventModel(
                    event.getEmotion(),
                    holder.descriptionEdit.getText().toString(),
                    event.getTimestamp(),
                    event.getEmoji(),
                    event.getColor(),
                    holder.privateMoodEdit.isChecked(),
                    event.HasLocation(),
                    event.getLatitude(),
                    event.getLongitude()
            );
            if (clickListener != null) clickListener.onMoodEventClick(updatedMood);
            isInEditMode = false;
            notifyItemChanged(expandedPosition);
        });

        holder.cancelButton.setOnClickListener(v -> {
            isInEditMode = false;
            notifyItemChanged(expandedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return moodEventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView moodText, timestamp, moodEmoji, description, socialSituation;
        EditText descriptionEdit;
        CardView cardView;
        View detailsContainer, editContainer;
        CheckBox privateMoodCheck, privateMoodEdit;
        Button editButton, deleteButton, saveButton, cancelButton;
        ImageButton commentButton;
        MapView mapView;
        TextView usernameView;
        LinearLayout imageContainer;
        TextView locationLabel;
        ImageButton optionsMenuButton;
        ImageView expandIcon;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            moodText = itemView.findViewById(R.id.mood_name);
            timestamp = itemView.findViewById(R.id.timestamp);
            moodEmoji = itemView.findViewById(R.id.mood_emoji);
            cardView = itemView.findViewById(R.id.card_view);
            commentButton = itemView.findViewById(R.id.comment_button2); //HERE
            detailsContainer = itemView.findViewById(R.id.details_container);
            description = itemView.findViewById(R.id.tv_description);

            editButton = itemView.findViewById(R.id.btn_edit);
            deleteButton = itemView.findViewById(R.id.btn_delete);
            editContainer = itemView.findViewById(R.id.edit_container);
            descriptionEdit = itemView.findViewById(R.id.et_description_edit);
            privateMoodEdit = itemView.findViewById(R.id.cb_private_mood_edit);
            saveButton = itemView.findViewById(R.id.btn_save);
            cancelButton = itemView.findViewById(R.id.btn_cancel);
            socialSituation = itemView.findViewById(R.id.tv_social_situation);
            mapView = itemView.findViewById(R.id.map_view);
            usernameView = itemView.findViewById(R.id.tv_username);
            imageContainer = itemView.findViewById(R.id.image_container);
            locationLabel = itemView.findViewById(R.id.tv_location_label);
            optionsMenuButton = itemView.findViewById(R.id.options_menu_button);
            expandIcon = itemView.findViewById(R.id.expand_icon);

        }
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
        expandedPosition = -1;
        isInEditMode = false;
        notifyDataSetChanged();
    }

   public void filter(String query, boolean last7Days, Set<String> selectedMoods, boolean isPrivateSelected) {
       this.moodEventList.clear();
       long cutoff = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000);
       for (MoodEventModel e : moodEventListFull) {
           if (e.getTimestamp() == null) continue;
           boolean matchDate = !last7Days || e.getTimestamp().getTime() >= cutoff;
           boolean matchQuery = query.isEmpty() || e.getDescription().toLowerCase().contains(query.toLowerCase());

           // Check if this mood event matches any of the selected moods
           boolean matchMood = selectedMoods.isEmpty() || selectedMoods.contains(e.getEmotion());

           boolean matchesPrivacy = !isPrivateSelected || e.hasTriggerWarning();
           if (matchDate && matchQuery && matchMood && matchesPrivacy) moodEventList.add(e);
       }
       expandedPosition = -1;
       isInEditMode = false;
       notifyDataSetChanged();
   }

    public void collapseExpandedItem() {
        if (expandedPosition != -1) {
            int p = expandedPosition;
            expandedPosition = -1;
            isInEditMode = false;
            notifyItemChanged(p);
        }
    }
}