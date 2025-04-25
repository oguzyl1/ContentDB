package com.contentdb.library_service.component;

import com.contentdb.library_service.client.content.ContentServiceClient;
import com.contentdb.library_service.client.dto.movie.MovieDetailResponse;
import com.contentdb.library_service.client.dto.series.TvDetailResponse;
import com.contentdb.library_service.exception.ContentNotFoundException;
import com.contentdb.library_service.exception.ListNotFoundException;
import com.contentdb.library_service.exception.UserIdEmptyException;
import com.contentdb.library_service.model.UserList;
import com.contentdb.library_service.repository.UserListRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserListValidator {

    private final UserListRepository userListRepository;
    private final ContentServiceClient contentServiceClient;

    public UserListValidator(UserListRepository userListRepository, ContentServiceClient contentServiceClient) {
        this.userListRepository = userListRepository;
        this.contentServiceClient = contentServiceClient;
    }


    @Transactional(readOnly = true)
    @Cacheable(value = "user-lists", key = "#listName + '::' + #userId")
    public UserList getUserList(String listName, String userId) {
        return userListRepository.findByNameAndUserId(listName, userId)
                .orElseThrow(() -> new ListNotFoundException("Bu isme sahip liste bulunamadı"));
    }



    @Transactional(readOnly = true)
    public void userIdControl(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new UserIdEmptyException("Kullanıcı kimliği belirtilmemiş");
        }
    }

}
