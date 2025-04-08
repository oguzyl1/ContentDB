package com.contentdb.comment_service.controller;

import com.contentdb.comment_service.dto.CommentDto;
import com.contentdb.comment_service.request.CommentRequest;
import com.contentdb.comment_service.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/add/{contentId}")
    public ResponseEntity<CommentDto> addComment(@RequestBody CommentRequest commentRequest,
                                                 @PathVariable(value = "contentId") String contentId,
                                                 @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(commentService.addComment(commentRequest, contentId, userId));
    }

    @PostMapping("/reply/{commentId}")
    public ResponseEntity<CommentDto> replyToComment(@RequestBody CommentRequest request,
                                                     @PathVariable(value = "commentId") String commentId,
                                                     @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(commentService.replyToComment(request, commentId, userId));
    }

    @GetMapping("/get-all/{contentId}")
    public ResponseEntity<Map<String, Object>> getComment(@PathVariable(value = "contentId") String contentId) {
        return ResponseEntity.ok(commentService.getCommentsWithReplies(contentId));
    }

    @PutMapping("/update/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable(value = "commentId") String commentId,
                                                    @RequestBody CommentRequest request,
                                                    @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(commentService.updateComment(request, commentId, userId));
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable(value = "commentId") String commentId,
                                              @RequestHeader("X-User-Id") String userId) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-replies/{commentId}")
    public ResponseEntity<List<CommentDto>> getReplies(@PathVariable(value = "commentId") String commentId) {
        return ResponseEntity.ok(commentService.getRepliesOfComment(commentId));
    }

    @GetMapping("/get-users/comment")
    public ResponseEntity<List<CommentDto>> getUsers(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(commentService.getUserComments(userId));
    }

}

