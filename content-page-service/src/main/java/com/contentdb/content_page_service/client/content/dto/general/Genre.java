package com.contentdb.content_page_service.client.content.dto.general;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Genre {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

