package com.example.rentini.models;

import java.util.List;

public class Conversation {
    private String id;
    private String otherUserId;
    private String otherUserName;
    private String lastMessage;
    private long timestamp;
    private List<String> participants;

    // Required empty constructor for Firebase
    public Conversation() {}

    public Conversation(String id, String otherUserId, String otherUserName, 
                       String lastMessage, long timestamp, List<String> participants) {
        this.id = id;
        this.otherUserId = otherUserId;
        this.otherUserName = otherUserName;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.participants = participants;
    }

    // Getter methods
    public String getId() { return id; }
    public String getOtherUserId() { return otherUserId; }
    public String getOtherUserName() { return otherUserName; }
    public String getLastMessage() { return lastMessage; }
    public long getTimestamp() { return timestamp; }
    public List<String> getParticipants() { return participants; }
}