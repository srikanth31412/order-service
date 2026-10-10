package com.example.orderservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id private UUID id;
    @Column(name = "customer_id", nullable = false, length = 100) private String customerId;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal amount;
    @Column(nullable = false, length = 30) private String status;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    protected OrderEntity() {}
    public OrderEntity(UUID id, String customerId, BigDecimal amount) {
        this.id=id; this.customerId=customerId; this.amount=amount; this.status="CREATED"; this.createdAt=Instant.now();
    }
    public UUID getId(){return id;} public String getCustomerId(){return customerId;} public BigDecimal getAmount(){return amount;} public String getStatus(){return status;} public Instant getCreatedAt(){return createdAt;}
}
