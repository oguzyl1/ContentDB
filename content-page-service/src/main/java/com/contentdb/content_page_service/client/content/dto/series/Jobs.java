package com.contentdb.content_page_service.client.content.dto.series;

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
