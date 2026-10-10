package com.example.orderservice.repository;
import com.example.orderservice.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {}
