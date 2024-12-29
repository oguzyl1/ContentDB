// içeriğin imdbID'sini almak için sınıf
package com.contentdb.library_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImdbIDRequest {

    @JsonProperty("imdbID")
    private String ImdbID;

    public String getImdbID() {
        return ImdbID;
    }

    public void setImdbID(String imdbID) {
        ImdbID = imdbID;
    }

    public ImdbIDRequest(String imdbID) {
        ImdbID = imdbID;
    }

    public ImdbIDRequest() {
    }
}
