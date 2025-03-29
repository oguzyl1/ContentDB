package com.contentdb.library_service.dto.user_list_content;

import com.contentdb.library_service.client.dto.general.RecommendationResponse;
import com.contentdb.library_service.client.dto.general.TranslationsResponse;
import com.contentdb.library_service.client.dto.series.TvAggregateCreditsResponse;
import com.contentdb.library_service.client.dto.series.TvDetailResponse;
import com.contentdb.library_service.client.dto.series.TvLatestCreditsResponse;

import java.util.List;

public class TvPageDto {

    private TvDetailResponse detail;
    private TvAggregateCreditsResponse aggregateCredits;
    private TvLatestCreditsResponse latestCredits;
    private List<RecommendationResponse> recommendations;
    private List<TranslationsResponse> translations;

    public TvPageDto() {
    }

    public TvPageDto(TvDetailResponse detail, TvAggregateCreditsResponse aggregateCredits, TvLatestCreditsResponse latestCredits, List<RecommendationResponse> recommendations, List<TranslationsResponse> translations) {
        this.detail = detail;
        this.aggregateCredits = aggregateCredits;
        this.latestCredits = latestCredits;
        this.recommendations = recommendations;
        this.translations = translations;
    }

    public TvDetailResponse getDetail() {
        return detail;
    }

    public TvAggregateCreditsResponse getAggregateCredits() {
        return aggregateCredits;
    }

    public TvLatestCreditsResponse getLatestCredits() {
        return latestCredits;
    }

    public List<RecommendationResponse> getRecommendations() {
        return recommendations;
    }

    public List<TranslationsResponse> getTranslations() {
        return translations;
    }
}
