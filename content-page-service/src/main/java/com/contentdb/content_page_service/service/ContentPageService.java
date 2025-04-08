package com.contentdb.content_page_service.service;

import com.contentdb.content_page_service.client.comment.CommentServiceClient;
import com.contentdb.content_page_service.client.content.ContentServiceClient;
import com.contentdb.content_page_service.client.content.dto.general.RecommendationResponse;
import com.contentdb.content_page_service.client.content.dto.general.TranslationsResponse;
import com.contentdb.content_page_service.client.content.dto.movie.MovieCreditsResponse;
import com.contentdb.content_page_service.client.content.dto.movie.MovieDetailResponse;
import com.contentdb.content_page_service.client.content.dto.series.TvAggregateCreditsResponse;
import com.contentdb.content_page_service.client.content.dto.series.TvDetailResponse;
import com.contentdb.content_page_service.client.content.dto.series.TvLatestCreditsResponse;
import com.contentdb.content_page_service.component.ContentPageValidator;
import com.contentdb.content_page_service.dto.MoviePageDto;
import com.contentdb.content_page_service.dto.TvPageDto;
import com.contentdb.content_page_service.exception.ContentNotFoundException;
import com.contentdb.content_page_service.exception.PageNotCompleteException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ContentPageService {

    private final ContentServiceClient contentServiceClient;
    private final ContentPageValidator validator;
    private final CommentServiceClient commentServiceClient;

    private final ExecutorService executorService = Executors.newFixedThreadPool(
            Math.max(16, Runtime.getRuntime().availableProcessors() * 4));



    public ContentPageService(ContentServiceClient contentServiceClient, ContentPageValidator validator, CommentServiceClient commentServiceClient) {
        this.contentServiceClient = contentServiceClient;
        this.validator = validator;
        this.commentServiceClient = commentServiceClient;
    }


    public Object getContentPage(String contentId) {
        String type = validator.getMediaTypeByContentId(contentId);

        if (type.equals("movie")) {
            return getMoviePage(contentId);
        } else if (type.equals("tv")) {
            return getTvPage(contentId);
        }

        throw new ContentNotFoundException("Aranan içerik bulunamadı.");

    }


    public TvPageDto getTvPage(String tvId) {

        try {
            CompletableFuture<TvDetailResponse> detailFuture = CompletableFuture.supplyAsync(() ->
                    contentServiceClient.getTvDetail(tvId).getBody(), executorService);

            CompletableFuture<TvAggregateCreditsResponse> aggregateCreditsFuture = CompletableFuture.supplyAsync(() ->
                    contentServiceClient.getTvAggregateCredits(tvId).getBody(), executorService);

            CompletableFuture<TvLatestCreditsResponse> latestCreditsFuture = CompletableFuture.supplyAsync(() ->
                    contentServiceClient.getTvLatestCredits(tvId).getBody(), executorService);

            CompletableFuture<List<RecommendationResponse>> recommendationFuture = CompletableFuture.supplyAsync(() ->
                    contentServiceClient.getTvRecommendation(tvId).getBody(), executorService);

            CompletableFuture<List<TranslationsResponse>> translationFuture = CompletableFuture.supplyAsync(() ->
                    contentServiceClient.getTvTranslations(tvId).getBody(), executorService);

            CompletableFuture<Map<String, Object>> commentFuture = CompletableFuture.supplyAsync(()->
                    commentServiceClient.getComment(tvId).getBody(),executorService);

            CompletableFuture.allOf(detailFuture,
                            aggregateCreditsFuture,
                            latestCreditsFuture,
                            recommendationFuture,
                            translationFuture,
                            commentFuture)
                    .join();

            return new TvPageDto(
                    detailFuture.get(),
                    aggregateCreditsFuture.get(),
                    latestCreditsFuture.get(),
                    recommendationFuture.get(),
                    translationFuture.get(),
                    commentFuture.get()
            );
        } catch (Exception e) {
            throw new PageNotCompleteException("Sayfa getirilemedi: " + e.getMessage());
        }

    }


    public MoviePageDto getMoviePage(String movieId) {

        try {

            CompletableFuture<MovieDetailResponse> detailFuture = CompletableFuture.supplyAsync(() ->
                    contentServiceClient.getMovieDetail(movieId).getBody(), executorService);

            CompletableFuture<MovieCreditsResponse> creditsFuture = CompletableFuture.supplyAsync(() ->
                    contentServiceClient.getMovieCredits(movieId).getBody(), executorService);

            CompletableFuture<List<RecommendationResponse>> recommendationFuture = CompletableFuture.supplyAsync(() ->
                    contentServiceClient.getMovieRecommendation(movieId).getBody(), executorService);

            CompletableFuture<List<TranslationsResponse>> translationsFuture = CompletableFuture.supplyAsync(() ->
                    contentServiceClient.getMovieTranslations(movieId).getBody(), executorService);

            CompletableFuture<Map<String,Object>> commentFuture = CompletableFuture.supplyAsync(()->
                    commentServiceClient.getComment(movieId).getBody(),executorService);


            CompletableFuture.allOf(detailFuture,
                    creditsFuture,
                    recommendationFuture,
                    translationsFuture,
                    commentFuture).join();

            return new MoviePageDto(
                    detailFuture.get(),
                    creditsFuture.get(),
                    recommendationFuture.get(),
                    translationsFuture.get(),
                    commentFuture.get()
            );
        } catch (Exception e) {
            throw new PageNotCompleteException("Sayfa getirilemedi: " + e.getMessage());
        }


    }

}
