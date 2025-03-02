package com.contentdb.content_service.client.dto.series;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AggregateCastMember {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("roles")
    private List<Roles> roles;

    @JsonProperty("profile_path")
    private String profilePath;

    @JsonProperty("total_episode_count")
    private String totalEpisodeCount;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Roles> getRoles() {
        return roles;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public String getTotalEpisodeCount() {
        return totalEpisodeCount;
    }
}
