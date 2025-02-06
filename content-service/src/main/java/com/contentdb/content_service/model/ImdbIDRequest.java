// içeriğin imdbID'sini almak için sınıf
package com.contentdb.content_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class ImdbIDRequest implements Serializable {

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
