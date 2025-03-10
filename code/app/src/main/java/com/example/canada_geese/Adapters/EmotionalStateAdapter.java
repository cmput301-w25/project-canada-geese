package com.example.canada_geese.Adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.canada_geese.R;
import com.example.canada_geese.Models.EmotionalState;
import com.google.android.material.card.MaterialCardView;

import java.util.Arrays;
import java.util.List;

/**
 * Adapter for displaying a list of emotional states in a RecyclerView.
 */
public class EmotionalStateAdapter extends RecyclerView.Adapter<EmotionalStateAdapter.MoodViewHolder> {
    private final List<EmotionalState> states;
    private final OnEmotionalStateSelectedListener listener;
    private EmotionalState selectedState;

    /**
     * Listener interface for handling emotional state selection.
     */
    public interface OnEmotionalStateSelectedListener {
        /**
         * Called when an emotional state is selected.
         *
         * @param state The selected emotional state.
         */
        void onEmotionalStateSelected(EmotionalState state);
    }

    /**
     * Constructor for EmotionalStateAdapter.
     *
     * @param listener Listener for handling emotional state selection.
     */
    public EmotionalStateAdapter(OnEmotionalStateSelectedListener listener) {
        this.states = Arrays.asList(EmotionalState.values());
        this.listener = listener;
        this.selectedState = null;
    }

    @NonNull
    @Override
    public MoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_emotional_state, parent, false);
        return new MoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoodViewHolder holder, int position) {
        EmotionalState state = states.get(position);
        holder.bind(state, state == selectedState);
    }

    @Override
    public int getItemCount() {
        return states.size();
    }

    /**
     * ViewHolder class for managing individual emotional state items.
     */
    public class MoodViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final TextView emojiView;
        private final TextView nameView;

        /**
         * Constructor for MoodViewHolder.
         *
         * @param itemView The view for a single item.
         */
        public MoodViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            emojiView = itemView.findViewById(R.id.emoji_view);
            nameView = itemView.findViewById(R.id.name_view);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    EmotionalState state = states.get(position);
                    setSelectedState(state);
                    listener.onEmotionalStateSelected(state);
                }
            });
        }

        /**
         * Binds the data to the view holder.
         *
         * @param state      The emotional state to bind.
         * @param isSelected Whether this state is currently selected.
         */
        public void bind(EmotionalState state, boolean isSelected) {
            emojiView.setText(state.getEmoji());
            nameView.setText(state.getDisplayName());
            cardView.setCardBackgroundColor(
                    itemView.getContext().getColor(state.getColorResId())
            );

            // Use integers instead of ColorStateList
            cardView.setStrokeWidth(isSelected ? 4 : 0);
            cardView.setStrokeColor(isSelected ?
                    itemView.getContext().getColor(R.color.primary) :
                    0);
        }
    }

    /**
     * Sets the selected emotional state and updates the view.
     *
     * @param state The new selected state.
     */
    public void setSelectedState(EmotionalState state) {
        EmotionalState oldState = this.selectedState;
        this.selectedState = state;

        if (oldState != null) {
            notifyItemChanged(states.indexOf(oldState));
        }
        if (state != null) {
            notifyItemChanged(states.indexOf(state));
        }
    }
}