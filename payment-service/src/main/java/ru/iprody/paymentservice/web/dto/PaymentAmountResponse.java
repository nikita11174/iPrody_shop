package ru.iprody.paymentservice.web.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentAmountResponse {

    private BigDecimal amount;
    private String currency;
}
