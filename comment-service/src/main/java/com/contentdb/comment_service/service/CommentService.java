package com.contentdb.comment_service.service;

import com.contentdb.comment_service.component.CommentValidator;
import com.contentdb.comment_service.dto.CommentDto;
import com.contentdb.comment_service.exception.*;
import com.contentdb.comment_service.model.Comment;
import com.contentdb.comment_service.repository.CommentRepository;
import com.contentdb.comment_service.request.CommentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentService {


    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;
    private final CommentValidator validator;

    public CommentService(CommentRepository commentRepository, CommentValidator validator) {
        this.commentRepository = commentRepository;
        this.validator = validator;
    }


    @Transactional
    public CommentDto addComment(CommentRequest request, String contentId, String userId) {

        logger.info("{} id'li Kullanıcı {} içeriğine yorum ekliyor", userId, contentId);
        validator.userIdControl(userId);
        validator.contentIdControl(contentId);

        Comment comment = Comment.builder().comment(request.comment()).userId(userId).contentId(contentId).parentComment(null).build();

        return CommentDto.convertToCommentDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto replyToComment(CommentRequest request, String parentCommentId, String userId) {
        logger.info("{} id'li kullanıcı {} id'li yoruma yanıt veriyor", userId, parentCommentId);
        validator.userIdControl(userId);
        validator.parenCommentControl(parentCommentId);

        Comment parentComment = commentRepository.findById(parentCommentId).orElseThrow(() -> new ParenCommentIdNotFoundException("Yanıt verilecek yorum bulunamadı."));

        Comment reply = Comment.builder().comment(request.comment()).userId(userId).contentId(parentComment.getContentId()).parentComment(parentComment).build();

        return CommentDto.convertToCommentDto(commentRepository.save(reply));
    }


    @Transactional(readOnly = true)
    public Map<String, Object> getCommentsWithReplies(String contentId) {

        List<Comment> allComments = commentRepository.findByContentIdAndIsDeletedFalse(contentId);

        List<Comment> topLevelComments = allComments
                .stream()
                .filter(c -> c.getParentComment() == null)
                .toList();

        List<Comment> replies = allComments
                .stream()
                .filter(c -> c.getParentComment() != null)
                .toList();

        Map<String, List<CommentDto>> replyMap = replies
                .stream()
                .collect(Collectors.groupingBy(
                        c -> c.getParentComment().getId(),
                        Collectors.mapping(CommentDto::convertToCommentDto, Collectors.toList())
                ));

        Map<String, Object> result = new HashMap<>();

        result.put("topLevelComments", topLevelComments
                .stream()
                .map(CommentDto::convertToCommentDto)
                .collect(Collectors.toList()));

        result.put("replies", replyMap);

        return result;
    }


    @Transactional
    public CommentDto updateComment(CommentRequest request, String commentId, String userId) {

        logger.info("{} id'li kullanıcı , {} id'li yorumunu güncelliyor ", userId, commentId);
        validator.userIdControl(userId);
        validator.commentIdControl(commentId);

        Comment currentComment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("Yorum bulunamadı."));


        if (!currentComment.getUserId().equals(userId)) {
            logger.error("Kullanıcı id uyuşmuyor. Yorum sahibinin id'si: {}, istek yapan: {}", currentComment.getUserId(), userId);
            throw new UserIdNotSameException("Sadece yorumun sahibi tarafından güncellenebilir.");
        }

        if (currentComment.getIsDeleted()) {
            logger.error("Kullanıcının güncellemek istediği yorum silinmiş.");
            throw new CommentDeletedException("Silinmiş yorum güncellenemez.");
        }

        currentComment.setComment(request.comment());
        Comment updatedComment = commentRepository.save(currentComment);
        return CommentDto.convertToCommentDto(updatedComment);
    }


    @Transactional
    public void deleteComment(String commentId, String userId) {

        logger.info("{} id'li kullanıcı, {} id'li yorumunu siliyor", userId, commentId);
        validator.userIdControl(userId);
        validator.commentIdControl(commentId);


        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Yorum bulunamadı."));


        if (!comment.getUserId().equals(userId)) {
            throw new UserIdNotSameException("Bu yorumu silmeye yetkiniz yok.");
        }

        comment.setIsDeleted(true);
        commentRepository.save(comment);

        logger.info("Kullanıcı {} yorum {} silindi olarak işaretlendi.", userId, commentId);

        if (comment.getParentComment() == null) {
            List<Comment> replies = commentRepository.findByParentCommentId(commentId);

            for (Comment reply : replies) {
                reply.setIsDeleted(true);
                logger.info("Yanıt yorum {} silindi olarak işaretlendi.", reply.getId());
            }

            commentRepository.saveAll(replies);
        }

    }


    @Transactional(readOnly = true)
    public List<CommentDto> getRepliesOfComment(String parentCommentId) {
        validator.parenCommentControl(parentCommentId);

        List<Comment> replies = commentRepository.findByParentCommentIdAndIsDeletedFalse(parentCommentId);

        return replies.stream()
                .map(CommentDto::convertToCommentDto)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<CommentDto> getUserComments(String userId) {
        validator.userIdControl(userId);
        List<Comment> comments = commentRepository.findByUserIdAndIsDeletedFalse(userId);
        return comments.stream()
                .map(CommentDto::convertToCommentDto)
                .collect(Collectors.toList());
    }

}
