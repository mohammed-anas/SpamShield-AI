package org.javahub.spamshield.service;

import org.javahub.spamshield.dto.BulkStoreResponse;
import org.javahub.spamshield.dto.CallRecord;
import org.javahub.spamshield.dto.ChatRequest;
import org.javahub.spamshield.dto.ChatResponse;
import org.javahub.spamshield.dto.StoreItemResult;
import org.javahub.spamshield.dto.ValidationResult;
import org.javahub.spamshield.loader.CallRecordDataLoader;
import org.javahub.spamshield.persistence.CallRecordMapper;
import org.javahub.spamshield.persistence.CallRecordRepository;
import org.javahub.spamshield.utils.PhoneNumberExtractor;
import org.javahub.spamshield.validators.CallRecordValidator;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Author: Mohammed Anas Date: 30/10/2026 */
@Service
public class CallRecordService {

  private final CallRecordRepository repository;
  private final CallRecordMapper mapper;
  private final CallRecordValidator validator;
  private final CallRecordDataLoader dataLoader;
  private final ChatClient chatClient;
    private final ChatMemory chatMemory;

  public CallRecordService(
          CallRecordRepository repository,
          CallRecordMapper mapper,
          CallRecordValidator validator,
          CallRecordDataLoader dataLoader,
          @Qualifier("customChatClient") ChatClient chatClient, ChatMemory chatMemory) {
    this.repository = repository;
    this.mapper = mapper;
    this.validator = validator;
    this.dataLoader = dataLoader;
    this.chatClient = chatClient;
    this.chatMemory = chatMemory;
  }

  public Mono<StoreItemResult> store(CallRecord record) {
    ValidationResult validation = validator.validate(record);
    if (!validation.valid()) {
      String id = record == null ? null : record.callRecordId();
      return Mono.just(StoreItemResult.rejected(id, validation.reasons()));
    }
    return repository
        .existsByCallRecordId(record.callRecordId())
        .flatMap(
            exists ->
                save(record)
                    .thenReturn(
                        exists
                            ? StoreItemResult.updated(record.callRecordId())
                            : StoreItemResult.created(record.callRecordId())));
  }

  public Mono<BulkStoreResponse> storeAll(List<CallRecord> records) {
    if (records == null || records.isEmpty()) {
      return Mono.just(new BulkStoreResponse(0, 0, 0, List.of()));
    }
    return Flux.fromIterable(records)
        .concatMap(this::store)
        .collectList()
        .map(BulkStoreResponse::from);
  }

  public Mono<BulkStoreResponse> seedFromFile() {
    return dataLoader.load().flatMap(this::storeAll);
  }

  public Mono<CallRecord> save(CallRecord record) {

    return repository
        .findByCallRecordId(record.callRecordId())
        .flatMap(
            existing -> {
              existing.setSenderNumber(record.senderNumber());
              existing.setReceiverNumber(record.receiverNumber());
              existing.setCallerCountry(record.callerCountry());
              existing.setRingTime(record.ringTime());
              existing.setAnswerTime(record.answerTime());
              existing.setEndTime(record.endTime());

              return repository.save(existing);
            })
        .switchIfEmpty(Mono.defer(() -> repository.save(mapper.toNewEntity(record))))
        .map(mapper::toDto);
  }

  public Mono<CallRecord> findByCallRecordId(String callRecordId) {
    return repository.findByCallRecordId(callRecordId).map(mapper::toDto);
  }

  public Flux<CallRecord> findAll() {
    return repository.findAll().map(mapper::toDto);
  }

  public Flux<CallRecord> findBySenderNumber(String senderNumber) {
    return repository.findBySenderNumber(senderNumber).map(mapper::toDto);
  }

  public Mono<Long> count() {
    return repository.count();
  }

    public Mono<ChatResponse> chat(ChatRequest req) {
        String id = (req.conversationId() == null || req.conversationId().isBlank())
                ? UUID.randomUUID().toString() : req.conversationId();

        List<String> numbers = PhoneNumberExtractor.getPhoneNumbers(req.message());

        if (numbers.isEmpty()) {
            // no number: new conversation or follow-up?
            return Mono.fromCallable(() -> chatMemory.get(id).isEmpty())   // inject ChatMemory
                    .subscribeOn(Schedulers.boundedElastic())
                    .flatMap(isNew -> isNew
                            ? Mono.just(new ChatResponse(id, "Which phone number would you like me to check?"))
                            : askModel(id, req.message(), "none (follow-up question)",
                            "No new lookup was done for this message."));
        }

        String number = numbers.getFirst();
        return findBySenderNumber(number)
                .collectList()
                .flatMap(records -> askModel(id, req.message(), number, records));
    }

    private Mono<ChatResponse> askModel(String id, String question, String number, Object records) {
        return Mono.fromCallable(() -> {
                    String response = chatClient.prompt()
                            .user(u -> u.param("phoneNumber", number)
                                    .param("records", records)
                                    .param("question", question))
                            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, id))
                            .call().content();
                    return new ChatResponse(id, response);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }
}
