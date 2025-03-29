package com.contentdb.library_service.controller;

import com.contentdb.library_service.dto.user_list.UserListDto;
import com.contentdb.library_service.request.CreateLibraryRequest;
import com.contentdb.library_service.request.UpdateLibraryRequest;
import com.contentdb.library_service.service.UserListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/list")
public class UserListController {

    private static final Logger logger = LoggerFactory.getLogger(UserListController.class);
    private final UserListService userListService;

    public UserListController(UserListService userListService) {
        this.userListService = userListService;
    }


    @PostMapping("/create")
    public ResponseEntity<UserListDto> createLibrary(@RequestBody CreateLibraryRequest request,
                                                     @RequestHeader("X-User-Id") String userId) {
        logger.info("POST /api/list/create - Liste oluşturuluyor: {} , userId: {}, public: {}", request.name(), userId, request.isPublic());
        return ResponseEntity.ok(userListService.createList(request, userId));
    }

    @PutMapping("/{list}/visibility")
    public ResponseEntity<Void> updateVisibility(@PathVariable String list,
                                                 @RequestParam boolean isPublic,
                                                 @RequestHeader("X-User-Id") String userId) {
        logger.info("PUT /api/list/set-visibility - Listenin görünürlüğü değiştiriliyor: {} , userId: {},", isPublic, userId);
        userListService.setListVisibility(list, isPublic, userId);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/get/all")
    public ResponseEntity<List<UserListDto>> getAllLists(@RequestHeader("X-User-Id") String userId) {
        logger.info("GET /api/list/all - kullanıcının tüm listeleri getiriliyor. userId: {}", userId);
        return ResponseEntity.ok(userListService.getAllLists(userId));
    }


    @GetMapping("/get/{list}")
    public ResponseEntity<UserListDto> getListByName(@PathVariable String list,
                                                     @RequestHeader("X-User-Id") String userId) {
        logger.info("GET /api/list/get-one - Kullanıcının listesi getiriliyor: {}. userId: {}", list, userId);
        return ResponseEntity.ok(userListService.getListByName(list, userId));
    }

    @GetMapping("/public/{userId}")
    public ResponseEntity<List<UserListDto>> getPublicLists(@PathVariable String userId) {
        logger.info("GET /api/list/public - Kullanıcının public olan listeleri getiriliyor. userId: {} ", userId);
        return ResponseEntity.ok(userListService.getPublicLists(userId));
    }


    @PutMapping("/update/{current}")
    public ResponseEntity<UserListDto> updateLibrary(@PathVariable String current,
                                                     @RequestBody UpdateLibraryRequest request,
                                                     @RequestHeader("X-User-Id") String userId) {
        logger.info("PUT /api/list/update - Liste güncelleniyor eski liste: {}, yeni liste: {} {} , userId: {}", current, request.name(), request.description(), userId);
        return ResponseEntity.ok(userListService.updateList(current, request, userId));
    }


    @GetMapping("/popular")
    public ResponseEntity<List<UserListDto>> getPopularLists() {
        logger.info("GET /api/list/popular - Sistemdeki en popüler 10 liste getiriliyor.");
        return ResponseEntity.ok(userListService.getMostPopularLists());
    }

    @DeleteMapping("/delete/{list}")
    public ResponseEntity<Void> deleteLibrary(@PathVariable String list,
                                              @RequestHeader("X-User-Id") String userId) {
        logger.info("DELETE /api/list/delete - Liste siliniyor: {} ,userId: {}", list, userId);
        userListService.deleteList(list, userId);
        return ResponseEntity.noContent().build();
    }
}
