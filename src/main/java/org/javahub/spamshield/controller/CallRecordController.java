package org.javahub.spamshield.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.javahub.spamshield.dto.BulkStoreResponse;
import org.javahub.spamshield.dto.CallRecord;
import org.javahub.spamshield.dto.StoreItemResult;
import org.javahub.spamshield.service.CallRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

/** Author: Mohammed Anas Date: 30/10/2026 */
@RestController
@RequestMapping("/v1/call-records")
@Tag(name = "Call Records")
public class CallRecordController {

    private final CallRecordService callRecordService;

    public CallRecordController(CallRecordService callRecordService) {
        this.callRecordService = callRecordService;
    }

    @PostMapping
    @Operation(summary = "Store a single call record")
    public Mono<ResponseEntity<StoreItemResult>> store(@RequestBody CallRecord record) {
        return callRecordService.store(record).map(this::toResponse);
    }

    @PostMapping("/bulk")
    @Operation(summary = "Store call records in bulk")
    public Mono<ResponseEntity<BulkStoreResponse>> storeBulk(@RequestBody List<CallRecord> records) {
        return callRecordService.storeAll(records).map(ResponseEntity::ok);
    }

    @PostMapping("/seed")
    @Operation(summary = "Load packaged sample call records into the database")
    public Mono<ResponseEntity<BulkStoreResponse>> seed() {
        return callRecordService.seedFromFile().map(ResponseEntity::ok);
    }

    private ResponseEntity<StoreItemResult> toResponse(StoreItemResult result) {
        return switch (result.status()) {
            case CREATED -> ResponseEntity.status(HttpStatus.CREATED).body(result);
            case UPDATED -> ResponseEntity.ok(result);
            case REJECTED -> ResponseEntity.badRequest().body(result);
        };
    }
}
