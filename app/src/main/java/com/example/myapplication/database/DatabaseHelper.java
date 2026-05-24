package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.model.Post;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "UserDB";
    private static final int DB_VERSION = 3;

    public static final String TABLE_NAME = "users";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //users
        String createTable = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "email TEXT UNIQUE, " +
                "password TEXT," +
                "phoneNumber TEXT UNIQUE, " +
                "address TEXT," +
                "avatarURL TEXT," +
                "description TEXT)";

        //friend requests
        String createFriendRequestTable = "CREATE TABLE friend_requests (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "sender_email TEXT, " +
                "receiver_email TEXT, " +
                "status TEXT)";

        //friends
        String createFriendTable = "CREATE TABLE friends (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_email TEXT, " +
                "friend_email TEXT)";

        //posts
        String createPostTable = "CREATE TABLE posts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_email TEXT, " +
                "content TEXT, " +
                "date TEXT)";


        db.execSQL(createTable);
        db.execSQL(createPostTable);
        db.execSQL(createFriendRequestTable);
        db.execSQL(createFriendTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS posts");
        db.execSQL("DROP TABLE IF EXISTS friend_requests");
        db.execSQL("DROP TABLE IF EXISTS friends");
        onCreate(db);
    }

    // insert user
    public boolean insertUser(String name, String email, String password, String phoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("email", email);
        values.put("phoneNumber", phoneNumber);
        values.put("password", password);

        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    // check login
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE email=? AND password=?",
                new String[]{email, password}
        );

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }


    //get user
    public Cursor getUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                "users",
                null,
                "email=?",
                new String[]{email},
                null,
                null,
                null
        );
    }


    //get username
    public String getName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT name FROM users WHERE email=?",
                new String[]{email}
        );

        if (cursor.moveToFirst()) {
            String name = cursor.getString(0);
            cursor.close();
            return name;
        }

        cursor.close();
        return "";
    }

    //get avatar
    public String getAvatar(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT avatarURL FROM users WHERE email = ?",
                new String[]{email}
        );

        if (cursor.moveToFirst()) {
            String avatar = cursor.getString(0);
            cursor.close();
            return avatar;
        }

        cursor.close();
        return null;
    }


    //update user
    public boolean updateUser(String email, String name, String phoneNumber, String address, String avatarURL, String description) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("phoneNumber", phoneNumber);
        values.put("address", address);
        values.put("avatarURL", avatarURL);
        values.put("description", description);
        int result = db.update("users", values, "email=?", new String[]{email});
        return result > 0;
    }

    // insert post
    public void insertPost(String email, String content) {
        SQLiteDatabase db = this.getWritableDatabase();

        String datetime = new java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                java.util.Locale.getDefault()
        ).format(new java.util.Date());

        ContentValues values = new ContentValues();
        values.put("user_email", email);
        values.put("content", content);
        values.put("date", datetime);

        db.insert("posts", null, values);
    }

    //get posts
    public ArrayList<Post> getPosts(String email) {
        ArrayList<Post> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT content, date FROM posts WHERE user_email=?",
                new String[]{email}
        );

        while (cursor.moveToNext()) {
            String content = cursor.getString(0);
            String date = cursor.getString(1);
            list.add(new Post(email, content, date));
        }

        cursor.close();
        return list;
    }

    //friend request
    public void sendFriendRequest(String sender, String receiver) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("sender_email", sender);
        values.put("receiver_email", receiver);
        values.put("status", "pending");

        db.insert("friend_requests", null, values);
    }


    //accept request
    public void acceptFriendRequest(String sender, String receiver) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("status", "accepted");

        db.update(
                "friend_requests",
                values,
                "sender_email=? AND receiver_email=?",
                new String[]{sender, receiver}
        );

        ContentValues friend1 = new ContentValues();
        friend1.put("user_email", sender);
        friend1.put("friend_email", receiver);

        ContentValues friend2 = new ContentValues();
        friend2.put("user_email", receiver);
        friend2.put("friend_email", sender);

        db.insert("friends", null, friend1);
        db.insert("friends", null, friend2);
    }

    //unfriend
    public void unfriend(String userEmail,
                         String friendEmail) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(
                "friends",
                "user_email=? AND friend_email=?",
                new String[]{userEmail, friendEmail}
        );

        db.delete(
                "friends",
                "user_email=? AND friend_email=?",
                new String[]{friendEmail, userEmail}
        );
    }


    //load friedn request
    public Cursor getFriendRequests(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM friend_requests WHERE receiver_email=? AND status='pending'",
                new String[]{email}
        );
    }


    //load friends
    public Cursor getFriends(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM friends WHERE user_email=?",
                new String[]{email}
        );
    }

    //get all users
    public Cursor getAllUsers() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM users",
                null
        );
    }

    //ís friend?
    public boolean isFriend(String userEmail,
                            String friendEmail) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM friends " +
                        "WHERE user_email=? " +
                        "AND friend_email=?",
                new String[]{userEmail, friendEmail}
        );

        boolean exists = cursor.moveToFirst();

        cursor.close();

        return exists;
    }

    // pending request?
    public boolean hasPendingRequest(String senderEmail,
                                     String receiverEmail) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM friend_requests " +
                        "WHERE sender_email=? " +
                        "AND receiver_email=? " +
                        "AND status='pending'",
                new String[]{senderEmail, receiverEmail}
        );

        boolean exists = cursor.moveToFirst();

        cursor.close();

        return exists;
    }


}