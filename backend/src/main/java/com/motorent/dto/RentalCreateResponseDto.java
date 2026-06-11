package com.motorent.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RentalCreateResponseDto {
    private Long rentalId;      // → rental_id
    private Integer totalPrice; // → total_price
    private String endDatetime; // → end_datetime
}
