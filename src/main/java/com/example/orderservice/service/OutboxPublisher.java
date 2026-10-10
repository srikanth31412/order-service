package com.example.orderservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class OutboxPublisher {
    private static final Logger log=LoggerFactory.getLogger(OutboxPublisher.class);
    private final JdbcTemplate jdbc;
    private final KafkaTemplate<String,String> kafka;
    private final TransactionTemplate tx;
    private final String workerId=UUID.randomUUID().toString();
    private final int batchSize;
    private final int leaseSeconds;

    public OutboxPublisher(JdbcTemplate jdbc, KafkaTemplate<String,String> kafka, TransactionTemplate tx,
            @Value("${outbox.batch-size:100}") int batchSize,
            @Value("${outbox.lease-seconds:120}") int leaseSeconds) {
        this.jdbc=jdbc; this.kafka=kafka; this.tx=tx; this.batchSize=batchSize; this.leaseSeconds=leaseSeconds;
    }

    @Scheduled(fixedDelayString="${outbox.poll-delay-ms:500}")
    public void publishBatch() {
        List<Row> rows=tx.execute(status -> jdbc.query("""
            WITH picked AS (
              SELECT id FROM outbox_events
              WHERE published_at IS NULL AND next_attempt_at <= now()
                AND (locked_until IS NULL OR locked_until < now())
              ORDER BY created_at FOR UPDATE SKIP LOCKED LIMIT ?
            )
            UPDATE outbox_events o SET claimed_by=?, locked_until=now() + (? * interval '1 second')
            FROM picked WHERE o.id=picked.id
            RETURNING o.id,o.topic,o.message_key,o.payload
            """, (rs,n)->new Row(rs.getObject("id",UUID.class),rs.getString("topic"),rs.getString("message_key"),rs.getString("payload")), batchSize, workerId, leaseSeconds));
        if (rows==null || rows.isEmpty()) return;
        for (Row row:rows) {
            try {
                kafka.send(row.topic(),row.key(),row.payload()).get(30, TimeUnit.SECONDS);
                jdbc.update("UPDATE outbox_events SET published_at=now(),claimed_by=NULL,locked_until=NULL,last_error=NULL WHERE id=? AND claimed_by=?",row.id(),workerId);
            } catch (Exception ex) {
                String message=(ex.getClass().getSimpleName()+": "+String.valueOf(ex.getMessage()));
                if(message.length()>1900) message=message.substring(0,1900);
                jdbc.update("""
                    UPDATE outbox_events SET attempts=attempts+1,last_error=?,
                    next_attempt_at=now() + (LEAST(900, power(2, LEAST(attempts,9))) * interval '1 second'),
                    claimed_by=NULL,locked_until=NULL WHERE id=? AND claimed_by=?
                    """,message,row.id(),workerId);
                log.warn("Outbox publish failed; eventId={} will retry",row.id(),ex);
            }
        }
    }
    private record Row(UUID id,String topic,String key,String payload) {}
}
