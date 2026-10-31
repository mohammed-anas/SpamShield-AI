package org.javahub.spamshield.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/** Author: Mohammed Anas Date: 29/10/2026 */
@Schema(description = "A single call detail record")
public record CallRecord(
        @Schema(example = "cr-sample-001") String callRecordId,
        @Schema(example = "+919810000101") String senderNumber,
        @Schema(example = "+919820000201") String receiverNumber,
        @Schema(example = "IN") String callerCountry,
        Instant ringTime,
        Instant answerTime,
        Instant endTime
) {}