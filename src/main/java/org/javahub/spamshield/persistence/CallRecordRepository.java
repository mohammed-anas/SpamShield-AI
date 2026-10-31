package org.javahub.spamshield.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** Author: Mohammed Anas Date: 30/10/2026 */
public interface CallRecordRepository extends ReactiveCrudRepository<CallRecordEntity, Long> {

    Mono<CallRecordEntity> findByCallRecordId(String callRecordId);

    Flux<CallRecordEntity> findBySenderNumber(String senderNumber);

    Mono<Boolean> existsByCallRecordId(String callRecordId);
}
