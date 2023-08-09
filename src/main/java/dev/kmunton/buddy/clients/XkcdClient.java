package dev.kmunton.buddy.clients;

import dev.kmunton.buddy.models.xkcd.XkcdComicResponse;
import org.springframework.web.service.annotation.GetExchange;

public interface XkcdClient {

    @GetExchange("/info.0.json")
    XkcdComicResponse getCurrentComic();
}
