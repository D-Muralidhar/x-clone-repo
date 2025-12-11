package com.example.demo.controller;

import com.example.demo.model.Comment;
import com.example.demo.model.Post;
import com.example.demo.repository.PostRepository;
import com.example.demo.security.AuthUtil;
import com.example.demo.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostRepository postRepository;
    private final AuthUtil authUtil;
    private final NotificationService notificationService;

    public PostController(PostRepository postRepository,
                          AuthUtil authUtil,
                          NotificationService notificationService) {
        this.postRepository = postRepository;
        this.authUtil = authUtil;
        this.notificationService = notificationService;
    }

    // ============================================================
    //                         CREATE POST
    // ============================================================
    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestBody Post post) {

        String currentUserId = authUtil.getCurrentUserId();

        if (post.getContent() == null || post.getContent().isEmpty()) {
            return ResponseEntity.badRequest().body("Missing content");
        }

        // always take userId from JWT, never from client
        post.setUserId(currentUserId);
        post.setCreatedAt(System.currentTimeMillis());

        return ResponseEntity.ok(postRepository.save(post));
    }

    // ============================================================
    //                         GET POSTS
    // ============================================================
    @GetMapping("/all")
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @GetMapping("/user/me")
    public List<Post> getMyPosts() {
        String currentUserId = authUtil.getCurrentUserId();
        return postRepository.findByUserId(currentUserId);
    }

    @GetMapping("/user/{userId}")
    public List<Post> getPostsByUser(@PathVariable String userId) {
        return postRepository.findByUserId(userId);
    }

    // ============================================================
    //                         LIKE POST
    // ============================================================
    @PostMapping("/like/{postId}")
    public ResponseEntity<?> likePost(@PathVariable String postId) {

        String currentUserId = authUtil.getCurrentUserId();

        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.badRequest().body("Post not found");
        }

        boolean added = false;
        if (!post.getLikedBy().contains(currentUserId)) {
            post.getLikedBy().add(currentUserId);
            post.setLikesCount(post.getLikesCount() + 1);
            added = true;
        }

        Post saved = postRepository.save(post);

        // --------------- NOTIFICATION FOR LIKE POST ---------------
        if (added && !currentUserId.equals(post.getUserId())) {
            notificationService.notifyUser(
                    post.getUserId(),          // receiver = post owner
                    currentUserId,             // who liked
                    "LIKE_POST",
                    "liked your post",
                    postId
            );
        }

        return ResponseEntity.ok(saved);
    }

    // ============================================================
    //                         ADD COMMENT
    // ============================================================
    @PostMapping("/comment/{postId}")
    public ResponseEntity<?> addComment(
            @PathVariable String postId,
            @RequestParam String text,
            @RequestParam(required = false) String gifUrl) {

        String currentUserId = authUtil.getCurrentUserId();

        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.badRequest().body("Post not found");
        }

        if (text == null || text.isEmpty()) {
            return ResponseEntity.badRequest().body("Missing text");
        }

        Comment comment = new Comment();
        comment.setUserId(currentUserId);
        comment.setText(text);
        comment.setGifUrl(gifUrl);
        comment.setCreatedAt(System.currentTimeMillis());

        post.getComments().add(comment);
        post.setCommentsCount(post.getComments().size());

        Post saved = postRepository.save(post);

        // --------------- NOTIFICATION FOR COMMENT ---------------
        if (!currentUserId.equals(post.getUserId())) {
            notificationService.notifyUser(
                    post.getUserId(),      // receiver = post owner
                    currentUserId,         // who commented
                    "COMMENT",
                    "commented on your post",
                    postId
            );
        }

        return ResponseEntity.ok(saved);
    }

    // ============================================================
    //                         EDIT COMMENT
    // ============================================================
    @PatchMapping("/comment/edit/{postId}/{index}")
    public ResponseEntity<?> editComment(
            @PathVariable String postId,
            @PathVariable int index,
            @RequestParam String newText) {

        String currentUserId = authUtil.getCurrentUserId();

        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.badRequest().body("Post not found");
        }

        if (index < 0 || index >= post.getComments().size()) {
            return ResponseEntity.badRequest().body("Invalid comment index");
        }

        Comment comment = post.getComments().get(index);
        if (!currentUserId.equals(comment.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not own this comment");
        }

        comment.setText(newText);
        return ResponseEntity.ok(postRepository.save(post));
    }

    // ============================================================
    //                         DELETE COMMENT
    // ============================================================
    @DeleteMapping("/comment/{postId}/{index}")
    public ResponseEntity<?> deleteComment(
            @PathVariable String postId,
            @PathVariable int index) {

        String currentUserId = authUtil.getCurrentUserId();

        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.badRequest().body("Post not found");
        }

        if (index < 0 || index >= post.getComments().size()) {
            return ResponseEntity.badRequest().body("Invalid comment index");
        }

        Comment comment = post.getComments().get(index);
        if (!currentUserId.equals(comment.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not own this comment");
        }

        post.getComments().remove(index);
        post.setCommentsCount(post.getComments().size());

        return ResponseEntity.ok(postRepository.save(post));
    }

    // ============================================================
    //                         EDIT POST
    // ============================================================
    @PatchMapping("/edit/{postId}")
    public ResponseEntity<?> editPost(
            @PathVariable String postId,
            @RequestBody Map<String, String> updates) {

        String currentUserId = authUtil.getCurrentUserId();

        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.badRequest().body("Post not found");
        }

        if (!currentUserId.equals(post.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not own this post");
        }

        if (updates.containsKey("content")) {
            post.setContent(updates.get("content"));
        }
        if (updates.containsKey("imageUrl")) {
            post.setImageUrl(updates.get("imageUrl"));
        }
        if (updates.containsKey("gifUrl")) {
            post.setGifUrl(updates.get("gifUrl"));
        }

        return ResponseEntity.ok(postRepository.save(post));
    }

    // ============================================================
    //                         DELETE POST
    // ============================================================
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable String postId) {

        String currentUserId = authUtil.getCurrentUserId();

        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.badRequest().body("Post not found");
        }

        if (!currentUserId.equals(post.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not own this post");
        }

        postRepository.deleteById(postId);
        return ResponseEntity.ok("Post deleted successfully");
    }
}
