package ru.iprody.orderservice.web.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.iprody.orderservice.domain.model.OrderStatus;

@Data
@NoArgsConstructor
public class OrderRequest {

    private Long customerId;
    private OrderStatus status;
    private ShippingAddressRequest shippingAddress;
    private List<OrderItemRequest> items = new ArrayList<>();
}
