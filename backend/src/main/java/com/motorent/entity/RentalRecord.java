package com.motorent.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class RentalRecord {
    private Long id;
    private Long motorcycleId;   // JPA 版是 Motorcycle 物件，MyBatis 版只存 FK
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String branch;
    private LocalDate rentalDate;
    private LocalTime startTime;
    private String duration;
    private LocalDateTime endDatetime;
    private BigDecimal totalPrice;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // JOIN 查詢時額外帶出的欄位（不在 rental_records 表，從 motorcycles JOIN 來的）
    private String motorcycleTitle;
    private String motorcycleBrand;
}
