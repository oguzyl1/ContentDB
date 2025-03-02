package com.contentdb.content_service.client.dto.series;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Jobs {

    @JsonProperty("job")
    private String job;

    @JsonProperty("episode_count")
    private String episodeCount;

    public String getJob() {
        return job;
    }
}
