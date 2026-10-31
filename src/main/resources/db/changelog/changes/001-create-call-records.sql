--liquibase formatted sql

--changeset spam-shield:001-create-call-records
CREATE TABLE call_records (
    id               BIGSERIAL PRIMARY KEY,
    call_record_id   VARCHAR(64)  NOT NULL UNIQUE,
    sender_number    VARCHAR(20)  NOT NULL,
    receiver_number  VARCHAR(20)  NOT NULL,
    caller_country   VARCHAR(8)   NOT NULL,
    ring_time        TIMESTAMPTZ  NOT NULL,
    answer_time      TIMESTAMPTZ,
    end_time         TIMESTAMPTZ  NOT NULL
);

CREATE INDEX idx_call_records_sender_number ON call_records (sender_number);
