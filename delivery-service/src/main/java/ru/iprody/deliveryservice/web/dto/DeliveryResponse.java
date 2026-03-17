package ru.iprody.deliveryservice.web.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.iprody.deliveryservice.domain.model.DeliveryStatus;

@Data
@AllArgsConstructor
public class DeliveryResponse {

    private Long id;
    private Long orderId;
    private DeliveryStatus status;
    private DeliveryAddressResponse deliveryAddress;
    private LocalDate deliveryDate;
    private TimeWindowResponse timeWindow;
    private String trackingNumber;
}
