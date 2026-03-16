package ru.iprody.paymentservice.web;

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
import ru.iprody.paymentservice.application.PaymentApplicationService;
import ru.iprody.paymentservice.web.dto.PaymentRequest;
import ru.iprody.paymentservice.web.dto.PaymentResponse;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentApplicationService paymentApplicationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse create(@RequestBody PaymentRequest request) {
        return paymentApplicationService.create(request);
    }

    @GetMapping
    public List<PaymentResponse> getAll() {
        return paymentApplicationService.getAll();
    }

    @GetMapping("/{id}")
    public PaymentResponse getById(@PathVariable Long id) {
        return paymentApplicationService.getById(id);
    }

    @PutMapping("/{id}")
    public PaymentResponse update(@PathVariable Long id, @RequestBody PaymentRequest request) {
        return paymentApplicationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        paymentApplicationService.delete(id);
    }
}
