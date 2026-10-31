package org.javahub.spamshield.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

/** Author: Mohammed Anas Date: 29/10/2026 */
@Component
public class OllamaHealthIndicator implements ReactiveHealthIndicator {

    private final WebClient webClient;

    public OllamaHealthIndicator(
            @Value("${ollama.base-url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public Mono<Health> health() {
        return webClient.get()
                .uri("/api/tags")
                .retrieve()
                .toBodilessEntity()
                .timeout(Duration.ofSeconds(3))
                .map(response -> Health.up()
                        .withDetail("message", "Ollama API is reachable")
                        .build())
                .onErrorResume(error -> Mono.just(
                        Health.down()
                                .withDetail("message", "Ollama is unavailable")
                                .build()
                ));
    }
}
