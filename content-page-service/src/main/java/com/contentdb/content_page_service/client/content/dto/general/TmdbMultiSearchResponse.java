package com.contentdb.content_page_service.client.content.dto.general;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class TmdbMultiSearchResponse implements Serializable {

    @JsonProperty("id")
    private Integer id;

    /**
     * Film ve dizilerde farklı isimlendirme olabilir:
     * - TV dizileri için: "name"
     * - Filmler için: "title"
     * Bu yüzden her iki ismi de kabul etmek için @JsonAlias kullanıyoruz.
     */
    @JsonProperty("name")
    @JsonAlias({"title"})
    private String titleTr;

    @JsonProperty("original_name")
    @JsonAlias("original_title")
    private String title;

    @JsonProperty("overview")
    private String overview;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("media_type")
    private String mediaType;

    /**
     * Yayın tarihi de farklı olabilir:
     * - TV dizileri için: "first_air_date"
     * - Filmler için: "release_date"
     */
    @JsonProperty("first_air_date")
    @JsonAlias({"release_date"})
    private String firstAirDate;

    @JsonProperty("vote_average")
    private Double voteAverage;


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

    public String getPosterPath() {
        return posterPath;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }
}
