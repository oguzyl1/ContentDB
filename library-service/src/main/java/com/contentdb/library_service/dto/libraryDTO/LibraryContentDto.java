package com.contentdb.library_service.dto.libraryDTO;

import com.contentdb.library_service.model.LibraryContent;

public class LibraryContentDto {

    private String contentId;

    public static LibraryContentDto convertToLibraryContentDto(LibraryContent libraryContent) {
        return new LibraryContentDto(
                libraryContent.getContentId()
        );
    }

    public LibraryContentDto(String contentId) {
        this.contentId = contentId;
    }

    public LibraryContentDto() {

    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }
}
