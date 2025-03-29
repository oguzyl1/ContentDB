package com.contentdb.library_service.client.content;

import com.contentdb.library_service.client.dto.general.RecommendationResponse;
import com.contentdb.library_service.client.dto.general.TmdbMultiSearchResponse;
import com.contentdb.library_service.client.dto.general.TranslationsResponse;
import com.contentdb.library_service.client.dto.movie.MovieCreditsResponse;
import com.contentdb.library_service.client.dto.movie.MovieDetailResponse;
import com.contentdb.library_service.client.dto.series.TvAggregateCreditsResponse;
import com.contentdb.library_service.client.dto.series.TvDetailResponse;
import com.contentdb.library_service.client.dto.series.TvLatestCreditsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "content-service", path = "/api/content/tmdb")
public interface ContentServiceClient {

    @GetMapping("/search")
    public ResponseEntity<List<TmdbMultiSearchResponse>> searchMulti(@RequestParam String query);

    @GetMapping("/movie/detail")
    public ResponseEntity<MovieDetailResponse> getMovieDetail(@RequestParam String movieId);

    @GetMapping("/movie/credits")
    public ResponseEntity<MovieCreditsResponse> getMovieCredits(@RequestParam String movieId);

    @GetMapping("/movie/recommendation")
    public ResponseEntity<List<RecommendationResponse>> getMovieRecommendation(@RequestParam String movieId);

    @GetMapping("/movie/translations")
    public ResponseEntity<List<TranslationsResponse>> getMovieTranslations(@RequestParam String movieId);

    @GetMapping("/tv/detail")
    public ResponseEntity<TvDetailResponse> getTvDetail(@RequestParam String tvId);

    @GetMapping("/tv/credits/aggregate")
    public ResponseEntity<TvAggregateCreditsResponse> getTvAggregateCredits(@RequestParam String tvId);

    @GetMapping("/tv/credits/latest")
    public ResponseEntity<TvLatestCreditsResponse> getTvLatestCredits(@RequestParam String tvId);

    @GetMapping("/tv/recommendation")
    public ResponseEntity<List<RecommendationResponse>> getTvRecommendation(@RequestParam String tvId);

    @GetMapping("/tv/translations")
    public ResponseEntity<List<TranslationsResponse>> getTvTranslations(@RequestParam String tvId);

}
