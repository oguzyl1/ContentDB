package com.contentdb.content_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SearchResponse {

    @JsonProperty("Search")
    private List<SearchRequest> search;

    @JsonProperty("totalResults")
    private String totalResults;

    @JsonProperty("Response")
    private String response;

    public SearchResponse(List<SearchRequest> search, String totalResults, String response) {
        this.search = search;
        this.totalResults = totalResults;
        this.response = response;
    }

    public SearchResponse() {
    }

    public List<SearchRequest> getSearch() {
        return search;
    }

    public void setSearch(List<SearchRequest> search) {
        this.search = search;
    }

    public String getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(String totalResults) {
        this.totalResults = totalResults;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
