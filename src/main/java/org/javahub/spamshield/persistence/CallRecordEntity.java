package org.javahub.spamshield.persistence;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/** Author: Mohammed Anas Date: 30/10/2026 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("call_records")
public class CallRecordEntity {

    @Id
    private Long id;

    @Column("call_record_id")
    private String callRecordId;

    @Column("sender_number")
    private String senderNumber;

    @Column("receiver_number")
    private String receiverNumber;

    @Column("caller_country")
    private String callerCountry;

    @Column("ring_time")
    private Instant ringTime;

    @Column("answer_time")
    private Instant answerTime;

    @Column("end_time")
    private Instant endTime;
}
