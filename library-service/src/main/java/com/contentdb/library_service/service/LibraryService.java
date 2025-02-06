package com.contentdb.library_service.service;

import com.contentdb.library_service.dto.libraryDTO.LibraryDto;
import com.contentdb.library_service.exception.ExceptionMessage;
import com.contentdb.library_service.exception.LibraryAlreadyExistException;
import com.contentdb.library_service.exception.LibraryNotFoundException;
import com.contentdb.library_service.model.Library;
import com.contentdb.library_service.repository.LibraryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class LibraryService {

    private final LibraryRepository libraryRepository;
    private static final Logger logger = LoggerFactory.getLogger(LibraryService.class);

    public LibraryService(LibraryRepository libraryRepository) {
        this.libraryRepository = libraryRepository;
    }


    /**
     * Kullanıcının kütüphane oluşturmasını sağlar
     *
     * @param libraryDto oluşturulacak kütüphanenin içeriği
     * @param userId kullanıcı id'si
     * @return oluşturulan kütüphaneyi döner
     */
    @Transactional
    public LibraryDto createLibrary(LibraryDto libraryDto, String userId) {
        Optional<Library> existingLibrary = libraryRepository.findByLibraryNameAndUserId(libraryDto.getName(), userId);
        if (existingLibrary.isPresent()) {
            logger.error("Library already exists: {}", libraryDto.getName());
            throw new LibraryAlreadyExistException(
                    new ExceptionMessage(
                            LocalDateTime.now().toString(),
                            HttpStatus.CONFLICT.value(),
                            "Conflict",
                            "Bu isme sahip kütüphane zaten bulunmakta: " + libraryDto.getName(),
                            "/library/createLibrary",
                            "Bu isimle daha önce bir kütüphane oluşturulmuş. Lütfen farklı bir isimle deneyin. Kütüphane adı: " + libraryDto.getName()
                    )
            );
        }
        Library newLibrary = new Library(libraryDto.getName());
        newLibrary.setUserId(userId);
        Library savedLibrary = libraryRepository.save(newLibrary);
        return new LibraryDto(savedLibrary.getLibraryName());
    }


    /**
     * kullanıcıya ait tüm kütüphaneleri getirir
     *
     * @param userId kullanıcı id'si
     * @return kullanıcının tüm kütüphanelerini döndürür
     */
    @Transactional(readOnly = true)
    public List<LibraryDto> getAllLibraries(String userId) {
        List<Library> libraries = libraryRepository.findByUserId(userId);
        if (libraries.isEmpty()) {
            return Collections.emptyList();
        }
        return libraries.stream()
                .map(library -> new LibraryDto(library.getLibraryName()))
                .toList();
    }


    /**
     * Kütüphane adı ile aratılan kütüphaneyi getirir
     *
     * @param libraryName aranan kütüphanenin adı
     * @param userId kullanıcı id'si
     * @return aranan kütüphaneyi getirir
     */
    @Transactional(readOnly = true)
    public LibraryDto getLibraryByName(String libraryName, String userId) {
        Library searchedLibrary = libraryRepository.findByLibraryNameAndUserId(libraryName, userId)
                .orElseThrow(() -> new LibraryNotFoundException(
                        new ExceptionMessage(
                                LocalDateTime.now().toString(),
                                HttpStatus.NOT_FOUND.value(),
                                "Not Found",
                                "Bu isme sahip kütüphane bulunamadı: " + libraryName,
                                "/library/getLibraryByName",
                                "Aradığınız isimle bir kütüphane bulunamadı lütfen kontrol ettikten sonra tekrar deneyin. Kütüphane adı: " + libraryName
                        )
                ));
        return new LibraryDto(searchedLibrary.getLibraryName());
    }


    /**
     * Kütüphanenin adını güncellemek için kullanılır
     *
     * @param currentLibraryName güncellenmek istenen kütüphane adı
     * @param libraryDto yeni kütüphane bilgileri
     * @param userId kullanıcı id'si
     * @return güncellenen değerleri döndürür
     */
    @Transactional
    public LibraryDto updateLibraryName(String currentLibraryName, LibraryDto libraryDto, String userId) {
        Optional<Library> libraryToUpdate = libraryRepository.findByLibraryNameAndUserId(currentLibraryName, userId);
        if (libraryToUpdate.isEmpty()) {
            logger.error("Library not found: {}", currentLibraryName);
            throw new LibraryNotFoundException(
                    new ExceptionMessage(
                            LocalDateTime.now().toString(),
                            HttpStatus.NOT_FOUND.value(),
                            "Not Found",
                            "Kütüphane bulunamadı: " + currentLibraryName,
                            "/library/updateLibraryName",
                            "Güncelleme yapmak istediğiniz kütüphane bulunamadı. Lütfen kütüphane adını kontrol ediniz."
                    )
            );
        }

        Optional<Library> duplicateLibrary = libraryRepository.findByLibraryNameAndUserId(libraryDto.getName(), userId);
        if (duplicateLibrary.isPresent()) {
            logger.error("Library already exists with new name: {}", libraryDto.getName());
            throw new LibraryAlreadyExistException(
                    new ExceptionMessage(
                            LocalDateTime.now().toString(),
                            HttpStatus.CONFLICT.value(),
                            "Conflict",
                            "Bu isimde bir kütüphane zaten mevcut: " + libraryDto.getName(),
                            "/library/updateLibraryName",
                            "Yeni isim olarak seçtiğiniz kütüphane adı zaten kullanılmakta. Lütfen farklı bir isim deneyin."
                    )
            );
        }

        Library library = libraryToUpdate.get();
        library.setLibraryName(libraryDto.getName());
        libraryRepository.save(library);
        return new LibraryDto(library.getLibraryName());
    }


    /**
     * Kütüphane silme işlemini gerçekleştirir
     *
     * @param libraryName silinecek kütüphanenin adı
     * @param userId kullanıcı id'si
     */
    @Transactional
    public void deleteLibrary(String libraryName, String userId) {
        Optional<Library> library = libraryRepository.findByLibraryNameAndUserId(libraryName, userId);
        library.ifPresentOrElse(
                libraryRepository::delete,
                () -> {
                    logger.error("Library not found for deletion: {}", libraryName);
                    throw new LibraryNotFoundException(
                            new ExceptionMessage(
                                    LocalDateTime.now().toString(),
                                    HttpStatus.NOT_FOUND.value(),
                                    "Not Found",
                                    "Bu isme sahip kütüphane bulunamadı: " + libraryName,
                                    "/library/deleteLibrary",
                                    "Silmek istediğiniz kütüphane bulunamadı lütfen kütüphane adını kontrol ediniz. Kütüphane adı: " + libraryName
                            )
                    );
                }
        );
    }
}
