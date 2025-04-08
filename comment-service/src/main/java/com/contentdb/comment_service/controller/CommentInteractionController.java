package com.contentdb.comment_service.controller;

import com.contentdb.comment_service.service.CommentInteractionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interaction")
public class CommentInteractionController {

    private final CommentInteractionService commentInteractionService;

    public CommentInteractionController(CommentInteractionService commentInteractionService) {
        this.commentInteractionService = commentInteractionService;
    }

    @PostMapping("/like/{commentId}")
    public ResponseEntity<Void> like(@PathVariable(value = "commentId") String commentId,
                                     @RequestHeader("X-User-Id") String userId) {

        commentInteractionService.toggleLike(commentId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/dislike/{commentId}")
    public ResponseEntity<Void> dislike(@PathVariable(value = "commentId") String commentId,
                                        @RequestHeader("X-User-Id") String userId) {

        commentInteractionService.toggleDislike(commentId, userId);
        return ResponseEntity.ok().build();
    }
}
