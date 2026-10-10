package com.example.orderservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="outbox_events")
public class OutboxEvent {
    @Id private UUID id;
    @Column(name="aggregate_id", nullable=false) private UUID aggregateId;
    @Column(name="event_type", nullable=false, length=100) private String eventType;
    @Column(nullable=false, length=249) private String topic;
    @Column(name="message_key", nullable=false, length=200) private String messageKey;
    @Column(nullable=false, columnDefinition="text") private String payload;
    @Column(name="created_at", nullable=false) private Instant createdAt;
    @Column(name="published_at") private Instant publishedAt;
    @Column(nullable=false) private int attempts;
    @Column(name="next_attempt_at", nullable=false) private Instant nextAttemptAt;
    @Column(name="claimed_by") private String claimedBy;
    @Column(name="locked_until") private Instant lockedUntil;
    @Column(name="last_error", length=2000) private String lastError;
    protected OutboxEvent() {}
    public OutboxEvent(UUID id, UUID aggregateId, String eventType, String topic, String messageKey, String payload) {
        this.id=id; this.aggregateId=aggregateId; this.eventType=eventType; this.topic=topic; this.messageKey=messageKey; this.payload=payload; this.createdAt=Instant.now(); this.nextAttemptAt=this.createdAt;
    }
}
