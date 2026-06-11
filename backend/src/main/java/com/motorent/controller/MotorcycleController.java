package com.motorent.controller;

import com.motorent.dto.MotorcycleDto;
import com.motorent.service.MotorcycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// @RestController = @Controller + @ResponseBody：所有方法回傳值直接序列化成 JSON
@RestController
@RequestMapping("/api")
public class MotorcycleController {

    @Autowired
    private MotorcycleService motorcycleService;

    @GetMapping("/motorcycles")
    public ResponseEntity<Map<String, Object>> getMotorcycles(
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String date,
            @RequestParam(name = "start_time", required = false) String startTime,
            @RequestParam(required = false) String duration,
            @RequestParam(name = "price_category", required = false) String priceCategory,
            @RequestParam(name = "moto_type", required = false) String motoType,
            @RequestParam(required = false) String brand
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
    public ResponseEntity<Map<String, Object>> getMotorcycle(@PathVariable Long id) {
        MotorcycleDto dto = motorcycleService.getMotorcycleById(id);
        return ResponseEntity.ok(Map.of("data", dto));
    }
}
