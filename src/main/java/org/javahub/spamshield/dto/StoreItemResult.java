package org.javahub.spamshield.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/** Author: Mohammed Anas Date: 30/10/2026 */
@Schema(description = "Outcome of storing one call record")
public record StoreItemResult(
        @Schema(example = "cr-sample-001") String callRecordId,
        Status status,
        List<String> reasons
) {
    public enum Status {
        CREATED,
        UPDATED,
        REJECTED
    }

    public static StoreItemResult created(String callRecordId) {
        return new StoreItemResult(callRecordId, Status.CREATED, List.of());
    }

    public static StoreItemResult updated(String callRecordId) {
        return new StoreItemResult(callRecordId, Status.UPDATED, List.of());
    }

    public static StoreItemResult rejected(String callRecordId, List<String> reasons) {
        return new StoreItemResult(callRecordId, Status.REJECTED, List.copyOf(reasons));
    }
}
