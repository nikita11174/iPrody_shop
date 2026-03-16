package ru.iprody.deliveryservice.web;

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
import ru.iprody.deliveryservice.application.DeliveryApplicationService;
import ru.iprody.deliveryservice.web.dto.DeliveryRequest;
import ru.iprody.deliveryservice.web.dto.DeliveryResponse;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryApplicationService deliveryApplicationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeliveryResponse create(@RequestBody DeliveryRequest request) {
        return deliveryApplicationService.create(request);
    }

    @GetMapping
    public List<DeliveryResponse> getAll() {
        return deliveryApplicationService.getAll();
    }

    @GetMapping("/{id}")
    public DeliveryResponse getById(@PathVariable Long id) {
        return deliveryApplicationService.getById(id);
    }

    @PutMapping("/{id}")
    public DeliveryResponse update(@PathVariable Long id, @RequestBody DeliveryRequest request) {
        return deliveryApplicationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        deliveryApplicationService.delete(id);
    }
}
