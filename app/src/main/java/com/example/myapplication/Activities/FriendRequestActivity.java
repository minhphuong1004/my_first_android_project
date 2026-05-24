package com.example.myapplication.Activities;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.adapter.FriendRequestAdapter;
import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.model.FriendRequest;

import java.util.ArrayList;

public class FriendRequestActivity extends AppCompatActivity {

    ListView listRequest;

    ArrayList<FriendRequest> requestList;

    FriendRequestAdapter adapter;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_request);

        listRequest = findViewById(R.id.listRequest);

        db = new DatabaseHelper(this);

        requestList = new ArrayList<>();

        String email = getIntent().getStringExtra("email");

        Cursor cursor = db.getFriendRequests(email);

        while (cursor.moveToNext()) {

            String sender = cursor.getString(
                    cursor.getColumnIndexOrThrow("sender_email")
            );

            String receiver = cursor.getString(
                    cursor.getColumnIndexOrThrow("receiver_email")
            );

            String status = cursor.getString(
                    cursor.getColumnIndexOrThrow("status")
            );

            requestList.add(
                    new FriendRequest(sender, receiver, status)
            );
        }

        cursor.close();

        adapter = new FriendRequestAdapter(this, requestList);

        listRequest.setAdapter(adapter);
    }
}