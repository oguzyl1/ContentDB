package com.contentdb.content_service.client.dto.movie;

import com.contentdb.content_service.client.dto.general.Genre;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class MovieDetailResponse implements Serializable {

    @JsonProperty("id")
    private String id;

    @JsonProperty("original_title")
    private String title;

    @JsonProperty("title")
    private String titleTR;

    @JsonProperty("original_language")
    private String language;

    @JsonProperty("overview")
    private String overview;

    @JsonProperty("genres")
    private List<Genre> genres;

    @JsonProperty("release_date")
    private String releaseDate;

    @JsonProperty("runtime")
    private Integer runtime;

    @JsonProperty("vote_average")
    private Double voteAverage;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("backdrop_path")
    private String backdropPath;

    @JsonProperty("imdb_id")
    private String imdbId;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getTitleTR() {
        return titleTR;
    }

    public String getLanguage() {
        return language;
    }

    public String getOverview() {
        return overview;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getImdbId() {
        return imdbId;
    }
}
