package com.contentdb.content_page_service.dto;


import com.contentdb.content_page_service.client.content.dto.general.RecommendationResponse;
import com.contentdb.content_page_service.client.content.dto.general.TranslationsResponse;
import com.contentdb.content_page_service.client.content.dto.series.TvAggregateCreditsResponse;
import com.contentdb.content_page_service.client.content.dto.series.TvDetailResponse;
import com.contentdb.content_page_service.client.content.dto.series.TvLatestCreditsResponse;

import java.util.List;
import java.util.Map;

public class TvPageDto {

    private TvDetailResponse detail;
    private TvAggregateCreditsResponse aggregateCredits;
    private TvLatestCreditsResponse latestCredits;
    private List<RecommendationResponse> recommendations;
    private List<TranslationsResponse> translations;
    private Map<String, Object> comment;

    public TvPageDto() {
    }

    public TvPageDto(TvDetailResponse detail, TvAggregateCreditsResponse aggregateCredits, TvLatestCreditsResponse latestCredits, List<RecommendationResponse> recommendations, List<TranslationsResponse> translations, Map<String, Object> comment) {
        this.detail = detail;
        this.aggregateCredits = aggregateCredits;
        this.latestCredits = latestCredits;
        this.recommendations = recommendations;
        this.translations = translations;
        this.comment = comment;
    }

    public Map<String, Object> getComment() {
        return comment;
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
