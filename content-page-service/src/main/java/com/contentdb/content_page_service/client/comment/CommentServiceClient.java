package com.contentdb.content_page_service.client.comment;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "comment-service", path = "/api/comment")
public interface CommentServiceClient {


    @GetMapping("/get-all/{contentId}")
    public ResponseEntity<Map<String, Object>> getComment(@PathVariable(value = "contentId") String contentId);


}
