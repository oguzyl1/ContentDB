package com.contentdb.content_page_service.dto;

import com.contentdb.content_page_service.client.content.dto.general.RecommendationResponse;
import com.contentdb.content_page_service.client.content.dto.general.TranslationsResponse;
import com.contentdb.content_page_service.client.content.dto.movie.MovieCreditsResponse;
import com.contentdb.content_page_service.client.content.dto.movie.MovieDetailResponse;

import java.util.List;
import java.util.Map;

public class MoviePageDto {

    private MovieDetailResponse details;
    private MovieCreditsResponse credits;
    private List<RecommendationResponse> recommendations;
    private List<TranslationsResponse> translations;
    private Map<String, Object> comment;

    public MoviePageDto() {
    }

    public MoviePageDto(MovieDetailResponse details, MovieCreditsResponse credits, List<RecommendationResponse> recommendations, List<TranslationsResponse> translations, Map<String, Object> comment) {
        this.details = details;
        this.credits = credits;
        this.recommendations = recommendations;
        this.translations = translations;
        this.comment = comment;
    }

    public Map<String, Object> getComment() {
        return comment;
    }

    public MovieDetailResponse getDetails() {
        return details;
    }

    public MovieCreditsResponse getCredits() {
        return credits;
    }

    public List<RecommendationResponse> getRecommendations() {
        return recommendations;
    }

    public List<TranslationsResponse> getTranslations() {
        return translations;
    }
}
