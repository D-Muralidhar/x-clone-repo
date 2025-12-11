package com.example.demo.repository;

import com.example.demo.model.CommentLike;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentLikeRepository extends MongoRepository<CommentLike, String> {

    boolean existsByUserIdAndCommentId(String userId, String commentId);

    void deleteByUserIdAndCommentId(String userId, String commentId);

    long countByCommentId(String commentId);
}
