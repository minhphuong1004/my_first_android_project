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
    private static final int DB_VERSION = 1;

    public static final String TABLE_NAME = "users";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "email TEXT UNIQUE, " +
                "password TEXT," +
                "address TEXT," +
                "avatarURL TEXT," +
                "description TEXT)";

        String createPostTable = "CREATE TABLE posts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_email TEXT, " +
                "content TEXT, " +
                "date TEXT)";
        db.execSQL(createTable);
        db.execSQL(createPostTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    // insert user
    public boolean insertUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("email", email);
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

    public boolean updateUser(String email, String name, String address, String avatarURL, String description) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("address", address);
        values.put("avatarURL", avatarURL);
        values.put("description", description);
        int result = db.update("users", values, "email=?", new String[]{email});
        return result > 0;
    }

    public void insertPost(String email, String content, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("user_email", email);
        values.put("content", content);
        values.put("date", date);

        db.insert("posts", null, values);
    }
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
            list.add(new Post("You", content, date));
        }

        cursor.close();
        return list;
    }
}