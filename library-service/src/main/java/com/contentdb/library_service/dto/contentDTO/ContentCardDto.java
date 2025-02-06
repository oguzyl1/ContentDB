package com.contentdb.library_service.dto.contentDTO;

import java.util.List;

public class ContentCardDto {
    private List<DetailsRequest> detailsList;
    private PosterRequest poster;

    public ContentCardDto(List<DetailsRequest> detailsList, PosterRequest poster) {
        this.detailsList = detailsList;
        this.poster = poster;
    }

    public ContentCardDto() {
    }

    public List<DetailsRequest> getDetailsList() {
        return detailsList;
    }

    public void setDetailsList(List<DetailsRequest> detailsList) {
        this.detailsList = detailsList;
    }

    public PosterRequest getPoster() {
        return poster;
    }

    public void setPoster(PosterRequest poster) {
        this.poster = poster;
    }
}
