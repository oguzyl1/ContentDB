package com.contentdb.content_service.service;

import com.contentdb.content_service.client.ContentFeignClient;
import com.contentdb.content_service.model.DetailsRequest;
import com.contentdb.content_service.model.ImdbIDRequest;
import com.contentdb.content_service.model.PosterRequest;
import com.contentdb.content_service.model.SearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ContentService {

    @Value("${omdb.api.key}")
    private String apiKey;


    private final ContentFeignClient contentFeignClient;

    public ContentService(ContentFeignClient contentFeignClient) {
        this.contentFeignClient = contentFeignClient;
    }

    @Cacheable(value = "contentCache", key = "#title", unless = "#result == null")
    public ImdbIDRequest getImdbIDByTitle(String title) {
        return contentFeignClient.getImdbIDByTitle(title, apiKey);
    }

    @Cacheable(value = "posterCache", key = "#imdbID", unless = "#result == null")
    public PosterRequest getPosterByImdbID(String imdbID) {
        return contentFeignClient.getPosterByImdbID(imdbID, apiKey);
    }

    @Cacheable(value = "detailsCache", key = "#imdbID", unless = "#result == null")
    public DetailsRequest getDetailsByImdbID(String imdbID) {
        return contentFeignClient.getDetailsByImdbID(imdbID, apiKey);
    }

    @Cacheable(value = "searchCache", key = "#title", unless = "#result == null")
    public SearchResponse searchContents(String title) {
        return contentFeignClient.searchContents(title, apiKey);
    }

    @CacheEvict(value = "posterCache", key = "#imdbID")
    public void evictPosterCache(String imdbID) {
        System.out.println("Poster cache temizlendi: " + imdbID);
    }

    @CacheEvict(value = "detailsCache", key = "#imdbID")
    public void evictDetailsCache(String imdbID) {
        System.out.println("Detay cache temizlendi: " + imdbID);
    }

    @CacheEvict(value = "searchCache", key = "#title")
    public void evictSearchCache(String title) {
        System.out.println("Arama cache temizlendi: " + title);
    }

    // 🔥 Tüm cache'leri temizleme
    @CacheEvict(value = {"contentCache", "posterCache", "detailsCache", "searchCache"}, allEntries = true)
    public void evictAllCaches() {
        System.out.println("Tüm cache temizlendi.");
    }

}
