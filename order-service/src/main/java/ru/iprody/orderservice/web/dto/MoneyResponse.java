package ru.iprody.orderservice.web.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MoneyResponse {

    private BigDecimal amount;
    private String currency;
}
