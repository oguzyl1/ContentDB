package com.contentdb.library_service.controller;

import com.contentdb.library_service.dto.contentDTO.AddContentRequest;
import com.contentdb.library_service.dto.contentDTO.ContentCardDto;
import com.contentdb.library_service.dto.libraryDTO.LibraryContentDto;
import com.contentdb.library_service.service.LibraryContentService;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/library/{libraryName}/contents")
@Validated
public class LibraryContentController {

    private final LibraryContentService libraryContentService;

    public LibraryContentController(LibraryContentService libraryContentService) {
        this.libraryContentService = libraryContentService;
    }

    /**
     * Belirtilen kütüphaneye yeni içerik ekler.
     * POST /api/v1/library/{libraryName}/contents
     *
     * @param request     Eklenmek istenen içerik bilgilerini içeren DTO
     * @param libraryName İçeriğin ekleneceği kütüphane adı (path üzerinden)
     * @param userId      Kullanıcı ID'si (header üzerinden "X-User-Id")
     * @return HTTP 200 OK
     */
    @PutMapping
    public ResponseEntity<Void> addContentToLibrary(@RequestBody AddContentRequest request,
                                                    @PathVariable(value = "libraryName") @NotEmpty String libraryName,
                                                    @RequestHeader("X-User-Id") String userId) {
        libraryContentService.addContentToLibrary(request, libraryName,userId);
        return ResponseEntity.ok().build();
    }


    /**
     * Kütüphanedeki içerik ID'lerini getirir.
     * GET /v1/libraries/{libraryName}/contents/ids
     *
     * @param libraryName Kütüphane adı (path üzerinden)
     * @param userId      Kullanıcı ID'si (header üzerinden "X-User-Id")
     * @return Kütüphanedeki içeriklerin DTO listesini döner
     */
    @GetMapping("/ids")
    public ResponseEntity<List<LibraryContentDto>> getAllContentIds(@PathVariable(value = "libraryName") @NotEmpty String libraryName,
                                                                    @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(libraryContentService.getAllContentsFromLibrary(libraryName,userId));
    }

    /**
     * Kütüphanedeki içeriklere ait kart bilgilerini getirir.
     * GET /v1/libraries/{libraryName}/contents/cards
     *
     * @param libraryName Kütüphane adı (path üzerinden)
     * @param userId      Kullanıcı ID'si (header üzerinden "X-User-Id")
     * @return İçerik kartlarının bulunduğu DTO listesini döner
     */
    @GetMapping("/cards")
    public ResponseEntity<List<ContentCardDto>> getContentCardsFromLibrary(@PathVariable(value = "libraryName") @NotEmpty String libraryName,
                                                                           @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(libraryContentService.getContentCardsFromLibrary(libraryName,userId));
    }
}
