package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.model.Friend;

import java.util.ArrayList;

public class FriendAdapter extends BaseAdapter {

    Context context;
    ArrayList<Friend> list;
    DatabaseHelper db;

    public FriendAdapter(Context context, ArrayList<Friend> list) {
        this.context = context;
        this.list = list;

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
        ImageView btnUnfriend;
    }

    @Override
    public View getView(int position,
                        View convertView,
                        ViewGroup parent) {

        ViewHolder holder;
        View view;

        if (convertView == null) {

            view = LayoutInflater.from(context)
                    .inflate(R.layout.item_friend, parent, false);

            holder = new ViewHolder();

            holder.imgAvatar = view.findViewById(R.id.imgAvatar);
            holder.txtName = view.findViewById(R.id.txtName);
            holder.btnUnfriend = view.findViewById(R.id.btnUnfriend);

            view.setTag(holder);

        } else {

            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        Friend friend = list.get(position);

        String name = db.getName(friend.friendEmail);
        String avatar = db.getAvatar(friend.friendEmail);

        holder.txtName.setText(name);

        Glide.with(context)
                .load(
                        avatar != null && !avatar.isEmpty()
                                ? avatar
                                : R.drawable.default_avatar
                )
                .circleCrop()
                .into(holder.imgAvatar);

        holder.btnUnfriend.setOnClickListener(v -> {

            db.unfriend(
                    friend.userEmail,
                    friend.friendEmail
            );

            list.remove(position);

            notifyDataSetChanged();
        });

        return view;
    }
}