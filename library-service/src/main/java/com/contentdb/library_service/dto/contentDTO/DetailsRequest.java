package com.contentdb.library_service.dto.contentDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DetailsRequest {

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Plot")
    private String plot;

    @JsonProperty("Genre")
    private String genre;

    @JsonProperty("Year")
    private String year;

    @JsonProperty("imdbRating")
    private String imdbRating;

    @JsonProperty("Runtime")
    private String runtime;

    @JsonProperty("Director")
    private String director;

    @JsonProperty("Actors")
    private String actors;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(String imdbRating) {
        this.imdbRating = imdbRating;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public DetailsRequest(String title, String plot, String genre, String year, String imdbRating, String runtime, String director, String actors) {
        this.title = title;
        this.plot = plot;
        this.genre = genre;
        this.year = year;
        this.imdbRating = imdbRating;
        this.runtime = runtime;
        this.director = director;
        this.actors = actors;
    }

    public DetailsRequest() {
    }
}
