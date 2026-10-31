package org.javahub.spamshield.health;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** Author: Mohammed Anas Date: 29/10/2026 */
@Component
public class DatabaseHealthIndicator implements ReactiveHealthIndicator {

    private final ConnectionFactory connectionFactory;

    public DatabaseHealthIndicator(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Mono<Health> health() {
        return Mono.usingWhen(
                Mono.from(connectionFactory.create()),
                connection -> Mono.just(Health.up().withDetail("status", "Database is up").build()),
                connection -> Mono.from(connection.close())
                        .onErrorResume(error -> Mono.just(Health.down().withDetail("error", error.getMessage()).build()).then())
        );
    }
}
