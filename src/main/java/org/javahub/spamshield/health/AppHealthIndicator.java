package org.javahub.spamshield.health;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/** Author: Mohammed Anas Date: 29/10/2026 */
@Component
public class AppHealthIndicator implements ReactiveHealthIndicator {
    @Override
    public Mono<Health> health() {

        return Mono.just(
                Health.up()
                      .withDetail("status", "Application is running")
                      .build());
    }
}
