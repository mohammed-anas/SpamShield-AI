package org.javahub.spamshield.persistence;

import org.javahub.spamshield.dto.CallRecord;
import org.springframework.stereotype.Component;

/** Author: Mohammed Anas Date: 30/10/2026 */
@Component
public class CallRecordMapper {

    public CallRecordEntity toNewEntity(CallRecord record) {
        return CallRecordEntity.builder()
                .callRecordId(record.callRecordId())
                .senderNumber(record.senderNumber())
                .receiverNumber(record.receiverNumber())
                .callerCountry(record.callerCountry())
                .ringTime(record.ringTime())
                .answerTime(record.answerTime())
                .endTime(record.endTime())
                .build();
    }

    public CallRecord toDto(CallRecordEntity entity) {
        return new CallRecord(
                entity.getCallRecordId(),
                entity.getSenderNumber(),
                entity.getReceiverNumber(),
                entity.getCallerCountry(),
                entity.getRingTime(),
                entity.getAnswerTime(),
                entity.getEndTime()
        );
    }
}
