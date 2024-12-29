package com.contentdb.library_service.controller;

import com.contentdb.library_service.dto.LibraryDto;
import com.contentdb.library_service.service.LibraryService;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/library")
@Validated
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @PostMapping("/create")
    public ResponseEntity<LibraryDto> createLibrary(@RequestBody LibraryDto libraryDto) {
        return ResponseEntity.ok(libraryService.createLibrary(libraryDto));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<LibraryDto>> getAllLibrary() {
        return ResponseEntity.ok(libraryService.getAllLibraries());
    }

    @PutMapping("{name}")
    public ResponseEntity<LibraryDto> updateLibrary(
            @NotEmpty @PathVariable(name = "name") String name,
            @RequestBody LibraryDto libraryDto
    ) {
        return ResponseEntity.ok(libraryService.updateLibraryName(name, libraryDto));
    }

    @DeleteMapping("{name}")
    public ResponseEntity<Void> deleteLibrary(@NotEmpty @PathVariable(name = "name") String name) {
        libraryService.deleteLibrary(name);
        return ResponseEntity.noContent().build();
    }
}
