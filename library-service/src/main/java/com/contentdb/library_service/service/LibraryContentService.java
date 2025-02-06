package com.contentdb.library_service.service;

import com.contentdb.library_service.client.ContentServiceClient;
import com.contentdb.library_service.dto.contentDTO.*;
import com.contentdb.library_service.dto.libraryDTO.LibraryContentDto;
import com.contentdb.library_service.exception.ContentNotFoundException;
import com.contentdb.library_service.exception.ExceptionMessage;
import com.contentdb.library_service.exception.LibraryNotFoundException;
import com.contentdb.library_service.model.Library;
import com.contentdb.library_service.model.LibraryContent;
import com.contentdb.library_service.repository.LibraryContentRepository;
import com.contentdb.library_service.repository.LibraryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class LibraryContentService {

    private static final Logger logger = LoggerFactory.getLogger(LibraryContentService.class);
    private final LibraryContentRepository libraryContentRepository;
    private final LibraryRepository libraryRepository;
    private final ContentServiceClient contentServiceClient;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);


    public LibraryContentService(LibraryContentRepository libraryContentRepository, LibraryRepository libraryRepository, ContentServiceClient contentServiceClient) {
        this.libraryContentRepository = libraryContentRepository;
        this.libraryRepository = libraryRepository;
        this.contentServiceClient = contentServiceClient;
    }


    /**
     * Kütüphaneye yeni içerik ekler.
     *
     * <p>
     * Öncelikle, kullanıcının belirttiği isimde ve kullanıcıya ait kütüphane bulunur.
     * Daha sonra, içerik başlığı kullanılarak ContentServiceClient üzerinden IMDb ID alınır.
     * Eğer içerik bulunamazsa, ContentNotFoundException fırlatılır; aksi halde, içerik kütüphaneye eklenir.
     * </p>
     *
     * @param request     Eklenmek istenen içerik bilgilerini içeren DTO (örneğin, içerik başlığı)
     * @param libraryName İçeriğin ekleneceği kütüphanenin adı
     * @param userId      Kütüphanenin sahibi kullanıcı ID'si
     *
     * @throws LibraryNotFoundException Eğer kütüphane bulunamaz veya kullanıcıya ait değilse
     * @throws ContentNotFoundException Eğer içerik başlığına ait IMDb ID alınamazsa
     */
    @Transactional
    public void addContentToLibrary(AddContentRequest request, String libraryName, String userId) {
        Library library = libraryRepository.findByLibraryNameAndUserId(libraryName, userId)
                .orElseThrow(() -> new LibraryNotFoundException(new ExceptionMessage(
                        LocalDateTime.now().toString(),
                        HttpStatus.NOT_FOUND.value(),
                        "Not Found",
                        "Bu isme sahip kütüphane bulunamadı veya bu kütüphane sizin değil: " + libraryName,
                        "/libraryContent/addContentToLibrary",
                        "Aradığınız isimle bir kütüphane bulunamadı ya da kütüphaneye erişim yetkiniz yok. Kütüphane adı: " + libraryName
                )));

        ResponseEntity<ImdbIDRequest> imdbResponse = contentServiceClient.getImdbIDByTitle(request.getTitle());
        if (!imdbResponse.getStatusCode().is2xxSuccessful() || imdbResponse.getBody() == null) {
            logger.error("Content not found for title: {}", request.getTitle());
            throw new ContentNotFoundException(new ExceptionMessage(
                    LocalDateTime.now().toString(),
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    "Kütüphaneye eklemeye çalıştığınız içerik bulunamadı: " + request.getTitle(),
                    "/libraryContent/addContentToLibrary",
                    "İçerik bulunamadı. Lütfen tekrar deneyin: " + request.getTitle()
            ));
        }
        String imdbId = imdbResponse.getBody().getImdbID();

        LibraryContent libraryContent = new LibraryContent();
        libraryContent.setLibrary(library);
        libraryContent.setContentId(imdbId);
        libraryContentRepository.save(libraryContent);
    }


    /**
     * Belirtilen kütüphaneye ait tüm içerikleri DTO listesi olarak döner.
     *
     * <p>
     * Kütüphane ismi ve kullanıcı ID'si ile kütüphane bulunur, ardından kütüphaneye ait içerikler
     * çekilerek DTO'ya dönüştürülür.
     * </p>
     *
     * @param libraryName Kütüphane adı
     * @param userId      Kütüphanenin sahibi kullanıcı ID'si
     *
     * @return Kütüphaneye ait içeriklerin DTO listesini döner
     *
     * @throws LibraryNotFoundException Eğer kütüphane bulunamaz veya kullanıcıya ait değilse
     */
    @Transactional(readOnly = true)
    public List<LibraryContentDto> getAllContentsFromLibrary(String libraryName, String userId) {
        Library library = libraryRepository.findByLibraryNameAndUserId(libraryName, userId)
                .orElseThrow(() -> new LibraryNotFoundException(new ExceptionMessage(
                        LocalDateTime.now().toString(),
                        HttpStatus.NOT_FOUND.value(),
                        "Not Found",
                        "Bu isme sahip kütüphane bulunamadı veya bu kütüphane sizin değil: " + libraryName,
                        "/libraryContent/getAllContentsFromLibrary",
                        "Aradığınız isimle bir kütüphane bulunamadı ya da kütüphaneye erişim yetkiniz yok. Kütüphane adı: " + libraryName
                )));

        List<LibraryContent> contents = libraryContentRepository.findByLibraryId(library.getId());
        return contents.stream()
                .map(LibraryContentDto::convertToLibraryContentDto)
                .toList();

    }


    /**
     * Kütüphanedeki içeriklere ait kart bilgilerini döner.
     *
     * <p>
     * Her içerik için, asenkron olarak Poster ve Detay bilgileri ContentServiceClient üzerinden alınır.
     * Alınan bilgiler, ContentCardDto içinde birleştirilir ve sonuç listesi döndürülür.
     * </p>
     *
     * @param libraryName Kütüphane adı
     * @param userId      Kütüphanenin sahibi kullanıcı ID'si
     *
     * @return İçerik kartlarının bulunduğu DTO listesini döner
     *
     * @throws LibraryNotFoundException Eğer kütüphane bulunamaz veya kullanıcıya ait değilse
     */
    @Transactional(readOnly = true)
    public List<ContentCardDto> getContentCardsFromLibrary(String libraryName, String userId) {
        List<LibraryContentDto> contentIDs = getAllContentsFromLibrary(libraryName, userId);
        List<ContentCardDto> contentCards = new ArrayList<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (LibraryContentDto libraryContent : contentIDs) {
            String contentId = libraryContent.getContentId();

            CompletableFuture<PosterRequest> posterFuture = CompletableFuture.supplyAsync(() -> {
                ResponseEntity<PosterRequest> posterResponse = contentServiceClient.getPosterByImdbID(contentId);
                if (posterResponse != null && posterResponse.getBody() != null) {
                    return posterResponse.getBody();
                } else {
                    return new PosterRequest("Filmin Posteri Bulunamadı!");
                }
            }, executorService).exceptionally(ex -> {
                logger.error("Error fetching poster for contentId {}: {}", contentId, ex.getMessage());
                return new PosterRequest("Filmin Posteri Bulunamadı!");
            });

            CompletableFuture<DetailsRequest> detailsFuture = CompletableFuture.supplyAsync(() -> {
                ResponseEntity<DetailsRequest> detailsResponse = contentServiceClient.getDetailsByImdbID(contentId);
                if (detailsResponse != null && detailsResponse.getBody() != null) {
                    return detailsResponse.getBody();
                } else {
                    return new DetailsRequest("Unknown Title", "No plot available", "Unknown", "Unknown", "N/A", "Unknown", "Unknown", "Unknown");
                }
            }, executorService).exceptionally(ex -> {
                logger.error("Error fetching details for contentId {}: {}", contentId, ex.getMessage());
                return new DetailsRequest("Unknown Title", "No plot available", "Unknown", "Unknown", "N/A", "Unknown", "Unknown", "Unknown");
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
