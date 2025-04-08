package com.contentdb.content_page_service.controller;

import com.contentdb.content_page_service.service.ContentPageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/page/")
public class ContentPageController {

    private final ContentPageService contentPageService;

    public ContentPageController(ContentPageService contentPageService) {
        this.contentPageService = contentPageService;
    }

    @GetMapping("/{contentId}")
    public ResponseEntity<Object> getPage(@PathVariable String contentId) {
        return ResponseEntity.ok(contentPageService.getContentPage(contentId));
    }


}
