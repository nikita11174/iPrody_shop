package ru.iprody.orderservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShippingAddressResponse {

    private String street;
    private String city;
    private String postalCode;
    private String country;
}
