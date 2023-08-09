package dev.kmunton.buddy.configuration;

import dev.kmunton.buddy.clients.DadJokeClient;
import dev.kmunton.buddy.clients.OpenAiClient;
import dev.kmunton.buddy.clients.StackOverflowClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Configuration
public class WebClientConfig {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Bean
    DadJokeClient dadJokeClient() {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://icanhazdadjoke.com")
                .defaultHeader(ACCEPT, APPLICATION_JSON_VALUE)
                .build();

        return getClient(webClient, DadJokeClient.class);

    }

    @Bean
    StackOverflowClient stackOverflowClient() {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://api.stackexchange.com")
                .defaultHeader(ACCEPT, APPLICATION_JSON_VALUE)
                .build();

        return getClient(webClient, StackOverflowClient.class);

    }

    @Bean
    OpenAiClient openApiClient() {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://api.openai.com")
                .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .defaultHeader(AUTHORIZATION, "Bearer " + openAiApiKey)
                .build();

        return getClient(webClient, OpenAiClient.class);

    }

    private <T> T getClient(WebClient webClient, Class<T> clientClass) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builder(WebClientAdapter.forClient(webClient))
                .blockTimeout(Duration.ofSeconds(30))
                .build();
        return factory.createClient(clientClass);
    }
}
