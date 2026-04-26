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
import com.example.myapplication.model.Post;

import java.util.ArrayList;

public class PostAdapter extends BaseAdapter {

    Context context;
    ArrayList<Post> list;
    String avatarUrl;

    // Constructor
    public PostAdapter(Context context, ArrayList<Post> list, String avatarUrl) {
        this.context = context;
        this.list = list;
        this.avatarUrl = avatarUrl;
    }

    // count item
    @Override
    public int getCount() {
        return list.size();
    }

    //get item
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    // ID item
    @Override
    public long getItemId(int position) {
        return position;
    }

    // ViewHolder
    static class ViewHolder {
        ImageView imgAvatar;
        TextView txtTitle, txtContent, txtDate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);

            holder = new ViewHolder();
            holder.txtTitle = view.findViewById(R.id.txtTitle);
            holder.txtContent = view.findViewById(R.id.txtContent);
            holder.txtDate = view.findViewById(R.id.txtDate);
            holder.imgAvatar = view.findViewById(R.id.imgAvatar);

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        // get data
        Post post = list.get(position);
        holder.txtTitle.setText(post.title);
        holder.txtContent.setText(post.content);
        holder.txtDate.setText(post.date);
        Glide.with(context)
                .load(avatarUrl != null && !avatarUrl.isEmpty()
                        ? avatarUrl
                        : R.drawable.default_avatar)
                .circleCrop()
                .into(holder.imgAvatar);

        return view;
    }
}
