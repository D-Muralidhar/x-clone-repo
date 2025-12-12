package com.example.demo.controller;

import com.example.demo.dto.RegisterUserRequest;
import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.repository.PostRepository;
import com.example.demo.security.AuthUtil;
import com.example.demo.service.MediaService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final PostRepository postRepository;
    private final AuthUtil authUtil;
    private final MediaService mediaService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService,
                          PostRepository postRepository,
                          AuthUtil authUtil,
                          MediaService mediaService,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.postRepository = postRepository;
        this.authUtil = authUtil;
        this.mediaService = mediaService;
        this.passwordEncoder = passwordEncoder;
    }

    // ============================================================
    //                      REGISTER USER
    // ============================================================
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterUserRequest request) {

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        // encode the raw password before saving
        user.setPassword(passwordEncoder.encode(request.getPassword()));

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
    //                      SEARCH USERS
    // ============================================================
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam String query) {
        return ResponseEntity.ok(userService.searchUsers(query));
    }

    // ============================================================
    //                  FOLLOW / UNFOLLOW USERS
    // ============================================================
    @PostMapping("/follow")
    public ResponseEntity<?> followUser(@RequestParam String targetId) {
        String followerId = authUtil.getCurrentUserId();
        return ResponseEntity.ok(userService.followUser(followerId, targetId));
    }

    @PostMapping("/unfollow")
    public ResponseEntity<?> unfollowUser(@RequestParam String targetId) {
        String followerId = authUtil.getCurrentUserId();
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
    //              RECOMMEND USERS TO FOLLOW
    // ============================================================
    @GetMapping("/recommend/me")
    public ResponseEntity<?> recommendUsersForCurrentUser() {
        String userId = authUtil.getCurrentUserId();
        return ResponseEntity.ok(userService.recommendUsers(userId));
    }

    // ============================================================
    //                      SAVE POST
    // ============================================================
    @PostMapping("/save")
    public ResponseEntity<?> savePost(@RequestParam String postId) {
        String userId = authUtil.getCurrentUserId();
        return ResponseEntity.ok(userService.savePost(userId, postId));
    }

    // ============================================================
    //                      UNSAVE POST
    // ============================================================
    @PostMapping("/unsave")
    public ResponseEntity<?> unsavePost(@RequestParam String postId) {
        String userId = authUtil.getCurrentUserId().toString();
        return ResponseEntity.ok(userService.unsavePost(userId, postId));
    }

    // ============================================================
    //                  GET SAVED POSTS LIST (SELF)
    // ============================================================
    @GetMapping("/me/saved")
    public ResponseEntity<?> getMySavedPosts() {

        String userId = authUtil.getCurrentUserId();

        User user = userService.getUserById(userId);
        if (user == null)
            return ResponseEntity.badRequest().body("User not found");

        List<Post> saved = postRepository.findAllById(user.getSavedPosts());
        return ResponseEntity.ok(saved);
    }

    // ============================================================
    //                  UPDATE USER PROFILE (SELF)
    // ============================================================
    @PatchMapping("/update/me")
    public ResponseEntity<?> updateProfile(@RequestBody User updatedUser) {

        String userId = authUtil.getCurrentUserId();

        User updated = userService.updateProfile(userId, updatedUser);
        if (updated == null)
            return ResponseEntity.badRequest().body("User not found");

        return ResponseEntity.ok(updated);
    }

    // ============================================================
    //              UPDATE PROFILE IMAGE (SELF)
    // ============================================================
    @PatchMapping("/me/profile-image")
    public ResponseEntity<?> updateProfileImage(@RequestParam("file") MultipartFile file)
            throws IOException {

        String userId = authUtil.getCurrentUserId();

        String imageUrl = mediaService.saveFile(file);

        User updated = userService.updateProfileImage(userId, imageUrl);
        if (updated == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        return ResponseEntity.ok(Map.of("profileImage", imageUrl));
    }
}
