package com.example.orderservice.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventProducer {

    private static final Logger log =
            LoggerFactory.getLogger(OrderEventProducer.class);

    private static final String TOPIC = "orders.created.v1";

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public OrderEventProducer(
            KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(OrderCreatedEvent event) {
        kafkaTemplate.send(
                TOPIC,
                event.orderId().toString(),
                event
        ).whenComplete((result, exception) -> {
            if (exception != null) {
                log.error(
                        "Failed to publish order event. eventId={}",
                        event.eventId(),
                        exception
                );
                return;
            }

            log.info(
                    "Published order event. eventId={}, partition={}, offset={}",
                    event.eventId(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset()
            );
        });
    }
}
