package ru.iprody.orderservice.application;

import java.util.Collections;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.iprody.orderservice.common.ResourceNotFoundException;
import ru.iprody.orderservice.domain.model.Money;
import ru.iprody.orderservice.domain.model.Order;
import ru.iprody.orderservice.domain.model.OrderItem;
import ru.iprody.orderservice.domain.model.ShippingAddress;
import ru.iprody.orderservice.domain.repository.OrderRepository;
import ru.iprody.orderservice.web.dto.MoneyDto;
import ru.iprody.orderservice.web.dto.OrderItemDto;
import ru.iprody.orderservice.web.dto.OrderRequest;
import ru.iprody.orderservice.web.dto.OrderResponse;
import ru.iprody.orderservice.web.dto.ShippingAddressDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderApplicationService {

    private final OrderRepository orderRepository;

    @Transactional
    public OrderResponse create(OrderRequest request) {
        Order order = new Order(
                request.getCustomerId(),
                request.getStatus(),
                toShippingAddress(request.getShippingAddress()),
                toOrderItems(request.getItems())
        );
        return toResponse(orderRepository.save(order));
    }

    public List<OrderResponse> getAll() {
        return orderRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public OrderResponse getById(Long id) {
        return toResponse(getOrder(id));
    }

    @Transactional
    public OrderResponse update(Long id, OrderRequest request) {
        Order order = getOrder(id);
        order.update(
                request.getCustomerId(),
                request.getStatus(),
                toShippingAddress(request.getShippingAddress()),
                toOrderItems(request.getItems())
        );
        return toResponse(order);
    }

    @Transactional
    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order with id " + id + " was not found");
        }
        orderRepository.deleteById(id);
    }

    private Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + id + " was not found"));
    }

    private ShippingAddress toShippingAddress(ShippingAddressDto source) {
        if (source == null) {
            throw new IllegalArgumentException("Shipping address must be provided");
        }
        return new ShippingAddress(
                source.getStreet(),
                source.getCity(),
                source.getPostalCode(),
                source.getCountry()
        );
    }

    private List<OrderItem> toOrderItems(List<OrderItemDto> source) {
        if (source == null) {
            return Collections.emptyList();
        }
        return source.stream()
                .map(item -> new OrderItem(
                        item.getProductName(),
                        item.getQuantity(),
                        toMoney(item)
                ))
                .toList();
    }

    private Money toMoney(OrderItemDto item) {
        if (item == null || item.getPrice() == null) {
            throw new IllegalArgumentException("Each order item must contain price");
        }
        return new Money(item.getPrice().getAmount(), item.getPrice().getCurrency());
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus(),
                order.getCreatedAt(),
                new ShippingAddressDto(
                        order.getShippingAddress().getStreet(),
                        order.getShippingAddress().getCity(),
                        order.getShippingAddress().getPostalCode(),
                        order.getShippingAddress().getCountry()
                ),
                new MoneyDto(
                        order.getTotalAmount().getAmount(),
                        order.getTotalAmount().getCurrency()
                ),
                order.getItems()
                        .stream()
                        .map(item -> new OrderItemDto(
                                item.getId(),
                                item.getProductName(),
                                item.getQuantity(),
                                new MoneyDto(item.getPrice().getAmount(), item.getPrice().getCurrency())
                        ))
                        .toList()
        );
    }
}
