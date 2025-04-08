package com.contentdb.content_page_service.client.content.dto.general;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TmdbMultiSearchResult {
    @JsonProperty("results")
    private List<TmdbMultiSearchResponse> results;

    public List<TmdbMultiSearchResponse> getResults() {
        return results;
    }
}