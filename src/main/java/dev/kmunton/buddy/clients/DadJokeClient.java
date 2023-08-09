package dev.kmunton.buddy.clients;

import dev.kmunton.buddy.models.dadjoke.DadJokeResponse;
import dev.kmunton.buddy.models.dadjoke.DadJokesList;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface DadJokeClient {

    @GetExchange("/")
    DadJokeResponse random();

    @GetExchange("/search")
    DadJokesList search(@RequestParam String term, @RequestParam int limit);
}
