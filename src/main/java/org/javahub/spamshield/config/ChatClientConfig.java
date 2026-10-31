package org.javahub.spamshield.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;


/** Author: Mohammed Anas Date: 30/10/2026 */
@Configuration
public class ChatClientConfig {

    @Bean
    ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(100)
                .build();
    }

    @Bean
    ChatClient customChatClient(
            ChatClient.Builder builder,
            ResourceLoader resourceLoader, ChatMemory chatMemory) {

        Advisor memoryAdvisor = MessageChatMemoryAdvisor.builder(
                chatMemory
        ).build();

        return builder
                .defaultSystem(resourceLoader.getResource(
                        "classpath:prompts/system/system-instructions.txt"))
                .defaultUser(resourceLoader.getResource(
                        "classpath:prompts/user/user-instructions.txt"))
                .defaultAdvisors(memoryAdvisor)
                .build();
    }
}
