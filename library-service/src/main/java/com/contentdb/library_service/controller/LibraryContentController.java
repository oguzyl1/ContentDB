package com.contentdb.library_service.controller;

import com.contentdb.library_service.dto.AddContentRequest;
import com.contentdb.library_service.service.LibraryContentService;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/libraryContent")
@Validated
public class LibraryContentController {

    private final LibraryContentService libraryContentService;

    public LibraryContentController(LibraryContentService libraryContentService) {
        this.libraryContentService = libraryContentService;
    }

    @PutMapping("{libraryName}")
    public ResponseEntity<Void> addContentToLibrary(@RequestBody AddContentRequest request, @PathVariable(value = "libraryName") @NotEmpty String LibraryName) {
        libraryContentService.addContentToLibrary(request, LibraryName);
        return ResponseEntity.ok().build();
    }
}
