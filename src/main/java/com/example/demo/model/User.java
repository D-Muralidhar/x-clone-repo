package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String email;
    private String username;
    private String password;

    private String name;
    private String bio;
    private String profileImage;

    private List<String> followers = new ArrayList<>();
    private List<String> following = new ArrayList<>();

    private List<String> savedPosts = new ArrayList<>();

    public User() {}

    // ----------------- GETTERS & SETTERS -----------------

    public String getId() { return id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public List<String> getFollowers() { return followers; }
    public void setFollowers(List<String> followers) { this.followers = followers; }

    public List<String> getFollowing() { return following; }
    public void setFollowing(List<String> following) { this.following = following; }

    public List<String> getSavedPosts() { return savedPosts; }
    public void setSavedPosts(List<String> savedPosts) { this.savedPosts = savedPosts; }
}
