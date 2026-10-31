package org.javahub.spamshield.loader;

import org.javahub.spamshield.dto.CallRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/** Author: Mohammed Anas Date: 30/10/2026 */
@Component
public class CallRecordDataLoader {

    private static final TypeReference<List<CallRecord>> CALL_RECORDS =
            new TypeReference<>() {};

    private final ResourceLoader resourceLoader;
    private final JsonMapper jsonMapper;
    private final String location;

    public CallRecordDataLoader(
            ResourceLoader resourceLoader,
            JsonMapper jsonMapper,
            @Value("${spam-shield.seed-data-location}") String location) {
        this.resourceLoader = resourceLoader;
        this.jsonMapper = jsonMapper;
        this.location = location;
    }

    public Mono<List<CallRecord>> load() {
        return Mono.fromCallable(this::readBlocking)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private List<CallRecord> readBlocking() {
        Resource resource = resourceLoader.getResource(location);
        if (!resource.exists()) {
            throw new IllegalStateException("Seed data file not found at " + location);
        }
        try (InputStream in = resource.getInputStream()) {
            return jsonMapper.readValue(in, CALL_RECORDS);
        } catch (IOException | JacksonException ex) {
            throw new IllegalStateException("Failed to read seed data from " + location, ex);
        }
    }
}
