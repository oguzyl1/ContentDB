package com.contentdb.content_service.client.dto.series;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class TvAggregateCreditsResponse implements Serializable {

    @JsonProperty("id")
    private String id;

    @JsonProperty("crew")
    private List<AggregateCrewMember> crew;

    @JsonProperty("cast")
    private List<AggregateCastMember> cast;



}
