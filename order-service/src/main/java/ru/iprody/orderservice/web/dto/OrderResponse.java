package ru.iprody.orderservice.web.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.iprody.orderservice.domain.model.OrderStatus;

@Data
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private Long customerId;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private ShippingAddressDto shippingAddress;
    private MoneyDto totalAmount;
    private List<OrderItemDto> items;
}
