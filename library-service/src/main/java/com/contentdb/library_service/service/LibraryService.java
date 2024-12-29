package com.contentdb.library_service.service;

import com.contentdb.library_service.dto.LibraryDto;
import com.contentdb.library_service.exception.ExceptionMessage;
import com.contentdb.library_service.exception.LibraryAlreadyExistException;
import com.contentdb.library_service.exception.LibraryNotFoundException;
import com.contentdb.library_service.model.Library;
import com.contentdb.library_service.repository.LibraryRepository;
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

    public LibraryService(LibraryRepository libraryRepository) {
        this.libraryRepository = libraryRepository;
    }

    @Transactional
    public LibraryDto createLibrary(LibraryDto libraryDto) {
        Optional<Library> existingLibrary = libraryRepository.findByLibraryName(libraryDto.getName());
        if (existingLibrary.isPresent()) {
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
        Library savedLibrary = libraryRepository.save(new Library(libraryDto.getName()));
        return new LibraryDto(savedLibrary.getLibraryName());
    }

    @Transactional
    public List<LibraryDto> getAllLibraries() {
        List<Library> libraries = libraryRepository.findAll();
        if (libraries.isEmpty()) {
            return Collections.emptyList();
        }
        return libraries.stream()
                .map(library -> new LibraryDto(library.getLibraryName()))
                .toList();
    }

    @Transactional
    public LibraryDto getLibraryByName(String libraryName) {
        Library searchedLibrary = libraryRepository.findByLibraryName(libraryName).orElseThrow(
                () -> new LibraryNotFoundException(
                        new ExceptionMessage(
                                LocalDateTime.now().toString(),
                                HttpStatus.NOT_FOUND.value(),
                                "Not Found",
                                "Bu isme sahip kütüphane bulunamadı: " + libraryName,
                                "/library/getLibraryByName",
                                "Aradığınız isimle bir kütüphane bulunamadı lütfen kontrol ettikten sonra tekrar deneyin. Kütüphane adı: " + libraryName
                        )
                )
        );
        return new LibraryDto(searchedLibrary.getLibraryName());
    }

    @Transactional
    public LibraryDto updateLibraryName(String wantToReplacedLibraryName, LibraryDto libraryDto) {
        Optional<Library> existingLibrary = libraryRepository.findByLibraryName(libraryDto.getName());
        if (existingLibrary.isPresent()) {
            throw new LibraryAlreadyExistException(
                    new ExceptionMessage(
                            LocalDateTime.now().toString(),
                            HttpStatus.CONFLICT.value(),
                            "Conflict",
                            "Bu isme sahip kütüphane zaten bulunmakta: " + libraryDto.getName(),
                            "/library/updateLibraryName",
                            "Güncelleme sırasında kullandığınız isimle daha önce bir kütüphane oluşturulmuş. Lütfen farklı bir isim kullanınız. Kütüphane adı: " + libraryDto.getName()
                    )
            );
        }

        Optional<Library> libraryToUpdate = libraryRepository.findByLibraryName(wantToReplacedLibraryName);
        if (libraryToUpdate.isEmpty()) {
            throw new LibraryNotFoundException(
                    new ExceptionMessage(
                            LocalDateTime.now().toString(),
                            HttpStatus.NOT_FOUND.value(),
                            "Not Found",
                            "Bu isme sahip kütüphane bulunamadı: " + wantToReplacedLibraryName,
                            "/library/updateLibraryName",
                            "Güncellemek istediğiniz kütüphane bulunamadı lütfen kütüphane adını kontrol ediniz. Kütüphane adı: " + wantToReplacedLibraryName
                    )
            );
        }

        Library updatedLibrary = libraryToUpdate.get();
        updatedLibrary.setLibraryName(libraryDto.getName());
        libraryRepository.save(updatedLibrary);
        return new LibraryDto(updatedLibrary.getLibraryName());
    }

    @Transactional
    public void deleteLibrary(String libraryName) {
        Optional<Library> library = libraryRepository.findByLibraryName(libraryName);
        library.ifPresent(libraryRepository::delete);
    }
}
