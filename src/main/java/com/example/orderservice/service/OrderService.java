package com.example.orderservice.service;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.entity.OrderItemEntity;
import com.example.orderservice.event.OrderCreatedEvent;
import com.example.orderservice.entity.OutboxEvent;
import com.example.orderservice.repository.OrderItemRepository;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orders;
    private final OrderItemRepository items;
    private final OutboxEventRepository outbox;
    private final ObjectMapper mapper;

    public OrderService(OrderRepository orders, OrderItemRepository items, OutboxEventRepository outbox, ObjectMapper mapper) {
        this.orders=orders; this.items=items; this.outbox=outbox; this.mapper=mapper;
    }

    @Transactional
    public UUID create(CreateOrderRequest request) {
        UUID orderId=UUID.randomUUID();
        UUID eventId=UUID.randomUUID();
        OrderEntity order=new OrderEntity(orderId, request.customerId(), request.amount());
        orders.save(order);
        List<OrderItemEntity> itemEntities=request.items().stream()
                .map(i -> new OrderItemEntity(UUID.randomUUID(), orderId, i.productId(), i.quantity())).toList();
        items.saveAll(itemEntities);
        OrderCreatedEvent event=new OrderCreatedEvent(eventId, 1, orderId, request.customerId(), request.amount(),
                request.items().stream().map(i -> new OrderCreatedEvent.OrderItem(i.productId(), i.quantity())).toList(), Instant.now());
        try {
            outbox.save(new OutboxEvent(eventId, orderId, "OrderCreated", "orders.created.v1", orderId.toString(), mapper.writeValueAsString(event)));
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to serialize OrderCreated event", ex);
        }
        return orderId;
    }
}
