package com.contentdb.library_service.dto;

import com.contentdb.library_service.model.LibraryContent;

public class LibraryContentDto {

    private String content_id;

    public static LibraryContentDto convertToLibraryContentDto(LibraryContent libraryContent) {
        return new LibraryContentDto(
                libraryContent.getContentId()
        );
    }

    public LibraryContentDto(String content_id) {
        this.content_id = content_id;
    }

    public LibraryContentDto() {

    }

    public String getContent_id() {
        return content_id;
    }

    public void setContent_id(String content_id) {
        this.content_id = content_id;
    }
}
