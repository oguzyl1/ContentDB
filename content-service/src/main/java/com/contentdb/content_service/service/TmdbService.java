package com.contentdb.content_service.service;

import com.contentdb.content_service.client.dto.general.RecommendationResponse;
import com.contentdb.content_service.client.dto.general.TmdbMultiSearchResponse;
import com.contentdb.content_service.client.dto.general.TmdbMultiSearchResult;
import com.contentdb.content_service.client.dto.general.TranslationsResponse;
import com.contentdb.content_service.client.dto.movie.*;
import com.contentdb.content_service.client.dto.series.*;
import com.contentdb.content_service.client.tmdb.TmdbFeignClient;
import com.contentdb.content_service.exception.ResponseEmptyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TmdbService {

    @Value("${tmdb.api.key}")
    private String tmdbApiKey;
    private static final Logger logger = LoggerFactory.getLogger(TmdbService.class);
    private final TmdbFeignClient tmdbFeignClient;

    public TmdbService(TmdbFeignClient tmdbFeignClient) {
        this.tmdbFeignClient = tmdbFeignClient;
    }


    /**
     * Arama butonuna yazılan sorguya göre film, dizi veya belgesel içeriğini getiren metod.
     *
     * @param query Kullanıcının aradığı içerik ismi
     * @return Arama sonucunda bulunan içeriklerin listesi
     */

    @Cacheable(value = "content-searchMultiCache", key = "#query")
    public List<TmdbMultiSearchResponse> searchMulti(String query) {
        logger.info("TMDB API'ye istek yapılıyor: /searchMulti, query={}", query);
        TmdbMultiSearchResult response = tmdbFeignClient.searchMulti(tmdbApiKey, query, "tr-TR");
        return validateResponse(response, "Arama sorgusu: " + query).getResults();
    }

    /**
     * Verilen ID'ye göre film detaylarını getiren metod.
     *
     * @param movieId Filmin benzersiz ID değeri
     * @return Film detaylarını içeren yanıt
     */

    @Cacheable(value = "content-movieDetailCache", key = "#movieId")
    public MovieDetailResponse getMovieDetail(String movieId) {
        logger.info("TMDB API'ye istek yapılıyor: /getMovieDetail, Film ID={}", movieId);
        return validateResponse(tmdbFeignClient.getMovieDetail(movieId, tmdbApiKey, "tr-TR"), "Film ID: " + movieId);
    }

    /**
     * Verilen ID'ye göre bir filmin oyuncu ve ekip bilgilerini getiren metod.
     *
     * @param movieId Filmin benzersiz ID değeri
     * @return Filmin oyuncu ve ekip bilgilerini içeren yanıt
     */

    @Cacheable(value = "content-movieCreditsCache", key = "#movieId")
    public MovieCreditsResponse getMovieCredits(String movieId) {
        logger.info("TMDB API'ye istek yapılıyor: /getMovieCredits, Film ID={}", movieId);
        return validateResponse(tmdbFeignClient.getMovieCredits(movieId, tmdbApiKey, "tr-TR"), "Film ID: " + movieId);
    }

    /**
     * Verilen ID'ye göre bir filme benzer önerilen filmleri getiren metod.
     *
     * @param movieId Filmin benzersiz ID değeri
     * @return Önerilen filmler listesi
     */
    @Cacheable(value = "content-movieRecommendation" , key = "#movieId")
    public List<RecommendationResponse> getMovieRecommendation(String movieId) {
        logger.info("TMDB API'ye istek yapılıyor: /getMovieRecommendation, Film ID={}", movieId);
        MovieRecommendationResult response = tmdbFeignClient.getMovieRecommendations(movieId, tmdbApiKey, "tr-TR");
        return validateResponse(response, "Film ID: " + movieId).getResults();
    }


    /**
     * Verilen ID'ye göre bir filmin çeviri bilgilerini getiren metod.
     *
     * @param movieId Filmin benzersiz ID değeri
     * @return Filmin mevcut çeviri dillerini içeren liste
     */
    @Cacheable(value = "content-movieTranslations" , key = "#movieId")
    public List<TranslationsResponse> getMovieTranslations(String movieId) {
        logger.info("TMDB API'ye istek yapılıyor: /getMovieTranslations, Film ID={}", movieId);
        MovieTranslationsResult response = tmdbFeignClient.getTheTranslationsForAMovie(movieId, tmdbApiKey, "tr-TR");
        return validateResponse(response, "Film ID: " + movieId).getResults();
    }


    @Cacheable(value = "content-moviePage" , key = "#movieId")
    public MoviePageResponse getMoviePage(String movieId) {
        logger.info("TMDB API'ye istek yapılıyor: /getMoviePage, Film ID={}", movieId);

        // API’den gelen ham cevabı alalım
        MoviePageResponse response = tmdbFeignClient.getMoviePage(movieId, tmdbApiKey, "tr-TR", "credits,recommendations,translations");

        // API'den dönen yanıtı logla
        logger.info("TMDB API yanıtı: {}", response);

        // Gelen yanıtı doğrula
        return validateResponse(response, "Film ID: " + movieId);
    }

    @Cacheable(value = "content-tvDetail" , key = "#tvId")
    public TvDetailResponse getTvDetail(String tvId) {
        logger.info("TMDB API'ye istek yapılıyor: /getTvDetail, Dizi ID={}", tvId);
        return validateResponse(tmdbFeignClient.getTvDetail(tvId, tmdbApiKey, "tr-TR"), "Dizi ID: " + tvId);
    }


    @Cacheable(value = "content-tvAggregateCredits" , key = "#tvId")
    public TvAggregateCreditsResponse getTvAggregateCredits(String tvId) {
        logger.info("TMDB API'ye istek yapılıyor: /getAggregateCredits, Dizi ID={}", tvId);
        return validateResponse(tmdbFeignClient.getTvAggregateCredits(tvId, tmdbApiKey, "tr-TR"), "Dizi ID: " + tvId);
    }

    @Cacheable(value = "content-tvLatestCredits" , key = "#tvId")
    public TvLatestCreditsResponse getLatestCredits(String tvId) {
        logger.info("TMDB API'ye istek yapılıyor: /getLatestCredits, Dizi ID={}", tvId);
        return validateResponse(tmdbFeignClient.getTvLatestCredits(tvId, tmdbApiKey, "tr-TR"), "Dizi ID: " + tvId);
    }

    @Cacheable(value = "content-tvRecommendations" , key = "#tvId")
    public List<RecommendationResponse> getTvRecommendations(String tvId) {
        logger.info("TMDB API'ye istek yapılıyor: /getTvRecommendations, Dizi ID={}", tvId);
        TvRecommendationResult response = tmdbFeignClient.getTvRecommendation(tvId, tmdbApiKey, "tr-TR");
        return validateResponse(response, "Film ID: " + tvId).getResults();
    }

    @Cacheable(value = "content-tvTranslations" , key = "#tvId")
    public List<TranslationsResponse> getTvTranslations(String tvId) {
        logger.info("TMDB API'ye istek yapılıyor: /getTvTranslations, Dizi ID={}", tvId);
        TvTranslationsResult response = tmdbFeignClient.getTheTranslationsForATv(tvId, tmdbApiKey, "tr-TR");
        return validateResponse(response, "Dizi ID: " + tvId).getResults();
    }



    /**
     * TMDB API'den gelen yanıtın boş olup olmadığını kontrol eden yardımcı metot.
     * Eğer yanıt boşsa hata fırlatır, değilse yanıtı döndürür.
     *
     * @param response API'den gelen yanıt
     * @param message  Log mesajı için ek bilgi
     * @param <T>      Dönen yanıtın tipi
     * @return Geçerli yanıt, eğer boşsa hata fırlatılır.
     */
    private <T> T validateResponse(T response, String message) {
        if (response == null) {
            logger.warn("TMDB API'den boş yanıt geldi. {}", message);
            throw new ResponseEmptyException();
        }
        logger.info("TMDB API başarılı yanıt döndü: {}", response);
        return response;
    }

}
