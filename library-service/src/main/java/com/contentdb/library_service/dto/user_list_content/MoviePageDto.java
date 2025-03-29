package com.contentdb.library_service.dto.user_list_content;

import com.contentdb.library_service.client.dto.general.RecommendationResponse;
import com.contentdb.library_service.client.dto.general.TranslationsResponse;
import com.contentdb.library_service.client.dto.movie.MovieCreditsResponse;
import com.contentdb.library_service.client.dto.movie.MovieDetailResponse;

import java.util.List;

public class MoviePageDto {

    private MovieDetailResponse details;
    private MovieCreditsResponse credits;
    private List<RecommendationResponse> recommendations;
    private List<TranslationsResponse> translations;

    public MoviePageDto() {
    }

    public MoviePageDto(MovieDetailResponse details, MovieCreditsResponse credits, List<RecommendationResponse> recommendations, List<TranslationsResponse> translations) {
        this.details = details;
        this.credits = credits;
        this.recommendations = recommendations;
        this.translations = translations;
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
