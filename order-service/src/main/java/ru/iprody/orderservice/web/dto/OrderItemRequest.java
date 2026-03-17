package ru.iprody.orderservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {

    private String productName;
    private Integer quantity;
    private MoneyRequest price;
}
