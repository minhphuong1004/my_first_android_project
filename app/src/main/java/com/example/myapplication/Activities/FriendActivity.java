package com.example.myapplication.Activities;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.adapter.FriendAdapter;
import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.model.Friend;

import java.util.ArrayList;

public class FriendActivity extends AppCompatActivity {

    ListView listFriend;

    ArrayList<Friend> friendList;

    FriendAdapter adapter;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_list);

        listFriend = findViewById(R.id.listFriend);

        db = new DatabaseHelper(this);

        friendList = new ArrayList<>();

        String email = getIntent().getStringExtra("email");

        Cursor cursor = db.getFriends(email);

        while (cursor.moveToNext()) {

            String userEmail = cursor.getString(
                    cursor.getColumnIndexOrThrow("user_email")
            );

            String friendEmail = cursor.getString(
                    cursor.getColumnIndexOrThrow("friend_email")
            );

            friendList.add(
                    new Friend(userEmail, friendEmail)
            );
        }

        cursor.close();

        adapter = new FriendAdapter(this, friendList);

        listFriend.setAdapter(adapter);
    }
}