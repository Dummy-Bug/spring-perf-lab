package com.perf.lab.dtos;

import java.util.List;

import com.perf.lab.schema.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateOrderRequestDto {

    private OrderStatus status;

    private List<OrderItemActionDto> orderItems;

}
