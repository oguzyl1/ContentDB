package com.contentdb.library_service.request;

public class AddContentRequest {
    private String title;

    public AddContentRequest(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public AddContentRequest() {
    }

}
