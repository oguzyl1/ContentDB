package com.contentdb.library_service.dto.libraryDTO;

import com.contentdb.library_service.model.Library;


public class LibraryDto {

    private String name;
    private String userId;

    public static LibraryDto convertToDto(Library library) {
        return new LibraryDto(
                library.getLibraryName()
        );
    }

    public LibraryDto(String name) {
        this.name = name;
    }

    public LibraryDto() {

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}
