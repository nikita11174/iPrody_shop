package ru.iprody.deliveryservice.web.dto;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.iprody.deliveryservice.domain.model.DeliveryStatus;

@Data
@NoArgsConstructor
public class DeliveryRequest {

    private Long orderId;
    private DeliveryStatus status;
    private DeliveryAddressRequest deliveryAddress;
    private LocalDate deliveryDate;
    private TimeWindowRequest timeWindow;
    private String trackingNumber;
}
