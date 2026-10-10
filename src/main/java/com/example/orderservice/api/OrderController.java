package com.example.orderservice.api;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService service;
    public OrderController(OrderService service) { this.service=service; }
    @PostMapping
    public ResponseEntity<Map<String, UUID>> create(@Valid @RequestBody CreateOrderRequest request) {
        UUID id=service.create(request);
        return ResponseEntity.accepted().location(URI.create("/api/v1/orders/"+id)).body(Map.of("orderId", id));
    }
}
