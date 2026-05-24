package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.model.FriendSuggestion;

import java.util.ArrayList;

public class FriendSuggestionAdapter extends BaseAdapter {

    Context context;
    ArrayList<FriendSuggestion> list;

    DatabaseHelper db;

    String currentUserEmail;

    public FriendSuggestionAdapter(Context context,
                                   ArrayList<FriendSuggestion> list,
                                   String currentUserEmail) {

        this.context = context;
        this.list = list;
        this.currentUserEmail = currentUserEmail;

        db = new DatabaseHelper(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {

        ImageView imgAvatar;
        TextView txtName;
        Button btnAdd;
    }

    @Override
    public View getView(int position,
                        View convertView,
                        ViewGroup parent) {

        ViewHolder holder;
        View view;

        if (convertView == null) {

            view = LayoutInflater.from(context)
                    .inflate(R.layout.item_friend_suggestion,
                            parent,
                            false);

            holder = new ViewHolder();

            holder.imgAvatar = view.findViewById(R.id.imgAvatar);
            holder.txtName = view.findViewById(R.id.txtName);
            holder.btnAdd = view.findViewById(R.id.btnAddfriend);

            view.setTag(holder);

        } else {

            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        FriendSuggestion suggestion = list.get(position);

        holder.txtName.setText(suggestion.name);

        Glide.with(context)
                .load(
                        suggestion.avatarUrl != null
                                && !suggestion.avatarUrl.isEmpty()
                                ? suggestion.avatarUrl
                                : R.drawable.default_avatar
                )
                .circleCrop()
                .into(holder.imgAvatar);

        holder.btnAdd.setOnClickListener(v -> {

            db.sendFriendRequest(
                    currentUserEmail,
                    suggestion.email
            );

            list.remove(position);

            notifyDataSetChanged();
        });

        return view;
    }
}