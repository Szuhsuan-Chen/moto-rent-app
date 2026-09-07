package com.motorent.service;

import org.springframework.stereotype.Service;

import java.util.Map;

// @Service: 標記為業務邏輯層，Spring 會自動管理這個 Bean
@Service
public class PricingService {

    // 價格對照表：車型類別 → (時長 → 價格)
    private static final Map<String, Map<String, Integer>> PRICE_MAP = Map.of(
            "type-ss",       Map.of("5h", 3000, "10h", 4800, "24h", 6000,  "48h", 10800),
            "type-s",        Map.of("5h", 2500, "10h", 4000, "24h", 5000,  "48h", 9000),
            "type-a",        Map.of("5h", 2000, "10h", 3200, "24h", 4000,  "48h", 7200),
            "type-b",        Map.of("5h", 1500, "10h", 2400, "24h", 3000,  "48h", 5400),
            "type-c",        Map.of("5h", 1000, "10h", 1600, "24h", 2000,  "48h", 3600),
            "type-minibike", Map.of("5h", 500,  "10h", 800,  "24h", 1000,  "48h", 1800)
    );

    public Integer getPrice(String priceCategory, String duration) {
        Map<String, Integer> prices = PRICE_MAP.get(priceCategory);
        if (prices == null) return null;
        // 有指定時長就用對應價格，否則預設顯示最低的 5h 起始價
        return (duration != null && prices.containsKey(duration))
                ? prices.get(duration)
                : prices.get("5h");
    }

    public Map<String, Integer> getAllPrices(String priceCategory) {
        return PRICE_MAP.get(priceCategory);
    }

    public boolean isValidCategory(String category) {
        return PRICE_MAP.containsKey(category);
    }
}
