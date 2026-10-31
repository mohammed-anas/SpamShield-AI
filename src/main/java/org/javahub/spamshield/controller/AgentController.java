package org.javahub.spamshield.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.javahub.spamshield.dto.ChatRequest;
import org.javahub.spamshield.dto.ChatResponse;
import org.javahub.spamshield.service.CallRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/** Author: Mohammed Anas Date: 30/10/2026 */
@RestController
@RequestMapping("/v1/agent")
@Tag(name = "Agent")
public class AgentController {

    private final CallRecordService callRecordService;

    public AgentController(CallRecordService callRecordService) {
        this.callRecordService = callRecordService;
    }

    @PostMapping("/chat")
    @Operation(summary = "Ask the agent about a phone number using stored call records")
    public Mono<ResponseEntity<ChatResponse>> chat(@RequestBody @Valid ChatRequest chatRequest) {
        return callRecordService.chat(chatRequest).map(ResponseEntity::ok);
    }
}
