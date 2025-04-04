package com.contentdb.comment_service.service;

import com.contentdb.comment_service.dto.CommentDto;
import com.contentdb.comment_service.exception.ParenCommentIdNotFoundException;
import com.contentdb.comment_service.exception.EmptyException;
import com.contentdb.comment_service.model.Comment;
import com.contentdb.comment_service.repository.CommentRepository;
import com.contentdb.comment_service.request.CommentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class CommentService {


    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }


    public CommentDto addComment(CommentRequest request, String contentId, String userId) {

        logger.info("{} id'li Kullanıcı {} içeriğine yorum ekliyor", userId, contentId);
        userIdControl(userId);
        contentIdControl(contentId);

        Comment comment = Comment.builder()
                .comment(request.comment())
                .userId(userId)
                .contentId(contentId)
                .parentComment(null)
                .build();

        return CommentDto.convertToCommentDto(commentRepository.save(comment));
    }

    public CommentDto replyToComment(CommentRequest request, String parentCommentId, String userId) {
        logger.info("{} id'li kullanıcı {} id'li yoruma yanıt veriyor", userId, parentCommentId);
        userIdControl(userId);
        parenCommentControl(parentCommentId);

        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new ParenCommentIdNotFoundException("Yanıt verilecek yorum bulunamadı."));

        Comment reply = Comment.builder()
                .comment(request.comment())
                .userId(userId)
                .contentId(parentComment.getContentId())
                .parentComment(parentComment)
                .build();

        return CommentDto.convertToCommentDto(commentRepository.save(reply));
    }

    public Map<String, Object> getCommentsWithReplies(String contentId) {

        List<Comment> allComments = commentRepository.findByContentId(contentId);

        List<Comment> topLevelComments = allComments.stream().
                filter(c -> c.getParentComment() == null)
                .toList();

        List<Comment> replies = allComments.stream().
                filter(c -> c.getParentComment() != null)
                .toList();

        Map<String, List<CommentDto>> replyMap = replies.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getParentComment().getId(), Collectors.mapping(CommentDto::convertToCommentDto, Collectors.toList())));

        Map<String, Object> result = new HashMap<>();

        result.put("topLevelComments", topLevelComments.stream()
                .map(CommentDto::convertToCommentDto).
                collect(Collectors.toList()));

        result.put("replies", replyMap);

        return result;
    }



    private void userIdControl(String userId) {
        if (userId == null || userId.isEmpty()) {
            logger.error("UserId boş geliyor: {}", userId);
            throw new EmptyException("Kullanıcı kimliği belirtilmemiş.");
        }
    }

    private void contentIdControl(String contentId) {
        if (contentId == null || contentId.isEmpty()) {
            logger.error("Content Id boş geliyor : {}", contentId);
            throw new EmptyException("İçerik kimliği belirtilmememiş.");
        }
    }

    private void parenCommentControl(String parentCommentId) {
        if (parentCommentId == null || parentCommentId.isEmpty()) {
            logger.error("ParentCommentId boş geliyor: {}", parentCommentId);
            throw new EmptyException("Yanıt verilen yorum kimliği belirtilmemiş");
        }
    }


}
