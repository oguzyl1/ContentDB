package com.contentdb.library_service.component;

import com.contentdb.library_service.client.content.ContentServiceClient;
import com.contentdb.library_service.client.dto.movie.MovieDetailResponse;
import com.contentdb.library_service.client.dto.series.TvDetailResponse;
import com.contentdb.library_service.exception.ContentNotFoundException;
import com.contentdb.library_service.exception.ListNotFoundException;
import com.contentdb.library_service.exception.UserIdEmptyException;
import com.contentdb.library_service.model.UserList;
import com.contentdb.library_service.repository.UserListRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserListValidator {

    private final UserListRepository userListRepository;
    private final CacheManager cacheManager;
    private final ContentServiceClient contentServiceClient;

    public UserListValidator(UserListRepository userListRepository, CacheManager cacheManager, ContentServiceClient contentServiceClient) {
        this.userListRepository = userListRepository;
        this.cacheManager = cacheManager;
        this.contentServiceClient = contentServiceClient;
    }


    @Transactional(readOnly = true)
    public UserList getUserList(String listName, String userId) {
        return userListRepository.findByNameAndUserId(listName, userId)
                .orElseThrow(() -> new ListNotFoundException("Bu isme sahip liste bulunamadı"));
    }


    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public void userIdControl(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new UserIdEmptyException("Kullanıcı kimliği belirtilmemiş");
        }
    }

}
