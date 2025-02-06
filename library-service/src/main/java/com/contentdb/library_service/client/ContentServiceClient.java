package com.contentdb.library_service.client;

import com.contentdb.library_service.dto.contentDTO.DetailsRequest;
import com.contentdb.library_service.dto.contentDTO.ImdbIDRequest;
import com.contentdb.library_service.dto.contentDTO.PosterRequest;
import com.contentdb.library_service.dto.contentDTO.SearchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "content-service", path = "v1/content")
public interface ContentServiceClient {

    @GetMapping("/imdbId")
    public ResponseEntity<ImdbIDRequest> getImdbIDByTitle(@RequestParam String title);

    @GetMapping("/poster")
    public ResponseEntity<PosterRequest> getPosterByImdbID(@RequestParam String id);

    @GetMapping("/details")
    public ResponseEntity<DetailsRequest> getDetailsByImdbID(@RequestParam String id);

    @GetMapping("/search")
    public ResponseEntity<SearchResponse> searchContents(@RequestParam String title);
}
