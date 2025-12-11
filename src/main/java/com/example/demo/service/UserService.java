package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepo;

    // --------------- DEPENDENCY INJECTION ---------------
    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // ---------------- REGISTER USER ---------------
    public User registerUser(User user) {
        user.setFollowers(new ArrayList<>());
        user.setFollowing(new ArrayList<>());
        user.setSavedPosts(new ArrayList<>());
        return userRepo.save(user);
    }

    // ---------------- FIND USER BY ID ---------------
    public User getUserById(String id) {
        return userRepo.findById(id).orElse(null);
    }

    // ---------------- SEARCH USERS ---------------
    public List<User> searchUsers(String query) {
        return userRepo
                .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrBioContainingIgnoreCase(
                        query, query, query
                );
    }

    // ---------------- FOLLOW USER ---------------
    public Map<String, Object> followUser(String followerId, String targetId) {

        if (followerId.equals(targetId))
            return Map.of("error", "You cannot follow yourself");

        User follower = getUserById(followerId);
        User target = getUserById(targetId);

        if (follower == null || target == null)
            return Map.of("error", "Invalid follower or target user");

        if (!follower.getFollowing().contains(targetId))
            follower.getFollowing().add(targetId);

        if (!target.getFollowers().contains(followerId))
            target.getFollowers().add(followerId);

        userRepo.save(follower);
        userRepo.save(target);

        return Map.of(
                "message", "Followed successfully",
                "followerId", followerId,
                "targetId", targetId
        );
    }

    // ---------------- UNFOLLOW USER ---------------
    public Map<String, Object> unfollowUser(String followerId, String targetId) {

        if (followerId.equals(targetId))
            return Map.of("error", "You cannot unfollow yourself");

        User follower = getUserById(followerId);
        User target = getUserById(targetId);

        if (follower == null || target == null)
            return Map.of("error", "Invalid follower or target user");

        follower.getFollowing().remove(targetId);
        target.getFollowers().remove(followerId);

        userRepo.save(follower);
        userRepo.save(target);

        return Map.of("message", "Unfollowed successfully");
    }

    // ---------------- GET FOLLOWERS ---------------
    public List<User> getFollowers(String userId) {
        User user = getUserById(userId);
        if (user == null) return new ArrayList<>();
        return userRepo.findAllById(user.getFollowers());
    }

    // ---------------- GET FOLLOWING ---------------
    public List<User> getFollowing(String userId) {
        User user = getUserById(userId);
        if (user == null) return new ArrayList<>();
        return userRepo.findAllById(user.getFollowing());
    }

    // ---------------- RECOMMEND USERS ---------------
    public List<User> recommendUsers(String userId) {

        User currentUser = getUserById(userId);
        if (currentUser == null) return new ArrayList<>();

        Set<String> recommendedIds = new HashSet<>();

        // Friends of friends
        for (String followingId : currentUser.getFollowing()) {
            User friend = getUserById(followingId);
            if (friend != null) {
                recommendedIds.addAll(friend.getFollowing());
            }
        }

        // Remove self + already following
        recommendedIds.remove(userId);
        recommendedIds.removeAll(currentUser.getFollowing());

        return userRepo.findAllById(recommendedIds);
    }

    // ---------------- SAVE POST ---------------
    public Map<String, Object> savePost(String userId, String postId) {
        User user = getUserById(userId);
        if (user == null) return Map.of("error", "User not found");

        if (!user.getSavedPosts().contains(postId))
            user.getSavedPosts().add(postId);

        userRepo.save(user);

        return Map.of(
                "message", "Post saved",
                "savedPosts", user.getSavedPosts()
        );
    }

    // ---------------- UNSAVE POST ---------------
    public Map<String, Object> unsavePost(String userId, String postId) {
        User user = getUserById(userId);
        if (user == null) return Map.of("error", "User not found");

        user.getSavedPosts().remove(postId);
        userRepo.save(user);

        return Map.of("message", "Post removed from saved list");
    }

    // ---------------- UPDATE PROFILE ---------------
    public User updateProfile(String userId, User updated) {

        User user = getUserById(userId);
        if (user == null) return null;

        if (updated.getName() != null) user.setName(updated.getName());
        if (updated.getBio() != null) user.setBio(updated.getBio());
        if (updated.getProfileImage() != null) user.setProfileImage(updated.getProfileImage());
        if (updated.getUsername() != null) user.setUsername(updated.getUsername());

        return userRepo.save(user);
    }

    // ---------------- UPDATE PROFILE IMAGE ONLY ---------------
    public User updateProfileImage(String userId, String imageUrl) {

        User user = getUserById(userId);
        if (user == null) return null;

        user.setProfileImage(imageUrl);
        return userRepo.save(user);
    }
}
