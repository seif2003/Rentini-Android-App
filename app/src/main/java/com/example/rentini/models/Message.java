package com.example.rentini.models;

public class Message {
    private String text;
    private String userId;
    private long timestamp;

    public Message() {} // Required for Firebase

    public Message(String text, String userId, long timestamp) {
        this.text = text;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public String getText() { return text; }
    public String getUserId() { return userId; }
    public long getTimestamp() { return timestamp; }
}