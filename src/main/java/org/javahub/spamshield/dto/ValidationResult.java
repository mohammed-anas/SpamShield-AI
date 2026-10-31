package org.javahub.spamshield.dto;

import java.util.List;

/** Author: Mohammed Anas Date: 29/10/2026 */
public record ValidationResult(boolean valid, List<String> reasons) {
    public ValidationResult {
        reasons = List.copyOf(reasons); // own immutable copy
    }
}