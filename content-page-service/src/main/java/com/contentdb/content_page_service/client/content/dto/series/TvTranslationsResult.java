package com.contentdb.content_page_service.client.content.dto.series;

import com.contentdb.content_page_service.client.content.dto.general.TranslationsResponse;
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
