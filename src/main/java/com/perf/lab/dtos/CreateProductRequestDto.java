package com.perf.lab.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequestDto {

    private String title;

    private String description;

    private String image;

    private BigDecimal price;

    private String category;

    private String rating;
}
