package com.contentdb.library_service.service;

import com.contentdb.library_service.client.content.ContentServiceClient;
import com.contentdb.library_service.client.dto.general.TmdbMultiSearchResponse;
import com.contentdb.library_service.client.dto.movie.MovieDetailResponse;
import com.contentdb.library_service.client.dto.series.TvDetailResponse;
import com.contentdb.library_service.component.UserListValidator;
import com.contentdb.library_service.dto.user_list_content.ListCardDto;
import com.contentdb.library_service.dto.user_list_content.UserListContentDto;
import com.contentdb.library_service.dto.user_list_content.UserListContentIdDto;
import com.contentdb.library_service.exception.ContentAlreadyExistsException;
import com.contentdb.library_service.exception.ContentNotFoundException;
import com.contentdb.library_service.exception.ListNotFoundException;
import com.contentdb.library_service.exception.SearchCannotBeEmptyException;
import com.contentdb.library_service.model.UserList;
import com.contentdb.library_service.model.UserListContent;
import com.contentdb.library_service.repository.UserListContentRepository;
import com.contentdb.library_service.repository.UserListRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Validated
public class UserListContentService {

    private static final Logger logger = LoggerFactory.getLogger(UserListContentService.class);
    private final UserListContentRepository userListContentRepository;
    private final UserListRepository userListRepository;
    private final ContentServiceClient contentServiceClient;
    private final UserListValidator validator;


    public UserListContentService(UserListContentRepository userListContentRepository, UserListRepository userListRepository, ContentServiceClient contentServiceClient, UserListValidator validator) {
        this.userListContentRepository = userListContentRepository;
        this.userListRepository = userListRepository;
        this.contentServiceClient = contentServiceClient;
        this.validator = validator;
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
            UserList userList = validator.getUserList(listName, userId);

            if (userListContentRepository.existsByUserListAndContentId(userList, contentId)) {
                throw new ContentAlreadyExistsException("Bu içerik zaten listenizde mevcut");
            }

            String type = validator.getMediaTypeByContentId(contentId);
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

        UserList userList = validator.getUserList(listName, userId);

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

        UserList userList = validator.getUserList(listName, userId);

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
        String type = validator.getMediaTypeByContentId(contentId);

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

}
