package com.contentdb.library_service.repository;

import com.contentdb.library_service.model.LibraryContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryContentRepository extends JpaRepository<LibraryContent, String> {

}
