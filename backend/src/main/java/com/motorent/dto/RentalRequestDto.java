package com.motorent.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

// Request DTO: 接收前端 POST body，用 @Valid + @NotBlank 做輸入驗證
@Data
public class RentalRequestDto {

    @NotNull(message = "motorcycle_id is required")
    @Positive(message = "motorcycle_id must be a positive number")
    private Long motorcycleId;  // 前端傳 motorcycle_id，SNAKE_CASE 自動對應

    @NotBlank(message = "customer_name is required")
    @Size(max = 50, message = "customer_name is too long")
    private String customerName;

    @NotBlank(message = "customer_phone is required")
    @Pattern(regexp = "^[0-9+\\-\\s]{8,20}$", message = "customer_phone format is invalid")
    private String customerPhone;

    @Email(message = "customer_email format is invalid")
    @Size(max = 100, message = "customer_email is too long")
    private String customerEmail;

    @NotBlank(message = "branch is required")
    @Pattern(regexp = "^(?:taipei|taichung|tainan)$", message = "branch must be one of: taipei, taichung, tainan")
    private String branch;

    @NotNull(message = "rental_date is required")
    @FutureOrPresent(message = "rental_date must be today or a future date")
    private LocalDate rentalDate;

    @NotNull(message = "start_time is required")
    private LocalTime startTime;

    @NotBlank(message = "duration is required")
    @Pattern(regexp = "^(?:5h|10h|24h|48h)$", message = "duration must be one of: 5h, 10h, 24h, 48h")
    private String duration;

    @Size(max = 500, message = "notes is too long")
    private String notes;
}
