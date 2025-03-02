package com.contentdb.content_service.client.dto.movie;

import com.contentdb.content_service.client.dto.general.CastMember;
import com.contentdb.content_service.client.dto.general.CrewMember;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class MovieCreditsResponse implements Serializable {

    @JsonProperty("id")
    private String id;

    @JsonProperty("cast")
    private List<CastMember> cast;

    @JsonProperty("crew")
    private List<CrewMember> crew;


    public String getId() {
        return id;
    }

    public List<CastMember> getCast() {
        return cast;
    }

    public List<CrewMember> getCrew() {
        return crew;
    }
}
