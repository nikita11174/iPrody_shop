package ru.iprody.deliveryservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAddressDto {

    private String street;
    private String city;
    private String postalCode;
    private String country;
}
