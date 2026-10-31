package org.javahub.spamshield.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/** Author: Mohammed Anas Date: 31/10/2026 */
@Configuration
@Profile("openai")
public class OpenAiKeyValidator {

    @Value("${spring.ai.openai.api-key:}")
    private String apiKey;

    @PostConstruct
    void requireApiKey() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "OPENAI_API_KEY is missing. Add it to the project .env file, "
                            + "or set OPENAI_API_KEY in the IntelliJ Run/Debug configuration.");
        }
    }
}
