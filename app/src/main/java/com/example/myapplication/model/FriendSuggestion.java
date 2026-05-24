package com.example.myapplication.model;

public class FriendSuggestion {
    public String name;
    public String email;
    public String phoneNumber;
    public String avatarUrl;

    public FriendSuggestion(String name,
                            String email,
                            String phoneNumber,
                            String avatarUrl) {

        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.avatarUrl = avatarUrl;
    }
}
