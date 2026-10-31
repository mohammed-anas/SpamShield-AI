package org.javahub.spamshield.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Author: Mohammed Anas Date: 30/10/2026 */
@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI spamShieldOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spam Shield API")
                        .description("Reactive APIs for call records and spam analysis")
                        .version("0.0.1"));
    }
}
