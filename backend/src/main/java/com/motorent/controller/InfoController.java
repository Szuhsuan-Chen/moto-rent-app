package com.motorent.controller;

import com.motorent.service.MotorcycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class InfoController {

    @Autowired
    private MotorcycleService motorcycleService;
    @Autowired
    private DataSource dataSource;  // Spring 自動注入 DB 連線池

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        try (Connection conn = dataSource.getConnection()) {
            return ResponseEntity.ok(Map.of("status", "healthy", "database", "connected"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", "unhealthy", "database", "disconnected"));
        }
    }

    @GetMapping("/brands")
    public ResponseEntity<Map<String, Object>> getBrands() {
        return ResponseEntity.ok(Map.of("data", motorcycleService.findAllBrands()));
    }

    @GetMapping("/types")
    public ResponseEntity<Map<String, Object>> getTypes() {
        return ResponseEntity.ok(Map.of("data", motorcycleService.findAllMotoTypes()));
    }

    @GetMapping("/branches")
    public ResponseEntity<Map<String, Object>> getBranches() {
        List<Map<String, String>> branches = List.of(
                Map.of("id", "taipei",   "name", "台北旗艦店", "address", "台北市信義區信義路五段7號",       "phone", "02-2345-6789", "hours", "10:00 - 22:00"),
                Map.of("id", "taichung", "name", "台中概念店", "address", "台中市西屯區台灣大道三段99號",    "phone", "04-2345-6789", "hours", "10:00 - 22:00"),
                Map.of("id", "tainan",   "name", "台南體驗店", "address", "台南市中西區中正路123號",        "phone", "06-2345-6789", "hours", "10:00 - 22:00")
        );
        return ResponseEntity.ok(Map.of("data", branches));
    }

    @GetMapping("/price-categories")
    public ResponseEntity<Map<String, Object>> getPriceCategories() {
        List<Map<String, Object>> categories = List.of(
                Map.of("id", "type-ss",       "name", "TYPE-SS",       "price", 6000),
                Map.of("id", "type-s",        "name", "TYPE-S",        "price", 5000),
                Map.of("id", "type-a",        "name", "TYPE-A",        "price", 4000),
                Map.of("id", "type-b",        "name", "TYPE-B",        "price", 3000),
                Map.of("id", "type-c",        "name", "TYPE-C",        "price", 2000),
                Map.of("id", "type-minibike", "name", "TYPE-MiniBike", "price", 1000)
        );
        return ResponseEntity.ok(Map.of("data", categories));
    }
}
