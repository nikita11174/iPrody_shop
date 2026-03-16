package ru.iprody.paymentservice.application;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.iprody.paymentservice.common.ResourceNotFoundException;
import ru.iprody.paymentservice.domain.model.Payment;
import ru.iprody.paymentservice.domain.model.PaymentAmount;
import ru.iprody.paymentservice.domain.repository.PaymentRepository;
import ru.iprody.paymentservice.web.dto.PaymentAmountDto;
import ru.iprody.paymentservice.web.dto.PaymentRequest;
import ru.iprody.paymentservice.web.dto.PaymentResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentApplicationService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentResponse create(PaymentRequest request) {
        Payment payment = new Payment(
                request.getOrderId(),
                request.getStatus(),
                request.getMethod(),
                toPaymentAmount(request.getAmount())
        );
        return toResponse(paymentRepository.save(payment));
    }

    public List<PaymentResponse> getAll() {
        return paymentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PaymentResponse getById(Long id) {
        return toResponse(getPayment(id));
    }

    @Transactional
    public PaymentResponse update(Long id, PaymentRequest request) {
        Payment payment = getPayment(id);
        payment.update(
                request.getOrderId(),
                request.getStatus(),
                request.getMethod(),
                toPaymentAmount(request.getAmount())
        );
        return toResponse(payment);
    }

    @Transactional
    public void delete(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payment with id " + id + " was not found");
        }
        paymentRepository.deleteById(id);
    }

    private Payment getPayment(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment with id " + id + " was not found"));
    }

    private PaymentAmount toPaymentAmount(PaymentAmountDto amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Payment amount must be provided");
        }
        return new PaymentAmount(amount.getAmount(), amount.getCurrency());
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getStatus(),
                payment.getMethod(),
                new PaymentAmountDto(payment.getAmount().getAmount(), payment.getAmount().getCurrency()),
                payment.getCreatedAt()
        );
    }
}
