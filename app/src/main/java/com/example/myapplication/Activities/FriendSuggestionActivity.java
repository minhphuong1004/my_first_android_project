package com.example.myapplication.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.example.myapplication.adapter.FriendSuggestionAdapter;
import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.model.FriendSuggestion;

import java.util.ArrayList;

public class FriendSuggestionActivity extends AppCompatActivity {

    ListView listSuggestion;

    ArrayList<FriendSuggestion> suggestionList;

    FriendSuggestionAdapter adapter;

    DatabaseHelper db;

    String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_suggestion);

        listSuggestion = findViewById(R.id.listSuggestion);

        db = new DatabaseHelper(this);

        suggestionList = new ArrayList<>();

        currentUserEmail = getIntent().getStringExtra("email");

        checkPermission();
    }

    private void checkPermission() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    1
            );

        } else {

            loadSuggestions();
        }
    }

    private void loadSuggestions() {

        ArrayList<String> contactNumbers = new ArrayList<>();

        Cursor contactCursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        while (contactCursor != null && contactCursor.moveToNext()) {

            String phone = contactCursor.getString(
                    contactCursor.getColumnIndexOrThrow(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    )
            );

            phone = normalizePhone(phone);

            contactNumbers.add(phone);
        }

        if (contactCursor != null) {
            contactCursor.close();
        }

        Cursor userCursor = db.getAllUsers();

        while (userCursor.moveToNext()) {

            String name = userCursor.getString(
                    userCursor.getColumnIndexOrThrow("name")
            );

            String email = userCursor.getString(
                    userCursor.getColumnIndexOrThrow("email")
            );

            String phone = userCursor.getString(
                    userCursor.getColumnIndexOrThrow("phoneNumber")
            );

            String avatar = userCursor.getString(
                    userCursor.getColumnIndexOrThrow("avatarURL")
            );

            phone = normalizePhone(phone);

            boolean isMe = email.equals(currentUserEmail);

            boolean isFriend = db.isFriend(
                    currentUserEmail,
                    email
            );

            boolean hasRequest = db.hasPendingRequest(
                    currentUserEmail,
                    email
            );

            if (contactNumbers.contains(phone)
                    && !isMe
                    && !isFriend
                    && !hasRequest) {

                suggestionList.add(
                        new FriendSuggestion(
                                name,
                                email,
                                phone,
                                avatar
                        )
                );
            }
        }

        userCursor.close();

        adapter = new FriendSuggestionAdapter(
                this,
                suggestionList,
                currentUserEmail
        );

        listSuggestion.setAdapter(adapter);
    }

    private String normalizePhone(String phone) {

        if (phone == null) return "";

        return phone
                .replace(" ", "")
                .replace("-", "")
                .replace("(", "")
                .replace(")", "");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == 1
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            loadSuggestions();
        }
    }
}