package com.contentdb.library_service.client.dto.series;

import com.contentdb.library_service.client.dto.general.RecommendationResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class TvRecommendationResult implements Serializable {

    @JsonProperty("results")
    private List<RecommendationResponse> results;


    public List<RecommendationResponse> getResults() {
        return results;
    }
}
