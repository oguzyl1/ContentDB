package com.contentdb.comment_service.repository;

import com.contentdb.comment_service.model.Comment;
import com.contentdb.comment_service.model.UserCommentInteraction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCommentInteractionRepository extends JpaRepository<UserCommentInteraction, String> {

    Optional<UserCommentInteraction> findByUserIdAndComment(String userId, Comment comment);

    Optional<UserCommentInteraction> findByCommentIdAndUserId(String commentId, String userId);
}
