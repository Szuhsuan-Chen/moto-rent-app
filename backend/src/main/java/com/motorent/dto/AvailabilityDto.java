package com.motorent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AvailabilityDto {
    private boolean available;
    private String message;
}
