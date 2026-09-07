package com.motorent.controller;

import com.motorent.dto.AvailabilityFiltersDto;
import com.motorent.dto.AvailabilitySearchResponseDto;
import com.motorent.dto.MotorcycleDetailResponseDto;
import com.motorent.dto.MotorcycleDto;
import com.motorent.dto.MotorcycleFiltersDto;
import com.motorent.dto.MotorcycleListResponseDto;
import com.motorent.service.MotorcycleService;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// @RestController = @Controller + @ResponseBody：所有方法回傳值直接序列化成 JSON
// @Validated: 讓 @RequestParam 上的 @Pattern/@Size 等驗證生效（失敗時拋出 ConstraintViolationException）
@RestController
@RequestMapping("/api")
@Validated
public class MotorcycleController {

    @Autowired
    private MotorcycleService motorcycleService;

    // 純瀏覽車輛目錄：不查可用性，只依 price_category/moto_type/brand 篩選
    @GetMapping("/motorcycles")
    public ResponseEntity<MotorcycleListResponseDto> getMotorcycles(
            @RequestParam(name = "price_category", required = false)
            @Pattern(regexp = "^(?:type-ss|type-s|type-a|type-b|type-c|type-minibike)$", message = "price_category is invalid")
            String priceCategory,

            @RequestParam(name = "moto_type", required = false)
            @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "moto_type format is invalid")
            String motoType,

            @RequestParam(required = false)
            @Size(max = 50, message = "brand is too long")
            String brand
    ) {
        List<MotorcycleDto> data = motorcycleService.findMotorcycles(priceCategory, motoType, brand);

        var filtersApplied = new MotorcycleFiltersDto(priceCategory, motoType, brand);
        var response = new MotorcycleListResponseDto(data.size(), data, filtersApplied);

        return ResponseEntity.ok(response);
    }

    // 搜尋指定分店/時段的可用性：branch/date/start_time/duration 皆為必填
    @GetMapping("/motorcycles/availability")
    public ResponseEntity<AvailabilitySearchResponseDto> searchAvailability(
            @RequestParam
            @Pattern(regexp = "^(?:taipei|taichung|tainan)$", message = "branch must be one of: taipei, taichung, tainan")
            String branch,

            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            @FutureOrPresent(message = "date must be today or a future date")
            LocalDate date,

            @RequestParam(name = "start_time")
            @DateTimeFormat(pattern = "HH:mm")
            LocalTime startTime,

            @RequestParam
            @Pattern(regexp = "^(?:5h|10h|24h|48h)$", message = "duration must be one of: 5h, 10h, 24h, 48h")
            String duration,

            @RequestParam(name = "price_category", required = false)
            @Pattern(regexp = "^(?:type-ss|type-s|type-a|type-b|type-c|type-minibike)$", message = "price_category is invalid")
            String priceCategory,

            @RequestParam(name = "moto_type", required = false)
            @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "moto_type format is invalid")
            String motoType,

            @RequestParam(required = false)
            @Size(max = 50, message = "brand is too long")
            String brand
    ) {
        List<MotorcycleDto> data = motorcycleService.searchAvailability(
                branch, date, startTime, duration, priceCategory, motoType, brand
        );

        var filtersApplied = new AvailabilityFiltersDto(
                branch, date.toString(), startTime.toString(), duration, priceCategory, motoType, brand
        );
        var response = new AvailabilitySearchResponseDto(data.size(), data, filtersApplied);

        return ResponseEntity.ok(response);
    }

    // @PathVariable: 從 URL 路徑取得參數，例如 /api/motorcycles/3 → id = 3
    @GetMapping("/motorcycles/{id}")
    public ResponseEntity<MotorcycleDetailResponseDto> getMotorcycle(@PathVariable @Positive(message = "id must be a positive number") Long id) {
        MotorcycleDto dto = motorcycleService.getMotorcycleById(id);
        return ResponseEntity.ok(new MotorcycleDetailResponseDto(dto));
    }
}
