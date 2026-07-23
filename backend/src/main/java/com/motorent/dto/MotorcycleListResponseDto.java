package com.motorent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MotorcycleListResponseDto {
    private int count;
    private List<MotorcycleDto> data;
    private MotorcycleFiltersDto filtersApplied; // → filters_applied
}
