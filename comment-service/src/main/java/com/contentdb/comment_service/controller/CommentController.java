package com.contentdb.comment_service.controller;

import com.contentdb.comment_service.dto.CommentDto;
import com.contentdb.comment_service.model.Comment;
import com.contentdb.comment_service.request.CommentRequest;
import com.contentdb.comment_service.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/add/{contentId}/{userId}")
    public ResponseEntity<CommentDto> addComment(@RequestBody CommentRequest commentRequest,
                                                 @PathVariable(value = "contentId") String contentId,
                                                 @PathVariable(value = "userId") String userId) {
        return ResponseEntity.ok(commentService.addComment(commentRequest, contentId, userId));
    }

    @PostMapping("/reply/{commentId}/{userId}")
    public ResponseEntity<CommentDto> replyToComment(@RequestBody CommentRequest request,
                                                     @PathVariable(value = "commentId") String commentId,
                                                     @PathVariable(value = "userId") String userId) {
        return ResponseEntity.ok(commentService.replyToComment(request, commentId, userId));
    }

    @GetMapping("/get/{contentId}")
    public ResponseEntity<Map<String, Object>> getComment(@PathVariable(value = "contentId") String contentId) {
        return ResponseEntity.ok(commentService.getCommentsWithReplies(contentId));
    }

}

