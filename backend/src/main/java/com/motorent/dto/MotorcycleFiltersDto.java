package com.motorent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MotorcycleFiltersDto {
    private String priceCategory; // → price_category
    private String motoType;      // → moto_type
    private String brand;
}
