package org.javahub.spamshield.validators;

import org.javahub.spamshield.dto.CallRecord;
import org.javahub.spamshield.dto.ValidationResult;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/** Author: Mohammed Anas Date: 29/10/2026 */
@Component
public class CallRecordValidator {

    private static final Pattern PHONE_PATTERN = Pattern.compile("\\+[1-9]\\d{1,14}"); // E.164

    public ValidationResult validate(CallRecord r) {
        if (r == null) {
            return new ValidationResult(false, List.of("Call record is missing."));
        }
        List<String> reasons = new ArrayList<>(); // fresh list per call

        if (isBlank(r.callRecordId())) reasons.add("Call record ID is missing.");
        if (isBlank(r.callerCountry())) reasons.add("Caller country is missing.");

        if (!isPhone(r.senderNumber())) reasons.add("Sender number is invalid.");
        if (!isPhone(r.receiverNumber())) reasons.add("Receiver number is invalid.");

        Instant ring = r.ringTime();
        Instant answer = r.answerTime();
        Instant end = r.endTime();

        if (ring == null) reasons.add("Ring time is missing.");
        if (end == null) reasons.add("End time is missing.");

        if (ring != null && end != null && end.isBefore(ring))
            reasons.add("End time cannot be before ring time.");
        if (answer != null && ring != null && answer.isBefore(ring))
            reasons.add("Answer time cannot be before ring time.");
        if (answer != null && end != null && answer.isAfter(end))
            reasons.add("Answer time cannot be after end time.");

        return new ValidationResult(reasons.isEmpty(), reasons);
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private static boolean isPhone(String s) {
        return s != null && PHONE_PATTERN.matcher(s).matches();
    }
}