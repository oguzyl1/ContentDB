package com.contentdb.content_service.client.dto.general;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CastMember {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("character")
    private String character;

    @JsonProperty("profile_path")
    private String profilePath;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCharacter() {
        return character;
    }

    public String getProfilePath() {
        return profilePath;
    }
}
