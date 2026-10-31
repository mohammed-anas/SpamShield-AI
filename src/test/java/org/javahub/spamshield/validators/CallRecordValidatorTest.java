package org.javahub.spamshield.validators;

import org.javahub.spamshield.dto.CallRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/** Author: Mohammed Anas Date: 29/10/2026 */
class CallRecordValidatorTest {

    private final CallRecordValidator validator = new CallRecordValidator();

    private static final Instant RING = Instant.parse("2026-10-29T10:00:00Z");

    private static CallRecord valid() {
        return build("id-1", "+919800000001", "+919800000002", "IN",
                RING, RING.plusSeconds(5), RING.plusSeconds(30));
    }

    private static CallRecord build(String id, String from, String to, String country,
                                    Instant ring, Instant answer, Instant end) {
        return new CallRecord(id, from, to, country, ring, answer, end);
    }

    @Test
    void validRecord_passes() {
        var result = validator.validate(valid());
        assertThat(result.valid()).isTrue();
        assertThat(result.reasons()).isEmpty();
    }

    @Test
    void oneSecondAnsweredCall_isOddButValid() {
        var r = build("id-1", "+919800000001", "+919800000002", "IN",
                RING, RING.plusSeconds(2), RING.plusSeconds(3));
        assertThat(validator.validate(r).valid()).isTrue();
    }

    @Test
    void unansweredCall_isValid() {
        var r = build("id-1", "+919800000001", "+919800000002", "IN",
                RING, null, RING.plusSeconds(3));
        assertThat(validator.validate(r).valid()).isTrue();
    }

    @Test
    void nullRecord_isInvalidNotException() {
        var result = validator.validate(null);
        assertThat(result.valid()).isFalse();
        assertThat(result.reasons()).containsExactly("Call record is missing.");
    }

    @Test
    void blankId_isInvalid() {
        var r = build("   ", "+919800000001", "+919800000002", "IN",
                RING, null, RING.plusSeconds(3));
        assertThat(validator.validate(r).reasons()).contains("Call record ID is missing.");
    }

    @Test
    void missingCountry_isInvalid() {
        var r = build("id-1", "+919800000001", "+919800000002", null,
                RING, null, RING.plusSeconds(3));
        assertThat(validator.validate(r).reasons()).contains("Caller country is missing.");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   ", "9198000", "+0123456", "+91-98000", "abc", "+1"})
    void badSenderNumber_isInvalid(String number) {
        var r = build("id-1", number, "+919800000002", "IN", RING, null, RING.plusSeconds(3));
        assertThat(validator.validate(r).reasons()).contains("Sender number is invalid.");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   ", "9198000", "abc"})
    void badReceiverNumber_isInvalid(String number) {
        var r = build("id-1", "+919800000001", number, "IN", RING, null, RING.plusSeconds(3));
        assertThat(validator.validate(r).reasons()).contains("Receiver number is invalid.");
    }

    @Test
    void sameSenderAndReceiver_isNotMalformed() {
        // suspicious, not broken: goes on to detection
        var r = build("id-1", "+919800000001", "+919800000001", "IN",
                RING, null, RING.plusSeconds(3));
        assertThat(validator.validate(r).valid()).isTrue();
    }

    @Test
    void missingTimes_areInvalid() {
        var r = build("id-1", "+919800000001", "+919800000002", "IN", null, null, null);
        assertThat(validator.validate(r).reasons())
                .contains("Ring time is missing.", "End time is missing.");
    }

    @Test
    void endBeforeRing_isInvalid() {
        var r = build("id-1", "+919800000001", "+919800000002", "IN",
                RING, null, RING.minusSeconds(1));
        assertThat(validator.validate(r).reasons()).contains("End time cannot be before ring time.");
    }

    @Test
    void answerBeforeRing_isInvalid() {
        var r = build("id-1", "+919800000001", "+919800000002", "IN",
                RING, RING.minusSeconds(1), RING.plusSeconds(10));
        assertThat(validator.validate(r).reasons()).contains("Answer time cannot be before ring time.");
    }

    @Test
    void answerAfterEnd_isInvalid() {
        var r = build("id-1", "+919800000001", "+919800000002", "IN",
                RING, RING.plusSeconds(20), RING.plusSeconds(10));
        assertThat(validator.validate(r).reasons()).contains("Answer time cannot be after end time.");
    }

    @Test
    void validationsDoNotLeakBetweenCalls() {
        var bad = build("", "x", "y", null, null, null, null);
        assertThat(validator.validate(bad).valid()).isFalse();
        assertThat(validator.validate(valid()).valid()).isTrue();
    }

    @Test
    void allProblemsAreReportedTogether() {
        var bad = build("", "x", "y", null, null, null, null);
        assertThat(validator.validate(bad).reasons()).hasSizeGreaterThan(3);
    }
}
