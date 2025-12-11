package com.example.demo.service;

import com.example.demo.model.CommentLike;
import com.example.demo.repository.CommentLikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;

    public CommentLikeService(CommentLikeRepository commentLikeRepository) {
        this.commentLikeRepository = commentLikeRepository;
    }

    // --------------- LIKE COMMENT ---------------
    @Transactional
    public void like(String userId, String commentId) {
        if (commentLikeRepository.existsByUserIdAndCommentId(userId, commentId)) {
            return; // already liked
        }

        CommentLike like = new CommentLike();
        like.setUserId(userId);
        like.setCommentId(commentId);
        like.setCreatedAt(System.currentTimeMillis());

        commentLikeRepository.save(like);
    }

    // --------------- UNLIKE COMMENT ---------------
    @Transactional
    public void unlike(String userId, String commentId) {
        commentLikeRepository.deleteByUserIdAndCommentId(userId, commentId);
    }

    // --------------- COUNT LIKES ---------------
    @Transactional(readOnly = true)
    public long countLikes(String commentId) {
        return commentLikeRepository.countByCommentId(commentId);
    }
}
