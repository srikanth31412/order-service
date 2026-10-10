package com.example.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(
        @NotBlank @Size(max = 100) String customerId,
        @NotNull @DecimalMin("0.01") @Digits(integer = 17, fraction = 2) BigDecimal amount,
        @NotEmpty List<@Valid OrderLineRequest> items) {
    public record OrderLineRequest(
            @NotBlank @Size(max = 100) String productId,
            @Positive int quantity) {}
}
