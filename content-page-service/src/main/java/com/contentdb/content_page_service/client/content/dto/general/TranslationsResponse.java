package com.contentdb.content_page_service.client.content.dto.general;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TranslationsResponse {
    @JsonProperty("iso_639_1")
    private String iso639_1;

    @JsonProperty("english_name")
    private String englishName;

    public String getIso639_1() {
        return iso639_1;
    }

    public String getEnglishName() {
        return englishName;
    }
}
