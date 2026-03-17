package ru.iprody.orderservice.web;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.iprody.orderservice.application.OrderApplicationService;
import ru.iprody.orderservice.web.dto.OrderRequest;
import ru.iprody.orderservice.web.dto.OrderResponse;
import ru.iprody.orderservice.web.mapper.OrderWebMapper;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderApplicationService orderApplicationService;
    private final OrderWebMapper orderWebMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@RequestBody OrderRequest orderRequest) {
        return orderWebMapper.toOrderResponse(
                orderApplicationService.create(orderWebMapper.toOrderCommand(orderRequest))
        );
    }

    @GetMapping
    public List<OrderResponse> getAll() {
        return orderApplicationService.getAll()
                .stream()
                .map(orderWebMapper::toOrderResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable("id") Long orderId) {
        return orderWebMapper.toOrderResponse(orderApplicationService.getById(orderId));
    }

    @PutMapping("/{id}")
    public OrderResponse update(@PathVariable("id") Long orderId, @RequestBody OrderRequest orderRequest) {
        return orderWebMapper.toOrderResponse(
                orderApplicationService.update(orderId, orderWebMapper.toOrderCommand(orderRequest))
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long orderId) {
        orderApplicationService.delete(orderId);
    }
}
