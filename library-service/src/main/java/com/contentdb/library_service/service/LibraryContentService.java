package com.contentdb.library_service.service;

import com.contentdb.library_service.client.ContentServiceClient;
import com.contentdb.library_service.dto.*;
import com.contentdb.library_service.exception.ContentNotFoundException;
import com.contentdb.library_service.exception.ExceptionMessage;
import com.contentdb.library_service.exception.LibraryNotFoundException;
import com.contentdb.library_service.model.Library;
import com.contentdb.library_service.model.LibraryContent;
import com.contentdb.library_service.repository.LibraryContentRepository;
import com.contentdb.library_service.repository.LibraryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class LibraryContentService {
    private final LibraryContentRepository libraryContentRepository;
    private final LibraryRepository libraryRepository;
    private final ContentServiceClient contentServiceClient;

    public LibraryContentService(LibraryContentRepository libraryContentRepository, LibraryRepository libraryRepository, ContentServiceClient contentServiceClient) {
        this.libraryContentRepository = libraryContentRepository;
        this.libraryRepository = libraryRepository;
        this.contentServiceClient = contentServiceClient;
    }

    @Transactional
    public void addContentToLibrary(AddContentRequest request, String libraryName) {
        Library library = libraryRepository.findByLibraryName(libraryName)
                .orElseThrow(() -> new LibraryNotFoundException(new ExceptionMessage(
                        LocalDateTime.now().toString(),
                        HttpStatus.NOT_FOUND.value(),
                        "Not Found",
                        "Bu isme sahip kütüphane bulunamadı: " + libraryName,
                        "/libraryContent/addContentToLibrary",
                        "Aradığınız isimle bir kütüphane bulunamadı lütfen kontrol ettikten sonra tekrar deneyin. Kütüphane adı: " + libraryName
                )));

        String imdbId = contentServiceClient.getImdbIDByTitle(request.getTitle()).getBody().getImdbID();

        if (imdbId == null) {
            throw new ContentNotFoundException(new ExceptionMessage(
                    LocalDateTime.now().toString(),
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    "Kütüphaneye eklemeye çalıştığınız içerik bulunamadı :" + request.getTitle(),
                    "/libraryContent/addContentToLibrary",
                    "Kütüphaneye eklemeye çalıştığınız içerik bulunamadı. Sistemsel bir sıkıntı olabilir ya da içeriği" +
                            " yanlış isimle aratıyor olabilirsiniz. Lütfen tekrar deneyin :  " + request.getTitle()
            ));
        }

        LibraryContent libraryContent = new LibraryContent();
        libraryContent.setLibrary(library);
        libraryContent.setContentId(imdbId);
        libraryContentRepository.save(libraryContent);
    }

    @Transactional
    public List<LibraryContentDto> getAllContentsFromLibrary(String libraryName) {
        Library library = libraryRepository.findByLibraryName(libraryName)
                .orElseThrow(() -> new LibraryNotFoundException(new ExceptionMessage(
                        LocalDateTime.now().toString(),
                        HttpStatus.NOT_FOUND.value(),
                        "Not Found",
                        "Bu isme sahip kütüphane bulunamadı: " + libraryName,
                        "/libraryContent/addContentToLibrary",
                        "Aradığınız isimle bir kütüphane bulunamadı lütfen kontrol ettikten sonra tekrar deneyin. Kütüphane adı: " + libraryName
                )));

        List<LibraryContent> contents = libraryContentRepository.findByLibraryId(library.getId());

        return contents.stream()
                .map(LibraryContentDto::convertToLibraryContentDto)
                .toList();

    }

    @Transactional
    public List<ContentCardDto> getContentCardsFromLibrary(String libraryName) {

        List<LibraryContentDto> contentID = getAllContentsFromLibrary(libraryName);

        List<ContentCardDto> contentCards = new ArrayList<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (LibraryContentDto libraryContent : contentID) {

            String contentId = libraryContent.getContent_id();

            CompletableFuture<PosterRequest> posterFuture = CompletableFuture.supplyAsync(() -> {
                ResponseEntity<PosterRequest> posterResponse = contentServiceClient.getPosterByImdbID(contentId);
                if (posterResponse != null && posterResponse.getBody() != null) {
                    return posterResponse.getBody();
                } else {
                    return new PosterRequest("Filmin Posteri Bulunamadı!");
                }
            });

            CompletableFuture<DetailsRequest> detailsFuture = CompletableFuture.supplyAsync(() -> {
                ResponseEntity<DetailsRequest> detailsResponse = contentServiceClient.getDetailsByImdbID(contentId);
                if (detailsResponse != null && detailsResponse.getBody() != null) {
                    return detailsResponse.getBody();
                } else {
                    return new DetailsRequest("Unknown Title", "No plot available", "Unknown", "Unknown", "N/A", "Unknown", "Unknown", "Unknown");
                }
            });

            CompletableFuture<Void> combinedFuture = posterFuture.thenCombine(detailsFuture, (poster, details) -> {
                List<DetailsRequest> detailsList = new ArrayList<>();
                detailsList.add(details);
                ContentCardDto contentCard = new ContentCardDto(detailsList, poster);
                synchronized (contentCards) {
                    contentCards.add(contentCard);
                }
                return null;
            });

            futures.add(combinedFuture);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return contentCards;
    }
}
