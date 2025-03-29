package com.contentdb.library_service.client.dto.series;

import com.contentdb.library_service.client.dto.general.TranslationsResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class TvTranslationsResult implements Serializable {

    @JsonProperty("id")
    private String id;

    @JsonProperty("translations")
    private List<TranslationsResponse> results;

    public String getId() {
        return id;
    }

    public List<TranslationsResponse> getResults() {
        return results;
    }
}
