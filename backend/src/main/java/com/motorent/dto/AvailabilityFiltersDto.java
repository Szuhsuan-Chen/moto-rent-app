package com.motorent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AvailabilityFiltersDto {
    private String branch;
    private String date;
    private String startTime;     // → start_time
    private String duration;
    private String priceCategory; // → price_category
    private String motoType;      // → moto_type
    private String brand;
}
