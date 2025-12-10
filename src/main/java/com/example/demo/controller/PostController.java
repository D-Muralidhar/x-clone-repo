package com.example.demo.controller;

import com.example.demo.model.Comment;
import com.example.demo.model.Post;
import com.example.demo.repository.PostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    // ============================================================
    //                        CREATE POST
    // ============================================================
    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestBody Post post) {

        if (post.getUserId() == null || post.getUserId().isEmpty())
            return ResponseEntity.badRequest().body("Missing userId");

        if (post.getContent() == null || post.getContent().isEmpty())
            return ResponseEntity.badRequest().body("Missing content");

        post.setCreatedAt(System.currentTimeMillis());
        return ResponseEntity.ok(postRepository.save(post));
    }

    // ============================================================
    //                        GET POSTS
    // ============================================================
    @GetMapping("/all")
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @GetMapping("/user/{userId}")
    public List<Post> getPostsByUser(@PathVariable String userId) {
        return postRepository.findByUserId(userId);
    }

    // ============================================================
    //                        LIKE POST
    // ============================================================
    @PostMapping("/like/{postId}")
    public ResponseEntity<?> likePost(
            @PathVariable String postId,
            @RequestParam String userId
    ) {
        Post post = postRepository.findById(postId).orElse(null);

        if (post == null)
            return ResponseEntity.badRequest().body("Post not found");

        if (!post.getLikedBy().contains(userId)) {
            post.getLikedBy().add(userId);
            post.setLikesCount(post.getLikesCount() + 1);
        }

        return ResponseEntity.ok(postRepository.save(post));
    }

    // ============================================================
    //                        ADD COMMENT
    // ============================================================
    @PostMapping("/comment/{postId}")
    public ResponseEntity<?> addComment(
            @PathVariable String postId,
            @RequestParam String userId,
            @RequestParam String text
    ) {
        Post post = postRepository.findById(postId).orElse(null);

        if (post == null)
            return ResponseEntity.badRequest().body("Post not found");

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setText(text);
        comment.setCreatedAt(System.currentTimeMillis());

        post.getComments().add(comment);
        post.setCommentsCount(post.getComments().size());

        return ResponseEntity.ok(postRepository.save(post));
    }

    // ============================================================
    //                        EDIT COMMENT
    // ============================================================
    @PatchMapping("/comment/edit/{postId}/{index}")
    public ResponseEntity<?> editComment(
            @PathVariable String postId,
            @PathVariable int index,
            @RequestParam String newText
    ) {
        Post post = postRepository.findById(postId).orElse(null);

        if (post == null)
            return ResponseEntity.badRequest().body("Post not found");

        if (index < 0 || index >= post.getComments().size())
            return ResponseEntity.badRequest().body("Invalid comment index");

        post.getComments().get(index).setText(newText);
        return ResponseEntity.ok(postRepository.save(post));
    }

    // ============================================================
    //                        DELETE COMMENT
    // ============================================================
    @DeleteMapping("/comment/{postId}/{index}")
    public ResponseEntity<?> deleteComment(
            @PathVariable String postId,
            @PathVariable int index
    ) {
        Post post = postRepository.findById(postId).orElse(null);

        if (post == null)
            return ResponseEntity.badRequest().body("Post not found");

        if (index < 0 || index >= post.getComments().size())
            return ResponseEntity.badRequest().body("Invalid comment index");

        post.getComments().remove(index);
        post.setCommentsCount(post.getComments().size());

        return ResponseEntity.ok(postRepository.save(post));
    }

    // ============================================================
    //                        EDIT POST
    // ============================================================
    @PatchMapping("/edit/{postId}")
    public ResponseEntity<?> editPost(
            @PathVariable String postId,
            @RequestBody Map<String, String> updates
    ) {
        Post post = postRepository.findById(postId).orElse(null);

        if (post == null)
            return ResponseEntity.badRequest().body("Post not found");

        if (updates.containsKey("content"))
            post.setContent(updates.get("content"));

        if (updates.containsKey("imageUrl"))
            post.setImageUrl(updates.get("imageUrl"));

        return ResponseEntity.ok(postRepository.save(post));
    }

    // ============================================================
    //                        DELETE POST
    // ============================================================
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable String postId) {

        if (!postRepository.existsById(postId))
            return ResponseEntity.badRequest().body("Post not found");

        postRepository.deleteById(postId);
        return ResponseEntity.ok("Post deleted successfully");
    }
}
