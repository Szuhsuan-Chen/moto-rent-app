package com.motorent.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilterInfoDto {
    private String branch;
    private String date;
    private String startTime;   // Jackson SNAKE_CASE → start_time
    private String duration;
}
