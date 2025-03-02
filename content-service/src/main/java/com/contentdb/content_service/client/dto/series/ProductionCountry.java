package com.contentdb.content_service.client.dto.series;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductionCountry {

    @JsonProperty("name")
    private String countryName;

    public String getCountryName() {
        return countryName;
    }
}
