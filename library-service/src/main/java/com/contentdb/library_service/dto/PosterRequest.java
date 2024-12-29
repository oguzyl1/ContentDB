package com.contentdb.library_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PosterRequest {

    @JsonProperty("Poster")
    private String poster;

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public PosterRequest(String poster) {
        this.poster = poster;
    }

    public PosterRequest() {
    }
}
