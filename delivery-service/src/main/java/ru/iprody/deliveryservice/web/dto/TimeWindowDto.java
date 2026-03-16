package ru.iprody.deliveryservice.web.dto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeWindowDto {

    private LocalTime startTime;
    private LocalTime endTime;
}
