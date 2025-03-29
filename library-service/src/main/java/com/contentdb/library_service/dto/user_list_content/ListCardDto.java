package com.contentdb.library_service.dto.user_list_content;

public class ListCardDto {
    private String id;
    private String name;
    private String overview;
    private String image;

    public ListCardDto(String id, String name, String overview, String image) {
        this.id = id;
        this.name = name;
        this.overview = overview;
        this.image = image;
    }

    public ListCardDto() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOverview() {
        return overview;
    }

    public String getImage() {
        return image;
    }
}
