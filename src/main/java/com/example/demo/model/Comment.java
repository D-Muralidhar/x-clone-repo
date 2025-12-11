package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Comment {

    private String id;
    private String userId;
    private String text;
    private String gifUrl;
    private long createdAt;

    private List<Comment> replies = new ArrayList<>();

    public Comment() {
        this.id = UUID.randomUUID().toString();
    }

    public Comment(String userId, String text, long createdAt) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.text = text;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public List<Comment> getReplies() { return replies; }
    public void setReplies(List<Comment> replies) { this.replies = replies; }

    public String getGifUrl() { return gifUrl; }
    public void setGifUrl(String gifUrl) { this.gifUrl = gifUrl; }
}
