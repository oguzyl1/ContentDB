package com.contentdb.content_service.client.tmdb;

import com.contentdb.content_service.client.dto.general.TmdbMultiSearchResult;
import com.contentdb.content_service.client.dto.movie.MovieCreditsResponse;
import com.contentdb.content_service.client.dto.movie.MovieDetailResponse;
import com.contentdb.content_service.client.dto.movie.MovieRecommendationResult;
import com.contentdb.content_service.client.dto.movie.MovieTranslationsResult;
import com.contentdb.content_service.client.dto.series.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Feign istemcisi, TMDB API'si ile iletişim kurarak film ve dizi verilerini almak için kullanılır.
 * Belirtilen API uç noktalarına HTTP GET istekleri yaparak ilgili bilgileri döner.
 */
@FeignClient(name = "tmdb-client", url = "${tmdb.api.url}")
public interface TmdbFeignClient {


    /**
     * Genel arama işlemi yapar. Film, dizi veya kişi bilgilerini aramak için kullanılır.
     *
     * @param apiKey   TMDB API anahtarı.
     * @param query    Aranacak kelime.
     * @param language Yanıtın döneceği dil (Varsayılan: Türkçe).
     * @return TMDB'den gelen arama sonuçları.
     */
    @GetMapping("/search/multi")
    TmdbMultiSearchResult searchMulti(@RequestParam("api_key") String apiKey,
                                      @RequestParam("query") String query,
                                      @RequestParam(value = "language", defaultValue = "tr-TR") String language);


    /**
     * Belirtilen film ID'sine göre film detaylarını getirir.
     *
     * @param movieId  Filmin TMDB üzerindeki ID'si.
     * @param apiKey   TMDB API anahtarı.
     * @param language Yanıtın döneceği dil (Varsayılan: Türkçe).
     * @return Filmin detay bilgilerini içeren yanıt.
     */
    @GetMapping("/movie/{movie_id}")
    MovieDetailResponse getMovieDetail(@PathVariable("movie_id") String movieId,
                                       @RequestParam("api_key") String apiKey,
                                       @RequestParam(value = "language", defaultValue = "tr-TR") String language);


    /**
     * Belirtilen film ID'sine göre oyuncu ve ekip bilgilerini getirir.
     *
     * @param movieId  Filmin TMDB üzerindeki ID'si.
     * @param apiKey   TMDB API anahtarı.
     * @param language Yanıtın döneceği dil (Varsayılan: Türkçe).
     * @return Filmin oyuncu ve ekip bilgilerini içeren yanıt.
     */
    @GetMapping("/movie/{movie_id}/credits")
    MovieCreditsResponse getMovieCredits(@PathVariable("movie_id") String movieId,
                                         @RequestParam("api_key") String apiKey,
                                         @RequestParam(value = "language", defaultValue = "tr-TR") String language);


    /**
     * Belirtilen film ID'sine göre TMDB tarafından önerilen filmleri getirir.
     *
     * @param movieId  Filmin TMDB üzerindeki ID'si.
     * @param apiKey   TMDB API anahtarı.
     * @param language Yanıtın döneceği dil (Varsayılan: İngilizce).
     * @return Önerilen filmler listesi.
     */
    @GetMapping("/movie/{movie_id}/recommendations")
    MovieRecommendationResult getMovieRecommendations(@PathVariable("movie_id") String movieId,
                                                      @RequestParam("api_key") String apiKey,
                                                      @RequestParam(value = "language", defaultValue = "en-US") String language
    );


    /**
     * Belirtilen film ID'sine göre mevcut çeviri bilgilerini getirir.
     *
     * @param movieId  Filmin TMDB üzerindeki ID'si.
     * @param apiKey   TMDB API anahtarı.
     * @param language Yanıtın döneceği dil (Varsayılan: İngilizce).
     * @return Filmin mevcut çeviri bilgilerini içeren yanıt.
     */
    @GetMapping("movie/{movie_id}/translations")
    MovieTranslationsResult getTheTranslationsForAMovie(@PathVariable("movie_id") String movieId,
                                                        @RequestParam("api_key") String apiKey,
                                                        @RequestParam(value = "language", defaultValue = "en-US") String language
    );


    /**
     * Belirtilen dizi ID'sine göre dizi detaylarını getirir.
     *
     * @param tvId     Dizinin TMDB üzerindeki ID'si.
     * @param apiKey   TMDB API anahtarı.
     * @param language Yanıtın döneceği dil (Varsayılan: Türkçe).
     * @return Dizinin detay bilgilerini içeren yanıt.
     */
    @GetMapping("/tv/{series_id}")
    TvDetailResponse getTvDetail(@PathVariable("series_id") String tvId,
                                 @RequestParam("api_key") String apiKey,
                                 @RequestParam(value = "language", defaultValue = "tr-TR") String language);


    @GetMapping("/tv/{series_id}/aggregate_credits")
    TvAggregateCreditsResponse getTvAggregateCredits(@PathVariable("series_id") String tvId,
                                                     @RequestParam("api_key") String apiKey,
                                                     @RequestParam(value = "language", defaultValue = "tr-TR") String language);


    @GetMapping("/tv/{series_id}/credits")
    TvLatestCreditsResponse getTvLatestCredits(@PathVariable("series_id") String tvId,
                                               @RequestParam("api_key") String apiKey,
                                               @RequestParam(value = "language", defaultValue = "tr-TR") String language);


    @GetMapping("/tv/{series_id}/recommendations")
    TvRecommendationResult getTvRecommendation(@PathVariable("series_id") String tvId,
                                               @RequestParam("api_key") String apiKey,
                                               @RequestParam(value = "language", defaultValue = "tr-TR") String language);


    @GetMapping("/tv/{series_id}/translations")
    TvTranslationsResult getTheTranslationsForATv(@PathVariable("series_id") String tvId,
                                                  @RequestParam("api_key") String apiKey,
                                                  @RequestParam(value = "language", defaultValue = "tr-TR") String language);



}
