package com.example.orderservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItemEntity {
    @Id private UUID id;
    @Column(name="order_id", nullable=false) private UUID orderId;
    @Column(name="product_id", nullable=false, length=100) private String productId;
    @Column(nullable=false) private int quantity;
    protected OrderItemEntity() {}
    public OrderItemEntity(UUID id, UUID orderId, String productId, int quantity) { this.id=id; this.orderId=orderId; this.productId=productId; this.quantity=quantity; }
}
