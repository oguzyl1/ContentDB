package com.contentdb.library_service.controller;

import com.contentdb.library_service.dto.libraryDTO.LibraryDto;
import com.contentdb.library_service.service.LibraryService;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/library")
@Validated
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }


    /**
     * Kütüphane oluşturur.
     * POST /api/v1/library
     *
     * @param libraryDto Oluşturulacak kütüphane bilgileri
     * @param userId     Kullanıcı ID'si (header üzerinden "X-User-Id")
     * @return Oluşturulan kütüphane DTO'su
     */
    @PostMapping
    public ResponseEntity<LibraryDto> createLibrary(@RequestBody LibraryDto libraryDto,
                                                    @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(libraryService.createLibrary(libraryDto, userId));
    }


    /**
     * Kullanıcıya ait tüm kütüphaneleri getirir.
     * GET /api/v1/library
     *
     * @param userId Kullanıcı ID'si (header üzerinden "X-User-Id")
     * @return Kütüphane DTO listesini döner
     */
    @GetMapping
    public ResponseEntity<List<LibraryDto>> getAllLibrary(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(libraryService.getAllLibraries(userId));
    }


    /**
     * Belirtilen kütüphanenin adını günceller.
     * PUT /api/v1/library/{name}
     *
     * @param name       Güncellenecek kütüphanenin mevcut adı
     * @param libraryDto Güncel kütüphane bilgileri (yeni isim vb.)
     * @param userId     Kullanıcı ID'si (header üzerinden "X-User-Id")
     * @return Güncellenmiş kütüphane DTO'su
     */
    @PutMapping("/{name}")
    public ResponseEntity<LibraryDto> updateLibrary(
            @NotEmpty @PathVariable(name = "name") String name,
            @RequestBody LibraryDto libraryDto,
            @RequestHeader("X-User-Id") String userId
    ) {
        return ResponseEntity.ok(libraryService.updateLibraryName(name, libraryDto, userId));
    }

    /**
     * Belirtilen kütüphaneyi siler.
     * DELETE /api/v1/library/{name}
     *
     * @param name   Silinecek kütüphane adı
     * @param userId Kullanıcı ID'si (header üzerinden "X-User-Id")
     * @return No Content
     */
    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteLibrary(@NotEmpty @PathVariable(name = "name") String name
            , @RequestHeader("X-User-Id") String userId) {
        libraryService.deleteLibrary(name, userId);
        return ResponseEntity.noContent().build();
    }
}
