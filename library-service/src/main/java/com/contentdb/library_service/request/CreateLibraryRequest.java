package com.contentdb.library_service.request;

public record CreateLibraryRequest(String name,
                                   String description,
                                   boolean isPublic) {}
