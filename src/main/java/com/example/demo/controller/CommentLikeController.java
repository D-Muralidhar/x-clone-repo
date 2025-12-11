package com.example.demo.controller;

import com.example.demo.security.AuthUtil;
import com.example.demo.service.CommentLikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentLikeController {

    private final CommentLikeService commentLikeService;
    private final AuthUtil authUtil;

    public CommentLikeController(CommentLikeService commentLikeService,
                                 AuthUtil authUtil) {
        this.commentLikeService = commentLikeService;
        this.authUtil = authUtil;
    }

    @PostMapping("/{commentId}/likes")
    public ResponseEntity<?> likeComment(@PathVariable String commentId) {
        String userId = authUtil.getCurrentUserId();
        commentLikeService.like(userId, commentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}/likes")
    public ResponseEntity<?> unlikeComment(@PathVariable String commentId) {
        String userId = authUtil.getCurrentUserId();
        commentLikeService.unlike(userId, commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{commentId}/likes/count")
    public ResponseEntity<?> getLikesCount(@PathVariable String commentId) {
        long count = commentLikeService.countLikes(commentId);
        return ResponseEntity.ok(count);
    }
}
