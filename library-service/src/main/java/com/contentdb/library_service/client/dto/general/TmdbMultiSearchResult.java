package com.contentdb.library_service.client.dto.general;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TmdbMultiSearchResult {
    @JsonProperty("results")
    private List<TmdbMultiSearchResponse> results;

    public List<TmdbMultiSearchResponse> getResults() {
        return results;
    }
}