package ru.iprody.deliveryservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeliveryAddressResponse {

    private String street;
    private String city;
    private String postalCode;
    private String country;
}
