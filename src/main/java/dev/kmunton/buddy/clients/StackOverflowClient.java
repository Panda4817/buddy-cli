package dev.kmunton.buddy.clients;

import dev.kmunton.buddy.models.stackoverflow.StackOverflowResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface StackOverflowClient {

    @GetExchange("/2.3/search/advanced")
    StackOverflowResponse getQuestions(@RequestParam(name = "q") String query,
                                       @RequestParam String order,
                                       @RequestParam String sort,
                                       @RequestParam String site);
}
