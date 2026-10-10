package com.example.orderservice.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Versioned integration event. Keep this contract compatible across independent repositories. */
public record OrderCreatedEvent(
        UUID eventId,
        int schemaVersion,
        UUID orderId,
        String customerId,
        BigDecimal amount,
        List<OrderItem> items,
        Instant occurredAt) {
    public record OrderItem(String productId, int quantity) {}
}
