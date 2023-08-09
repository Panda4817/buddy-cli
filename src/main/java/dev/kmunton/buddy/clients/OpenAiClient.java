package dev.kmunton.buddy.clients;

import dev.kmunton.buddy.models.openai.OpenAiRequest;
import dev.kmunton.buddy.models.openai.OpenAiResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

public interface OpenAiClient {

    @PostExchange("/v1/chat/completions")
    OpenAiResponse getChatGptAnswer(@RequestBody OpenAiRequest request);
}
