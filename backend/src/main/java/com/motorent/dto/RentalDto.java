package com.motorent.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RentalDto {
    private Long id;
    private Long motorcycleId;      // → motorcycle_id
    private String motorcycleTitle; // → motorcycle_title
    private String motorcycleBrand; // → motorcycle_brand
    private String customerName;    // → customer_name
    private String customerPhone;   // → customer_phone
    private String customerEmail;   // → customer_email
    private String branch;
    private String rentalDate;      // → rental_date (格式化後的字串)
    private String startTime;       // → start_time
    private String duration;
    private String endDatetime;     // → end_datetime
    private BigDecimal totalPrice;  // → total_price
    private String status;
    private String notes;
    private String createdAt;       // → created_at
    private String updatedAt;       // → updated_at
}
