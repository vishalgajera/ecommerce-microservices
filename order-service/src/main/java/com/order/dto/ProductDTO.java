package com.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDTO {
    private long id;
    private String name;
    private Double price;
    private Long availableQty;
}
