package com.contentdb.comment_service.repository;

import com.contentdb.comment_service.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {

    List<Comment> findByContentIdAndDeletedFalse(String contentId);

    List<Comment> findByParentCommentId(String commentId);

    List<Comment> findByParentCommentIdAndDeletedFalse(String parentCommentId);

    List<Comment> findByUserIdAndDeletedFalse(String userId);
}
