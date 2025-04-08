package com.contentdb.content_page_service.client.content.dto.movie;

import com.contentdb.content_page_service.client.content.dto.general.RecommendationResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class MovieRecommendationResult implements Serializable {

    @JsonProperty("results")
    private List<RecommendationResponse> results;


    public List<RecommendationResponse> getResults() {
        return results;
    }
}
