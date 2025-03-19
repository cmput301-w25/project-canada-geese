package com.example.canada_geese.Adapters;

import static androidx.test.InstrumentationRegistry.getContext;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.canada_geese.Models.Users;
import com.example.canada_geese.R;

import java.util.ArrayList;
import java.util.List;



public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private List<Users> usersList;
    private List<Users> filteredusersList;
    private Context context;
    private String currentQuery = "";
    private onItemClickListener listener;



    // constructor
    public UsersAdapter(List<Users> usersList, Context context) {
        this.usersList = new ArrayList<>(usersList);
        this.filteredusersList = new ArrayList<>(usersList);
        this.context = context;
    }

    public interface onItemClickListener {
        void onItemClick(Users users);
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
        Users users = filteredusersList.get(position);
        if (users != null) {
            holder.text_username.setText(users.getUsername());
            holder.profile_image.setImageResource(R.drawable.profile);
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    Log.d("UsersAdapter", "User clicked: " + users.getUsername());  // Log click event
                    listener.onItemClick(users);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return filteredusersList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text_username;
        ImageView profile_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text_username = itemView.findViewById(R.id.text_username);
            profile_image = itemView.findViewById(R.id.profile_image);
        }
    }

    public void updateList(List<Users> newData) {
        this.usersList.clear();
        this.usersList.addAll(newData);

        filter(currentQuery);
    }

    public void filter(String query) {
        this.currentQuery = query;
        filteredusersList.clear();

        for (Users user : usersList) {
            if (user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                filteredusersList.add(user);
            }
        }
        notifyDataSetChanged();
    }
}
