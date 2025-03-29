package com.contentdb.content_service.controller;

import com.contentdb.content_service.client.dto.general.RecommendationResponse;
import com.contentdb.content_service.client.dto.general.TmdbMultiSearchResponse;
import com.contentdb.content_service.client.dto.general.TranslationsResponse;
import com.contentdb.content_service.client.dto.movie.MovieCreditsResponse;
import com.contentdb.content_service.client.dto.movie.MovieDetailResponse;
import com.contentdb.content_service.client.dto.movie.MoviePageResponse;
import com.contentdb.content_service.client.dto.series.TvAggregateCreditsResponse;
import com.contentdb.content_service.client.dto.series.TvDetailResponse;
import com.contentdb.content_service.client.dto.series.TvLatestCreditsResponse;
import com.contentdb.content_service.service.TmdbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/content/tmdb")
public class TmdbController {

    private static final Logger logger = LoggerFactory.getLogger(TmdbController.class);
    private final TmdbService tmdbService;

    public TmdbController(TmdbService tmdbService) {
        this.tmdbService = tmdbService;
    }

    /**
     * Arama sorgusuna göre film, dizi veya belgesel arayan endpoint.
     *
     * @param query Kullanıcının aradığı içerik
     * @return Arama sonucundaki içerikler
     */
    @GetMapping("/search")
    public ResponseEntity<List<TmdbMultiSearchResponse>> searchMulti(@RequestParam String query) {
        logger.info("GET /v1/content/tmdb/search - query={}", query);
        List<TmdbMultiSearchResponse> results = tmdbService.searchMulti(query);
        return ResponseEntity.ok(results);
    }

    /**
     * Belirtilen film ID'sine göre film detaylarını döndüren endpoint.
     *
     * @param movieId Filmin benzersiz ID'si
     * @return Film detayları
     */
    @GetMapping("/movie/detail")
    public ResponseEntity<MovieDetailResponse> getMovieDetail(@RequestParam String movieId) {
        logger.info("GET /v1/content/tmdb/movie/detail - movieId={}", movieId);
        MovieDetailResponse response = tmdbService.getMovieDetail(movieId);
        return ResponseEntity.ok(response);
    }

    /**
     * Belirtilen film ID'sine göre oyuncu ve ekip bilgilerini döndüren endpoint.
     *
     * @param movieId Filmin benzersiz ID'si
     * @return Oyuncu ve ekip bilgileri
     */
    @GetMapping("/movie/credits")
    public ResponseEntity<MovieCreditsResponse> getMovieCredits(@RequestParam String movieId) {
        logger.info("GET /v1/content/tmdb/movie/credits - movieId={}", movieId);
        MovieCreditsResponse response = tmdbService.getMovieCredits(movieId);
        return ResponseEntity.ok(response);
    }

    /**
     * Belirtilen film ID'sine göre önerilen filmleri döndüren endpoint.
     *
     * @param movieId Filmin benzersiz ID'si
     * @return Önerilen filmler listesi
     */
    @GetMapping("/movie/recommendation")
    public ResponseEntity<List<RecommendationResponse>> getMovieRecommendation(@RequestParam String movieId) {
        logger.info("GET /v1/content/tmdb/movie/recommendation - movieId={}", movieId);
        List<RecommendationResponse> recommendations = tmdbService.getMovieRecommendation(movieId);
        return ResponseEntity.ok(recommendations);
    }

    /**
     * Belirtilen film ID'sine göre çeviri bilgilerini döndüren endpoint.
     *
     * @param movieId Filmin benzersiz ID'si
     * @return Film çeviri bilgileri
     */
    @GetMapping("/movie/translations")
    public ResponseEntity<List<TranslationsResponse>> getMovieTranslations(@RequestParam String movieId) {
        logger.info("GET /v1/content/tmdb/movie/translations - movieId={}", movieId);
        List<TranslationsResponse> translations = tmdbService.getMovieTranslations(movieId);
        return ResponseEntity.ok(translations);
    }

    @GetMapping("/movie/page")
    public ResponseEntity<MoviePageResponse> getMoviePage(@RequestParam String movieId) {
        logger.info("GET /v1/content/tmdb/movie/page - movieId={}", movieId);
        MoviePageResponse response = tmdbService.getMoviePage(movieId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tv/detail")
    public ResponseEntity<TvDetailResponse> getTvDetail(@RequestParam String tvId) {
        logger.info("GET /v1/content/tmdb/tv/detail - tvId={}", tvId);
        TvDetailResponse response = tmdbService.getTvDetail(tvId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tv/credits/aggregate")
    public ResponseEntity<TvAggregateCreditsResponse> getTvAggregateCredits(@RequestParam String tvId) {
        logger.info("GET /v1/content/tmdb/tv/credits/aggregate - tvId={}", tvId);
        TvAggregateCreditsResponse response = tmdbService.getTvAggregateCredits(tvId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tv/credits/latest")
    public ResponseEntity<TvLatestCreditsResponse> getTvLatestCredits(@RequestParam String tvId) {
        logger.info("GET /v1/content/tmdb/tv/credits/latest - tvId={}", tvId);
        TvLatestCreditsResponse response = tmdbService.getLatestCredits(tvId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tv/recommendation")
    public ResponseEntity<List<RecommendationResponse>> getTvRecommendation(@RequestParam String tvId) {
        logger.info("GET /v1/content/tmdb/tv/recommendation - tvId={}", tvId);
        List<RecommendationResponse> responses = tmdbService.getTvRecommendations(tvId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/tv/translations")
    public ResponseEntity<List<TranslationsResponse>> getTvTranslations(@RequestParam String tvId) {
        logger.info("GET /v1/content/tmdb/tv/translations - tvId={}", tvId);
        List<TranslationsResponse> responses = tmdbService.getTvTranslations(tvId);
        return ResponseEntity.ok(responses);
    }

}
