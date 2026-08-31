package com.perf.lab.dtos;

import com.perf.lab.schema.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetOrderSummaryResponseDto {

    private Long id;

    private OrderStatus status;

    private List<OrderItemResponseDto> items;

    private Integer totalItems;

    private BigDecimal totalPrice;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
