package com.contentdb.library_service.service;

import com.contentdb.library_service.client.content.ContentServiceClient;
import com.contentdb.library_service.client.dto.general.RecommendationResponse;
import com.contentdb.library_service.client.dto.general.TmdbMultiSearchResponse;
import com.contentdb.library_service.client.dto.general.TranslationsResponse;
import com.contentdb.library_service.client.dto.movie.MovieCreditsResponse;
import com.contentdb.library_service.client.dto.movie.MovieDetailResponse;
import com.contentdb.library_service.client.dto.series.TvAggregateCreditsResponse;
import com.contentdb.library_service.client.dto.series.TvDetailResponse;
import com.contentdb.library_service.client.dto.series.TvLatestCreditsResponse;
import com.contentdb.library_service.dto.user_list_content.*;
import com.contentdb.library_service.exception.*;
import com.contentdb.library_service.model.UserList;
import com.contentdb.library_service.model.UserListContent;
import com.contentdb.library_service.repository.UserListContentRepository;
import com.contentdb.library_service.repository.UserListRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Validated
public class UserListContentService {

    private static final Logger logger = LoggerFactory.getLogger(UserListContentService.class);
    private final UserListContentRepository userListContentRepository;
    private final UserListRepository userListRepository;
    private final ContentServiceClient contentServiceClient;
    private final ExecutorService executorService = Executors.newFixedThreadPool(
            Math.max(16, Runtime.getRuntime().availableProcessors() * 4));
    private final CacheManager cacheManager;


    public UserListContentService(UserListContentRepository userListContentRepository, UserListRepository userListRepository, ContentServiceClient contentServiceClient, CacheManager cacheManager) {
        this.userListContentRepository = userListContentRepository;
        this.userListRepository = userListRepository;
        this.contentServiceClient = contentServiceClient;
        this.cacheManager = cacheManager;
    }


