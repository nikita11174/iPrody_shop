package ru.iprody.paymentservice.web.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentAmountDto {

    private BigDecimal amount;
    private String currency;
}
