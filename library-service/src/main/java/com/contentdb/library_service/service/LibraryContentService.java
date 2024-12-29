package com.contentdb.library_service.service;

import com.contentdb.library_service.client.ContentServiceClient;
import com.contentdb.library_service.dto.AddContentRequest;
import com.contentdb.library_service.dto.LibraryDto;
import com.contentdb.library_service.model.Library;
import com.contentdb.library_service.model.LibraryContent;
import com.contentdb.library_service.repository.LibraryContentRepository;
import jakarta.transaction.TransactionScoped;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LibraryContentService {
    private final LibraryContentRepository libraryContentRepository;
    private final ContentServiceClient contentServiceClient;
    private final LibraryService libraryService;

    public LibraryContentService(LibraryContentRepository libraryContentRepository, ContentServiceClient contentServiceClient, LibraryService libraryService) {
        this.libraryContentRepository = libraryContentRepository;
        this.contentServiceClient = contentServiceClient;
        this.libraryService = libraryService;
    }

    @Transactional
    public void addContentToLibrary(AddContentRequest request, String LibraryName) {
        LibraryDto libraryDto = libraryService.getLibraryByName(LibraryName);
        String imdbId = contentServiceClient.getImdbIDByTitle(request.getTitle()).getBody().getImdbID();
        LibraryContent libraryContent = new LibraryContent();
        libraryContent.setLibrary(new Library(libraryDto.getName()));
        libraryContent.setContentId(imdbId);
        libraryContentRepository.save(libraryContent);
    }


}
