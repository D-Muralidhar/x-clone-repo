package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.Post;
import com.example.demo.service.UserService;
import com.example.demo.repository.PostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostRepository postRepository;

    // ============================================================
    //                      REGISTER USER
    // ============================================================
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {

        if (user.getEmail() == null || user.getEmail().isEmpty())
            return ResponseEntity.badRequest().body("Email is required");

        if (user.getPassword() == null || user.getPassword().isEmpty())
            return ResponseEntity.badRequest().body("Password is required");

        User created = userService.registerUser(user);
        return ResponseEntity.ok(created);
    }

    // ============================================================
    //                      USER PROFILE
    // ============================================================
    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable String userId) {

        User user = userService.getUserById(userId);
        if (user == null)
            return ResponseEntity.badRequest().body("User not found");

        List<Post> posts = postRepository.findByUserId(userId);

        Map<String, Object> profile = new HashMap<>();
        profile.put("user", user);
        profile.put("posts", posts);
        profile.put("totalPosts", posts.size());
        profile.put("followers", user.getFollowers().size());
        profile.put("following", user.getFollowing().size());
        profile.put("savedPosts", user.getSavedPosts());

        return ResponseEntity.ok(profile);
    }

    // ============================================================
    //                       SEARCH USERS
    // ============================================================
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam String query) {
        return ResponseEntity.ok(userService.searchUsers(query));
    }

    // ============================================================
    //                FOLLOW / UNFOLLOW USERS
    // ============================================================
    @PostMapping("/follow")
    public ResponseEntity<?> followUser(
            @RequestParam String followerId,
            @RequestParam String targetId
    ) {
        return ResponseEntity.ok(userService.followUser(followerId, targetId));
    }

    @PostMapping("/unfollow")
    public ResponseEntity<?> unfollowUser(
            @RequestParam String followerId,
            @RequestParam String targetId
    ) {
        return ResponseEntity.ok(userService.unfollowUser(followerId, targetId));
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<?> getFollowers(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getFollowers(userId));
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<?> getFollowing(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getFollowing(userId));
    }

    // ============================================================
    //                RECOMMEND USERS TO FOLLOW
    // ============================================================
    @GetMapping("/recommend/{userId}")
    public ResponseEntity<?> recommendUsers(@PathVariable String userId) {
        return ResponseEntity.ok(userService.recommendUsers(userId));
    }

    // ============================================================
    //                     SAVE POST
    // ============================================================
    @PostMapping("/save")
    public ResponseEntity<?> savePost(
            @RequestParam String userId,
            @RequestParam String postId
    ) {
        return ResponseEntity.ok(userService.savePost(userId, postId));
    }

    // ============================================================
    //                     UNSAVE POST
    // ============================================================
    @PostMapping("/unsave")
    public ResponseEntity<?> unsavePost(
            @RequestParam String userId,
            @RequestParam String postId
    ) {
        return ResponseEntity.ok(userService.unsavePost(userId, postId));
    }

    // ============================================================
    //               GET SAVED POSTS LIST
    // ============================================================
    @GetMapping("/{userId}/saved")
    public ResponseEntity<?> getSavedPosts(@PathVariable String userId) {

        User user = userService.getUserById(userId);
        if (user == null)
            return ResponseEntity.badRequest().body("User not found");

        List<Post> saved = postRepository.findAllById(user.getSavedPosts());
        return ResponseEntity.ok(saved);
    }

    // ============================================================
    //               UPDATE USER PROFILE
    // ============================================================
    @PatchMapping("/update/{userId}")
    public ResponseEntity<?> updateProfile(
            @PathVariable String userId,
            @RequestBody User updatedUser
    ) {
        User updated = userService.updateProfile(userId, updatedUser);

        if (updated == null)
            return ResponseEntity.badRequest().body("User not found");

        return ResponseEntity.ok(updated);
    }
}
