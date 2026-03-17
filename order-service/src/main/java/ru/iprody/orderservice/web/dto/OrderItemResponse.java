package ru.iprody.orderservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemResponse {

    private Long id;
    private String productName;
    private Integer quantity;
    private MoneyResponse price;
}
