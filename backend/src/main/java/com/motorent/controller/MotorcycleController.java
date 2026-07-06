package com.motorent.controller;

import com.motorent.dto.MotorcycleDto;
import com.motorent.service.MotorcycleService;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// @RestController = @Controller + @ResponseBody：所有方法回傳值直接序列化成 JSON
// @Validated: 讓 @RequestParam 上的 @Pattern/@Size 等驗證生效（失敗時拋出 ConstraintViolationException）
@RestController
@RequestMapping("/api")
@Validated
public class MotorcycleController {

    @Autowired
    private MotorcycleService motorcycleService;

    @GetMapping("/motorcycles")
    public ResponseEntity<Map<String, Object>> getMotorcycles(
            @RequestParam(required = false)
            @Pattern(regexp = "^(?:taipei|taichung|tainan)$", message = "branch must be one of: taipei, taichung, tainan")
            String branch,

            @RequestParam(required = false)
            @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "date must be in format yyyy-MM-dd")
            String date,

            @RequestParam(name = "start_time", required = false)
            @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "start_time must be in format HH:mm")
            String startTime,

            @RequestParam(required = false)
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
        List<MotorcycleDto> data = motorcycleService.findMotorcycles(
                branch, date, startTime, duration, priceCategory, motoType, brand
        );

        // 保持 key 順序，維持與原 Flask API 相同的回傳格式
        Map<String, Object> filtersApplied = new LinkedHashMap<>();
        filtersApplied.put("branch", branch);
        filtersApplied.put("date", date);
        filtersApplied.put("start_time", startTime);
        filtersApplied.put("duration", duration);
        filtersApplied.put("price_category", priceCategory);
        filtersApplied.put("moto_type", motoType);
        filtersApplied.put("brand", brand);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("count", data.size());
        response.put("data", data);
        response.put("filters_applied", filtersApplied);

        return ResponseEntity.ok(response);
    }

    // @PathVariable: 從 URL 路徑取得參數，例如 /api/motorcycles/3 → id = 3
    @GetMapping("/motorcycles/{id}")
    public ResponseEntity<Map<String, Object>> getMotorcycle(@PathVariable @Positive(message = "id must be a positive number") Long id) {
        MotorcycleDto dto = motorcycleService.getMotorcycleById(id);
        return ResponseEntity.ok(Map.of("data", dto));
    }
}
