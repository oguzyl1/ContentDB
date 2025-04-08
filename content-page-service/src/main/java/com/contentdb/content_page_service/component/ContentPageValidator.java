package com.contentdb.content_page_service.component;


import com.contentdb.content_page_service.client.content.ContentServiceClient;
import com.contentdb.content_page_service.client.content.dto.movie.MovieDetailResponse;
import com.contentdb.content_page_service.client.content.dto.series.TvDetailResponse;
import com.contentdb.content_page_service.exception.ContentNotFoundException;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ContentPageValidator {

    private final CacheManager cacheManager;
    private final ContentServiceClient contentServiceClient;

    public ContentPageValidator(CacheManager cacheManager, ContentServiceClient contentServiceClient) {
        this.cacheManager = cacheManager;
        this.contentServiceClient = contentServiceClient;
    }

    public String getMediaTypeByContentId(String contentId) {

        Cache mediaTypeCache = cacheManager.getCache("mediaTypeCache");
        String cachedType = mediaTypeCache != null ? mediaTypeCache.get(contentId, String.class) : null;

        if (cachedType != null) {
            return cachedType;
        }

        try {
            ResponseEntity<MovieDetailResponse> movieResponse = contentServiceClient.getMovieDetail(contentId);
            if (movieResponse.getStatusCode().is2xxSuccessful()) {

                if (mediaTypeCache != null) {
                    mediaTypeCache.put(contentId, "movie");
                }

                return "movie";
            }
        } catch (Exception ignored) {
        }

        try {
            ResponseEntity<TvDetailResponse> tvResponse = contentServiceClient.getTvDetail(contentId);
            if (tvResponse.getStatusCode().is2xxSuccessful()) {

                if (mediaTypeCache != null) {
                    mediaTypeCache.put(contentId, "tv");
                }

                return "tv";
            }
        } catch (Exception ignored) {
        }
        throw new ContentNotFoundException("Aranan içerik bulunamadı.");
    }
}
