package com.contentdb.library_service.client.dto.general;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CrewMember {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("job")
    private String job;

    @JsonProperty("profile_path")
    private String profilePath;

    public String getProfilePath() {
        return profilePath;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getJob() {
        return job;
    }
}
