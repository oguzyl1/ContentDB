package com.contentdb.content_service.controller;

import com.contentdb.content_service.model.DetailsRequest;
import com.contentdb.content_service.model.ImdbIDRequest;
import com.contentdb.content_service.model.PosterRequest;
import com.contentdb.content_service.model.SearchResponse;
import com.contentdb.content_service.service.ContentService;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/content")
@Validated
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping("/imdbId")
    public ResponseEntity<ImdbIDRequest> getImdbIDByTitle(@RequestParam @NotEmpty String title) {
        return ResponseEntity.ok(contentService.getImdbIDByTitle(title));
    }

    @GetMapping("/poster")
    public ResponseEntity<PosterRequest> getPosterByImdbID(@RequestParam @NotEmpty String id) {
        return ResponseEntity.ok(contentService.getPosterByImdbID(id));
    }

    @GetMapping("/details")
    public ResponseEntity<DetailsRequest> getDetailsByImdbID(@RequestParam @NotEmpty String id) {
        return ResponseEntity.ok(contentService.getDetailsByImdbID(id));
    }

    @GetMapping("/search")
    public ResponseEntity<SearchResponse> searchContents(@RequestParam @NotEmpty String title) {
        return ResponseEntity.ok(contentService.searchContents(title));
    }

}
