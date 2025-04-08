package com.contentdb.library_service.service;

import com.contentdb.library_service.component.UserListValidator;
import com.contentdb.library_service.dto.user_list.UserListDto;
import com.contentdb.library_service.exception.ListAlreadyExistException;
import com.contentdb.library_service.exception.ListNotFoundException;
import com.contentdb.library_service.model.UserList;
import com.contentdb.library_service.repository.UserListRepository;
import com.contentdb.library_service.request.CreateLibraryRequest;
import com.contentdb.library_service.request.UpdateLibraryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Validated
@Service
public class UserListService {

    private static final Logger logger = LoggerFactory.getLogger(UserListService.class);
    private final UserListRepository userListRepository;
    private final UserListValidator validator;

    public UserListService(UserListRepository userListRepository, UserListValidator validator) {
        this.userListRepository = userListRepository;
        this.validator = validator;
    }


    @Transactional
    public UserListDto createList(CreateLibraryRequest request, String userId) {

        logger.info("Liste Oluşturuluyor.");
        validator.userIdControl(userId);

        Optional<UserList> existingLibrary = userListRepository.findByNameAndUserId(request.name(), userId);
        logger.info("Listenin var olup olmadığı kontrol edildi. Sonuç: {}", existingLibrary.isPresent());

        if (existingLibrary.isPresent()) {
            logger.error("Liste zaten bulunmakta: {}", request.name());
            throw new ListAlreadyExistException("Bu isme sahip kütüphane zaten bulunmakta");
        }

        logger.info("Liste oluşturuluyor. Name: {}, Description: {}, UserId: {}",
                request.name(), request.description(), userId);

        UserList userList = UserList.builder(request.name())
                .description(request.description())
                .userId(userId)
                .isPublic(request.isPublic())
                .popularity(0)
                .build();

        return UserListDto.convertToLibraryDto(userListRepository.save(userList));
    }


    @Transactional
    public void setListVisibility(String listName, boolean isPublic, String userId) {

        logger.info("Listenin görünürlüğü değiştiriliyor: {} {}", listName, isPublic);

        validator.userIdControl(userId);

        UserList list = userListRepository.findByNameAndUserId(listName, userId)
                .orElseThrow(() -> new ListNotFoundException("Aranan liste bulunamadı."));

        list.setPublic(isPublic);

        logger.info("Listenin görünürlüğü set edildi.");
        userListRepository.save(list);
        logger.info("Liste görünürlüğü kaydedildi.");
    }


    @Transactional(readOnly = true)
    public List<UserListDto> getPublicLists(String userId) {

        logger.info("Kullanıcının public olan listleri getiriliyor.");
        validator.userIdControl(userId);

        List<UserList> lists = userListRepository.findByUserIdAndIsPublicTrue(userId);

        if (lists.isEmpty()) {
            return Collections.emptyList();
        }

        logger.info("Kullanıcının public listeleri getirildi.");
        return lists.stream()
                .map(UserListDto::convertToLibraryDto)
                .toList();
    }


    @Transactional(readOnly = true)
    public List<UserListDto> getAllLists(String userId) {

        logger.info("Kullanıcının tüm listeleri getiriliyor.");
        validator.userIdControl(userId);

        List<UserList> lists = userListRepository.findByUserId(userId);

        if (lists.isEmpty()) {
            return Collections.emptyList();
        }

        logger.info("Kullanıcının listeleri getirildi.");
        return lists.stream()
                .map(UserListDto::convertToLibraryDto)
                .toList();
    }


    @Transactional(readOnly = true)
    public UserListDto getListByName(String listName, String userId) {

        logger.info("Kulllanıcnın aranan listesi getiriliyor: {}", listName);

        validator.userIdControl(userId);

        UserList searchedUserList = userListRepository.findByNameAndUserId(listName, userId)
                .orElseThrow(() -> new ListNotFoundException("Bu isme sahip liste bulunamadı"));

        return UserListDto.convertToLibraryDto(searchedUserList);
    }


    @Transactional
    public UserListDto updateList(String current, UpdateLibraryRequest request, String userId) {

        logger.info("Kullanıcı listesini güncelliyor: {}", current);

        validator.userIdControl(userId);

        UserList userList = userListRepository.findByNameAndUserId(current, userId)
                .orElseThrow(() -> new ListNotFoundException("Güncelleme yapmak istediğiniz liste bulunamadı"));

        boolean needsSave = false;

        if (request.name() != null && !request.name().isBlank() && !request.name().equals(current)) {
            if (userListRepository.findByNameAndUserId(request.name(), userId).isPresent()) {
                throw new ListAlreadyExistException("Bu isimde bir kütüphane zaten mevcut");
            }
            userList.setLibraryName(request.name());
            needsSave = true;
        }

        if (request.description() != null && !request.description().isBlank() &&
                !request.description().equals(userList.getDescription())) {
            userList.setDescription(request.description());
            needsSave = true;
        }

        return needsSave ?
                UserListDto.convertToLibraryDto(userListRepository.save(userList)) :
                UserListDto.convertToLibraryDto(userList);
    }


    @Transactional
    public void deleteList(String listName, String userId) {

        logger.info("Kullanıcının listesi siliniyor: {}", listName);

        validator.userIdControl(userId);

        UserList userList = userListRepository.findByNameAndUserId(listName, userId)
                .orElseThrow(() -> new ListNotFoundException("Silinmek istenen liste bulunamadı"));

        userListRepository.delete(userList);

        logger.info("Kulalnıcı listesi silindi: {}", listName);
    }


    @Transactional
    public void increaseListPopularity(String listName) {
        logger.info("Listenin popülerliği artırılıyor: {}", listName);

        UserList list = userListRepository.findByName(listName)
                .orElseThrow(() -> new ListNotFoundException("Bu isimde bir liste bulunamadı"));

        list.setPopularity(list.getPopularity() + 1);
        userListRepository.save(list);
    }

    @Transactional(readOnly = true)
    public List<UserListDto> getMostPopularLists() {
        logger.info("En popüler listeler getiriliyor.");

        List<UserList> lists = userListRepository.findTop10ByIsPublicTrueOrderByPopularityDesc();
        return lists.stream()
                .map(UserListDto::convertToLibraryDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public int getListContentCount(String listName, String userId) {

        logger.info("Listenin içerik sayısı getiriliyor: {}", listName);

        validator.userIdControl(userId);

        UserList list = userListRepository.findByNameAndUserId(listName, userId)
                .orElseThrow(() -> new ListNotFoundException("Bu isimde bir liste bulunamadı"));

        return list.getContentCount();
    }

    @Transactional
    public void increaseContentCount(String listName, String userId) {
        logger.info("Listenin içerik sayısı artırılıyor: {}", listName);

        validator.userIdControl(userId);

        UserList list = userListRepository.findByNameAndUserId(listName, userId)
                .orElseThrow(() -> new ListNotFoundException("Bu isimde bir liste bulunamadı"));

        list.increaseContentCount();

        userListRepository.save(list);
    }

    @Transactional
    public void decreaseContentCount(String listName, String userId) {
        logger.info("Listenin içerik sayısı azaltılıyor: {}", listName);

        validator.userIdControl(userId);

        UserList list = userListRepository.findByNameAndUserId(listName, userId)
                .orElseThrow(() -> new ListNotFoundException("Bu isimde bir liste bulunamadı"));

        if (list.getContentCount() > 0) {
            list.setContentCount(list.getContentCount() - 1);
            userListRepository.save(list);
        }
    }


}
