package com.contentdb.content_service.client;

import com.contentdb.content_service.model.DetailsRequest;
import com.contentdb.content_service.model.ImdbIDRequest;
import com.contentdb.content_service.model.PosterRequest;
import com.contentdb.content_service.model.SearchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "content-client", url = "${omdb.api.url}")
public interface ContentFeignClient {

    @GetMapping("/")
    ImdbIDRequest getImdbIDByTitle(@RequestParam("t") String title,
                                   @RequestParam("apikey") String apiKey);

    @GetMapping("/")
    PosterRequest getPosterByImdbID(@RequestParam("i") String imdbID,
                                    @RequestParam("apikey") String apiKey);

    @GetMapping("/")
    DetailsRequest getDetailsByImdbID(@RequestParam("i") String imdbID,
                                      @RequestParam("apikey") String apiKey);

    @GetMapping("/")
    SearchResponse searchContents(@RequestParam("s") String title,
                                  @RequestParam("apikey") String apiKey);

}
