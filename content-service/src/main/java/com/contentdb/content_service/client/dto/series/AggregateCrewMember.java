package com.contentdb.content_service.client.dto.series;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AggregateCrewMember {


    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("jobs")
    private List<Jobs> jobs;

    @JsonProperty("profile_path")
    private String profilePath;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Jobs> getJobs() {
        return jobs;
    }

    public String getProfilePath() {
        return profilePath;
    }
}
