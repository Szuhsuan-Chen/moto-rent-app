package com.motorent.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

// Request DTO: 接收前端 POST body，用 @Valid + @NotBlank 做輸入驗證
@Data
public class RentalRequestDto {

    @NotNull(message = "motorcycle_id is required")
    private Long motorcycleId;  // 前端傳 motorcycle_id，SNAKE_CASE 自動對應

    @NotBlank(message = "customer_name is required")
    private String customerName;

    @NotBlank(message = "customer_phone is required")
    private String customerPhone;

    private String customerEmail;

    @NotBlank(message = "branch is required")
    private String branch;

    @NotNull(message = "rental_date is required")
    @FutureOrPresent(message = "rental_date must be today or a future date")
    private LocalDate rentalDate;

    @NotBlank(message = "start_time is required")
    private String startTime;

    @NotBlank(message = "duration is required")
    private String duration;

    private String notes;
}
