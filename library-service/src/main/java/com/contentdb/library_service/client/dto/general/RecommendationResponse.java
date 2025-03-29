package com.contentdb.library_service.client.dto.general;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RecommendationResponse {


    @JsonProperty("id")
    private String id;

    @JsonProperty("original_name")
    @JsonAlias("original_title")
    private String title;

    @JsonProperty("name")
    @JsonAlias({"title"})
    private String titleTR;

    @JsonProperty("overview")
    private String overview;

    @JsonProperty("media_type")
    private String mediaType;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("backdrop_path")
    private String backdropPath;

    @JsonProperty("vote_average")
    private Double voteAverage;

    @JsonProperty("vote_count")
    private Integer voteCount;

    @JsonProperty("genre_ids")
    private List<Integer> genreIds;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getTitleTR() {
        return titleTR;
    }

    public String getOverview() {
        return overview;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }
}