    @Transactional(readOnly = true)
    public List<TmdbMultiSearchResponse> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new SearchCannotBeEmptyException("Arama sorgusu boş olamaz.");
        }

        ResponseEntity<List<TmdbMultiSearchResponse>> response = contentServiceClient.searchMulti(query);

        return response.getBody() != null ? response.getBody() : Collections.emptyList();
    }


    @Transactional
    public UserListContentDto addContentToUserList(String contentId, String listName, String userId) {
        try {
            UserList userList = getUserList(listName, userId);

            if (userListContentRepository.existsByUserListAndContentId(userList, contentId)) {
                throw new ContentAlreadyExistsException("Bu içerik zaten listenizde mevcut");
            }

            String type = getMediaTypeByContentId(contentId);
            if (type.isEmpty()) {
                throw new RuntimeException("Medya türü belirlenemedi.");
            }

            Integer currentCount = userListContentRepository.countByUserList(userList);
            Integer newOrderNumber = (currentCount != null ? currentCount : 0) + 1;

            UserListContent listContent = UserListContent.builder(userList, contentId)
                    .orderNumber(newOrderNumber)
                    .createdBy(userId)
                    .mediaType(type)
                    .build();

            UserListContent savedList = userListContentRepository.save(listContent);

            userList.increaseContentCount();
            userListRepository.save(userList);

            return UserListContentDto.convertToUserListContentDto(savedList);

        } catch (ListNotFoundException e) {
            logger.warn("Kullanıcı listesi bulunamadı: listName={}, userId={}", listName, userId);
            throw e;
        } catch (ContentAlreadyExistsException e) {
            logger.warn("İçerik zaten mevcut: contentId={}, listName={}, userId={}", contentId, listName, userId);
            throw e;
        } catch (Exception e) {
            logger.error("İçerik eklenirken beklenmedik bir hata oluştu: ", e);
            throw new RuntimeException("İçerik eklenirken beklenmedik bir hata oluştu.");
        }
    }

    @Transactional
    public void deleteContentFromUserList(String contentId, String listName, String userId) {

        UserList userList = getUserList(listName, userId);

        UserListContent content = userListContentRepository.findByUserListAndContentId(userList, contentId)
                .orElseThrow(() -> new ContentNotFoundException("Bu içerik kütüphanenizde bulunamadı"));

        userListContentRepository.delete(content);

        List<UserListContent> contentsToUpdate = userListContentRepository.findByUserListAndOrderNumberGreaterThan(userList, content.getOrderNumber());

        userList.decreaseContentCount();
        userListRepository.save(userList);

        for (UserListContent c : contentsToUpdate) {
            c.setOrderNumber(c.getOrderNumber() - 1);
        }

        userListContentRepository.saveAll(contentsToUpdate);
    }


    @Transactional(readOnly = true)
    public UserListContentIdDto getAllContentsFromOneUserList(String listName, String userId) {

        UserList userList = getUserList(listName, userId);

        List<UserListContent> lists = userListContentRepository.findByUserListId(userList.getId());

        List<String> contentIds = lists.stream()
                .map(UserListContent::getContentId)
                .collect(Collectors.toList());

        return new UserListContentIdDto(userList.getId(), userList.getName(), contentIds);
    }


    @Transactional(readOnly = true)
    public ListCardDto getTvCard(String tvId) {
        ResponseEntity<TvDetailResponse> response = contentServiceClient.getTvDetail(tvId);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ContentNotFoundException("Dizi detayları alınamadı. TV ID: " + tvId);
        }

        TvDetailResponse tvDetail = response.getBody();
        if (tvDetail == null) {
            throw new ContentNotFoundException("TV içeriği bulunamadı. TV ID: " + tvId);
        }

        String id = String.valueOf(tvDetail.getId());
        String name = tvDetail.getTitle();
        String overview = tvDetail.getOverview();
        String image = tvDetail.getPosterPath();

        return new ListCardDto(id, name, overview, image);
    }

    @Transactional(readOnly = true)
    public ListCardDto getMovieCard(String movieId) {
        ResponseEntity<MovieDetailResponse> response = contentServiceClient.getMovieDetail(movieId);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ContentNotFoundException("Film detayları alınamadı. film ID: " + movieId);
        }
        MovieDetailResponse movieDetail = response.getBody();
        if (movieDetail == null) {
            throw new ContentNotFoundException("Film içeriği bulunamadı. film ID: " + movieId);
        }

        String id = String.valueOf(movieDetail.getId());
        String name = movieDetail.getTitle();
        String overview = movieDetail.getOverview();
        String image = movieDetail.getPosterPath();

        return new ListCardDto(id, name, overview, image);
    }

    @Transactional(readOnly = true)
    public Object getContentCard(String contentId) {
        String type = getMediaTypeByContentId(contentId);

        if (type.equals("movie")) {
            return getMovieCard(contentId);
        }
        if (type.equals("tv")) {
            return getTvCard(contentId);
        }
        throw new ContentNotFoundException("Aranan içerik bulunamadı.");
    }

    @Transactional(readOnly = true)
    public List<Object> getContentCardsFromUserList(String listName, String userId) {
        UserListContentIdDto dto = getAllContentsFromOneUserList(listName, userId);
        List<String> contentIds = dto.getContentIds();

        return contentIds.stream()
                .map(
                        contentId -> {

                            try {
                                return getContentCard(contentId);
                            } catch (ContentNotFoundException e) {
                                return null;
                            }
                        }

                )
                .filter(Objects::nonNull)
                .toList();
    }


    @Transactional(readOnly = true)
    public Object getContentPage(String contentId) {
        String type = getMediaTypeByContentId(contentId);

        if (type.equals("movie")) {
            return getMoviePage(contentId);
        } else if (type.equals("tv")) {
            return getTvPage(contentId);
        }

        throw new ContentNotFoundException("Aranan içerik bulunamadı.");

    }


    @Transactional(readOnly = true)
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

            CompletableFuture.allOf(detailFuture,
                            aggregateCreditsFuture,
                            latestCreditsFuture,
                            recommendationFuture,
                            translationFuture)
                    .join();

            return new TvPageDto(
                    detailFuture.get(),
                    aggregateCreditsFuture.get(),
                    latestCreditsFuture.get(),
                    recommendationFuture.get(),
                    translationFuture.get()
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


            CompletableFuture.allOf(detailFuture,
                    creditsFuture,
                    recommendationFuture,
                    translationsFuture).join();

            return new MoviePageDto(
                    detailFuture.get(),
                    creditsFuture.get(),
                    recommendationFuture.get(),
                    translationsFuture.get()
            );
        } catch (Exception e) {
            throw new PageNotCompleteException("Sayfa getirilemedi: " + e.getMessage());
        }


    }


    private UserList getUserList(String listName, String userId) {
        return userListRepository.findByNameAndUserId(listName, userId)
                .orElseThrow(() -> new ListNotFoundException("Bu isme sahip liste bulunamadı"));
    }


    private String getMediaTypeByContentId(String contentId) {

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
