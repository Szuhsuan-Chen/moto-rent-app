package com.motorent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RentalFilterParams {
    private String status;
    private String branch;
    private String customerPhone;
}
