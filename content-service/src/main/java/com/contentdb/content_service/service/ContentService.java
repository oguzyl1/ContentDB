package com.contentdb.content_service.service;

import com.contentdb.content_service.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class ContentService {

    @Value("${omdb.api.key}")
    private String apiKey;

    private final WebClient webClient;

    public ContentService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://www.omdbapi.com/").build();
    }

    public ImdbIDRequest getImdbIDByTitle(String title) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("t", title)
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(ImdbIDRequest.class)
                .block(); // Mono'yu bloklayarak senkron hale getiriyoruz
    }

    public PosterRequest getPosterByImdbID(String imdbID) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("i", imdbID)
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(PosterRequest.class)
                .block();
    }

    public DetailsRequest getDetailsByImdbID(String imdbID) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("i", imdbID)
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(DetailsRequest.class)
                .block();
    }

    public List<SearchRequest> searchContentsByTitle(String title) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("s", title)
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(SearchResponse.class)
                .map(SearchResponse::getSearch)
                .block();
    }

}
