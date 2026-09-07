package com.motorent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AvailabilitySearchResponseDto {
    private int count;
    private List<MotorcycleDto> data;
    private AvailabilityFiltersDto filtersApplied; // → filters_applied
}
