package com.example.myapplication.model;

public class FriendRequest {
    public String senderEmail;
    public String receiverEmail;
    public String status;

    public FriendRequest(String senderEmail,
                         String receiverEmail,
                         String status) {

        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.status = status;
    }
}
