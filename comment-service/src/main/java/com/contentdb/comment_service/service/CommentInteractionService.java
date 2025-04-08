package com.contentdb.comment_service.service;

import com.contentdb.comment_service.exception.CommentNotFoundException;
import com.contentdb.comment_service.model.Comment;
import com.contentdb.comment_service.model.InteractionType;
import com.contentdb.comment_service.model.UserCommentInteraction;
import com.contentdb.comment_service.repository.CommentRepository;
import com.contentdb.comment_service.repository.UserCommentInteractionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CommentInteractionService {
    private static final Logger logger = LoggerFactory.getLogger(CommentInteractionService.class);
    private final UserCommentInteractionRepository interactionRepository;
    private final CommentRepository commentRepository;

    public CommentInteractionService(UserCommentInteractionRepository interactionRepository, CommentRepository commentRepository) {
        this.interactionRepository = interactionRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public void toggleLike(String commentId, String userId) {
        logger.info("{} id'li kullanıcı {} id'li yorumu beğeniyor/kaldırıyor", userId, commentId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Yorum bulunamadı."));

        Optional<UserCommentInteraction> existingInteraction = interactionRepository.findByUserIdAndComment(userId, comment);

        if (existingInteraction.isPresent() && existingInteraction.get().getInteractionType() == InteractionType.LIKE) {
            interactionRepository.delete(existingInteraction.get());
            comment.setLikeCount(comment.getLikeCount() - 1);
            logger.info("Beğeni kaldırıldı: {}", commentId);
        } else {
            if (existingInteraction.isPresent() && existingInteraction.get().getInteractionType() == InteractionType.DISLIKE) {
                interactionRepository.delete(existingInteraction.get());
                comment.setDislikeCount(comment.getDislikeCount() - 1);
            }
            UserCommentInteraction newInteraction = new UserCommentInteraction();
            newInteraction.setUserId(userId);
            newInteraction.setComment(comment);
            newInteraction.setInteractionType(InteractionType.LIKE);
            interactionRepository.save(newInteraction);
            comment.setLikeCount(comment.getLikeCount() + 1);
            logger.info("Yeni beğeni eklendi: {}", commentId);
        }
        commentRepository.save(comment);
    }

    @Transactional
    public void toggleDislike(String commentId, String userId) {
        logger.info("{} id'li kullanıcı {} id'li yorumu dislike ediyor/kaldırıyor", userId, commentId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Yorum bulunamadı."));

        Optional<UserCommentInteraction> existingInteraction = interactionRepository.findByUserIdAndComment(userId, comment);

        if (existingInteraction.isPresent() && existingInteraction.get().getInteractionType() == InteractionType.DISLIKE) {
            interactionRepository.delete(existingInteraction.get());
            comment.setDislikeCount(comment.getDislikeCount() - 1);
            logger.info("Dislike kaldırıldı: {}", commentId);
        } else {
            if (existingInteraction.isPresent() && existingInteraction.get().getInteractionType() == InteractionType.LIKE) {
                interactionRepository.delete(existingInteraction.get());
                comment.setLikeCount(comment.getLikeCount() - 1);
            }
            UserCommentInteraction newInteraction = new UserCommentInteraction();
            newInteraction.setUserId(userId);
            newInteraction.setComment(comment);
            newInteraction.setInteractionType(InteractionType.DISLIKE);
            interactionRepository.save(newInteraction);
            comment.setDislikeCount(comment.getDislikeCount() + 1);
            logger.info("Yeni dislike eklendi: {}", commentId);
        }
        commentRepository.save(comment);
    }
}