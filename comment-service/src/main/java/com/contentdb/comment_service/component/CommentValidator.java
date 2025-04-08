package com.contentdb.comment_service.component;

import com.contentdb.comment_service.exception.EmptyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CommentValidator {

    private static final Logger logger = LoggerFactory.getLogger(CommentValidator.class);

    public void userIdControl(String userId) {
        if (userId == null || userId.isEmpty()) {
            logger.error("UserId boş geliyor: {}", userId);
            throw new EmptyException("Kullanıcı kimliği belirtilmemiş.");
        }
    }

    public void contentIdControl(String contentId) {
        if (contentId == null || contentId.isEmpty()) {
            logger.error("Content Id boş geliyor : {}", contentId);
            throw new EmptyException("İçerik kimliği belirtilmememiş.");
        }
    }

    public void commentIdControl(String commentId) {
        if (commentId == null || commentId.isEmpty()) {
            logger.error("Comment Id boş geliyor : {}", commentId);
            throw new EmptyException("Güncellenmek istenen yorumun kimliği belirtilmemiş.");
        }
    }

    public void parenCommentControl(String parentCommentId) {
        if (parentCommentId == null || parentCommentId.isEmpty()) {
            logger.error("ParentCommentId boş geliyor: {}", parentCommentId);
            throw new EmptyException("Yanıt verilen yorum kimliği belirtilmemiş");
        }
    }

}
