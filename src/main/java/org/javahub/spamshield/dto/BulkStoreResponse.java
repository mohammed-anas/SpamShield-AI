package org.javahub.spamshield.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/** Author: Mohammed Anas Date: 30/10/2026 */
@Schema(description = "Summary of a bulk or seed store operation")
public record BulkStoreResponse(
        int created,
        int updated,
        int rejected,
        List<StoreItemResult> items
) {
    public static BulkStoreResponse from(List<StoreItemResult> items) {
        int created = 0;
        int updated = 0;
        int rejected = 0;
        for (StoreItemResult item : items) {
            switch (item.status()) {
                case CREATED -> created++;
                case UPDATED -> updated++;
                case REJECTED -> rejected++;
            }
        }
        return new BulkStoreResponse(created, updated, rejected, items);
    }
}
