package ru.iprody.deliveryservice.web.dto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TimeWindowResponse {

    private LocalTime startTime;
    private LocalTime endTime;
}
