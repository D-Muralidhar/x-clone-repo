package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

public class Comment {

    private String userId;
    private String text;
    private long createdAt;

    private List<Comment> replies = new ArrayList<>();

    public Comment() {}

    public Comment(String userId, String text, long createdAt) {
        this.userId = userId;
        this.text = text;
        this.createdAt = createdAt;
    }

    // Getters & setters

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public List<Comment> getReplies() { return replies; }
    public void setReplies(List<Comment> replies) { this.replies = replies; }
}
