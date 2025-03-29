package com.contentdb.library_service.client.dto.series;

import com.contentdb.library_service.client.dto.general.Genre;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class TvDetailResponse implements Serializable {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String titleTr;

    @JsonProperty("original_name")
    private String title;

    @JsonProperty("overview")
    private String overview;

    @JsonProperty("episode_run_time")
    private List<String> episodeRunTime;

    @JsonProperty("genres")
    private List<Genre> genres;

    @JsonProperty("first_air_date")
    private String firstAirDate;

    @JsonProperty("last_air_date")
    private String lastAirDate;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("backdrop_path")
    private String backdropPath;

    @JsonProperty("vote_average")
    private Double voteAverage;

    @JsonProperty("vote_count")
    private Integer voteCount;

    @JsonProperty("status")
    private String status;

    @JsonProperty("tagline")
    private String tagline;

    @JsonProperty("number_of_seasons")
    private Integer numberOfSeasons;

    @JsonProperty("number_of_episodes")
    private Integer numberOfEpisodes;

    @JsonProperty("languages")
    private List<String> languages;

    @JsonProperty("production_countries")
    private List<ProductionCountry> productionCountries;

    public Integer getId() {
        return id;
    }

    public String getTitleTr() {
        return titleTr;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public List<String> getEpisodeRunTime() {
        return episodeRunTime;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public String getLastAirDate() {
        return lastAirDate;
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

    public String getStatus() {
        return status;
    }

    public String getTagline() {
        return tagline;
    }

    public Integer getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public Integer getNumberOfEpisodes() {
        return numberOfEpisodes;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public List<ProductionCountry> getProductionCountries() {
        return productionCountries;
    }
}
