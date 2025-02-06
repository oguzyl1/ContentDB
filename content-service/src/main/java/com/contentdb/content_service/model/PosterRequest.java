package com.contentdb.content_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class PosterRequest implements Serializable {

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
