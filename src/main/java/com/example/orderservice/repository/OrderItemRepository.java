package com.example.orderservice.repository;
import com.example.orderservice.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, UUID> {}
