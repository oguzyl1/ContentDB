package com.contentdb.content_page_service.client.content.dto.series;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductionCountry {

    @JsonProperty("name")
    private String countryName;

    public String getCountryName() {
        return countryName;
    }
}
