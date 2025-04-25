package com.contentdb.library_service.controller;

import com.contentdb.library_service.client.dto.general.TmdbMultiSearchResponse;
import com.contentdb.library_service.dto.user_list_content.ListCardDto;
import com.contentdb.library_service.dto.user_list_content.UserListContentDto;
import com.contentdb.library_service.dto.user_list_content.UserListContentIdDto;
import com.contentdb.library_service.service.UserListContentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/list/content")
public class UserListContentController {

    private final UserListContentService userListContentService;
    private static final Logger logger = LoggerFactory.getLogger(UserListContentController.class);

    public UserListContentController(UserListContentService userListContentService) {
        this.userListContentService = userListContentService;
    }


    @GetMapping("/search")
    public ResponseEntity<List<TmdbMultiSearchResponse>> search(@RequestParam String query) {
        logger.info("İçerik aranıyor: {}", query);
        return ResponseEntity.ok(userListContentService.search(query));
    }

    @PostMapping("/{listName}/contents")
    public ResponseEntity<UserListContentDto> addContent(@PathVariable String listName,
                                                         @RequestParam String contentId,
                                                         @RequestParam String mediaType,
                                                         @RequestHeader("X-User-Id") String userId) {
        logger.info("PUT /add - Kütüphaneye : {} , içerik ekleniyor: {} , kullanıcı: {}", contentId, listName, userId);
        return ResponseEntity.ok(userListContentService.addContentToUserList(contentId, mediaType, listName, userId));
    }

    @DeleteMapping("/delete/{listName}/{contentId}")
    public ResponseEntity<Void> deleteContent(@PathVariable String contentId,
                                              @PathVariable String listName,
                                              @RequestHeader("X-User-Id") String userId) {
        logger.info("DELETE /delete/{}/{} - İçerik kütüphaneden siliniyor.", listName, contentId);
        userListContentService.deleteContentFromUserList(contentId, listName, userId);
        logger.info("Silme işlemi başarılı");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get/{listName}")
    public ResponseEntity<UserListContentIdDto> getAllContentsFromOneUserList(@PathVariable String listName,
                                                                              @RequestHeader("X-User-Id") String userId) {
        logger.info("GET /get/{} - Listenin içeriği getiriliyor", listName);
        return ResponseEntity.ok(userListContentService.getAllContentsFromOneUserList(listName, userId));
    }

    @GetMapping("/get/card/{contentId}/{mediaType}")
    public ResponseEntity<Object> getContentCard(@PathVariable String contentId,
                                                 @PathVariable String mediaType
    ) {
        logger.info("GET /get/card/{} - İçeriğin şeması getiriliyor", contentId);
        return ResponseEntity.ok(userListContentService.getContentCard(contentId, mediaType));
    }

    @GetMapping("/get/cards/{listName}")
    public ResponseEntity<List<ListCardDto>> getContentCardList(@PathVariable String listName,
                                                                @RequestHeader("X-User-Id") String userId) {
        logger.info("GET /get/card/{} - Kullanıcının listesindeki tüm içeriklerin şeması getiriliyor", listName);
        return ResponseEntity.ok(userListContentService.getContentCardsFromUserList(listName, userId));
    }

}
