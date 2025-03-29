package com.contentdb.library_service.client.dto.series;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Roles {

    @JsonProperty("character")
    private String character;

    @JsonProperty("episode_count")
    private String episodeCount;

    public String getCharacter() {
        return character;
    }

    public String getEpisodeCount() {
        return episodeCount;
    }
}
