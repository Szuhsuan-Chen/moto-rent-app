package com.motorent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateStatusDto {

    @NotBlank(message = "status is required")
    private String status;
}
