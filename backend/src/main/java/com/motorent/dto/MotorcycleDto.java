package com.motorent.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

// DTO (Data Transfer Object): 只用於傳輸，不含業務邏輯，和 Entity 分離
// @Builder: 提供 Builder pattern，讓 Service 可以用 .field(...).build() 方式建立物件
@Data
@Builder
public class MotorcycleDto {
    private Long id;
    private String image;
    private String title;
    private String brand;
    private String priceCategory;         // → price_category
    private Integer price;                // 列表用：單一時長的價格
    private Map<String, Integer> prices;  // 詳情用：所有時長的價格表
    private String motoType;              // → moto_type
    private String engineDisplacement;    // → engine_displacement
    private String maxHorsepower;         // → max_horsepower
    private String maxTorque;             // → max_torque
    private String engineType;            // → engine_type
    private String fuelTankCapacity;      // → fuel_tank_capacity
    private String seatHeight;            // → seat_height
    private String weight;
    private AvailabilityDto availability;
    private FilterInfoDto filterInfo;     // → filter_info
}
