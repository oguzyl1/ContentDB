package com.contentdb.library_service.repository;

import com.contentdb.library_service.model.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LibraryRepository extends JpaRepository<Library, String> {
    Optional<Library> findByLibraryName(String libraryName);
}