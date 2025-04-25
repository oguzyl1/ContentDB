package com.contentdb.comment_service.repository;

import com.contentdb.comment_service.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface CommentRepository extends JpaRepository<Comment, String> {

    List<Comment> findByContentIdAndIsDeletedFalse(String contentId);

    List<Comment> findByParentCommentId(String commentId);

    List<Comment> findByParentCommentIdAndIsDeletedFalse(String parentCommentId);

    List<Comment> findByUserIdAndIsDeletedFalse(String userId);
}
