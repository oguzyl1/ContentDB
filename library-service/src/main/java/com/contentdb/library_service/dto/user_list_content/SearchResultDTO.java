package com.contentdb.library_service.dto.user_list_content;

public class SearchResultDTO {

    private String tmdbId;
    private String contentType;
    private String title;
    private String posterUrl;


    public SearchResultDTO() {
    }

    public SearchResultDTO(String tmdbId, String contentType, String title, String posterUrl) {
        this.tmdbId = tmdbId;
        this.contentType = contentType;
        this.title = title;
        this.posterUrl = posterUrl;
    }

    public String getTmdbId() {
        return tmdbId;
    }

    public String getContentType() {
        return contentType;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }
}
