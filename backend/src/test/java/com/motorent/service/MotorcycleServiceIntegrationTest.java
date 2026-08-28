package com.motorent.service;

import com.motorent.dto.MotorcycleDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// 跟 MotorcycleServiceTest 不一樣：這裡沒有 @Mock，MotorcycleMapper 是真的，會真的打 H2 執行 SQL
@SpringBootTest
@Transactional
class MotorcycleServiceIntegrationTest {

    @Autowired
    private MotorcycleService motorcycleService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void findMotorcycles_realDatabase_returnsMatchingMotorcycle() {
        // Arrange：先用 JdbcTemplate 塞一筆真的資料進 H2
        jdbcTemplate.update(
                "INSERT INTO motorcycles (image, title, brand, price_category, moto_type, " +
                        "engine_displacement, max_horsepower, max_torque, engine_type, " +
                        "fuel_tank_capacity, seat_height, weight) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                "yamaha-r3.jpg", "Yamaha R3", "Yamaha", "type-a", "sport",
                321, 42.0, 29.6, "雙缸", 14.0, 780, 169
        );

        // Act：呼叫真正的 service，底層會真的執行 MotorcycleMapper.xml 裡的 SQL
        List<MotorcycleDto> result = motorcycleService.findMotorcycles("type-a", "sport", "Yamaha");

        // Assert
        assertThat(result).hasSize(1);
        MotorcycleDto dto = result.get(0);
        assertThat(dto.getTitle()).isEqualTo("Yamaha R3");
        assertThat(dto.getBrand()).isEqualTo("Yamaha");
        assertThat(dto.getPriceCategory()).isEqualTo("type-a");
        assertThat(dto.getPrice()).isEqualTo(2000); // PricingService 真的計算出來的 type-a 5h 價格
    }

    @Test
    void findMotorcycles_returnsResultsOrderedByPriceCategoryDesc() {
        // Arrange：先用 JdbcTemplate 塞一筆真的資料進 H2
        jdbcTemplate.update(
                "INSERT INTO motorcycles (image, title, brand, price_category, moto_type, " +
                        "engine_displacement, max_horsepower, max_torque, engine_type, " +
                        "fuel_tank_capacity, seat_height, weight) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                "yamaha-r3.jpg", "Yamaha R3", "Yamaha", "type-a", "sport",
                321, 42.0, 29.6, "雙缸", 14.0, 780, 169
        );
        jdbcTemplate.update(
                "INSERT INTO motorcycles (image, title, brand, price_category, moto_type, " +
                        "engine_displacement, max_horsepower, max_torque, engine_type, " +
                        "fuel_tank_capacity, seat_height, weight) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                "KAWASAKI_NINJA_400.png", "KAWASAKI NINJA 400", "KAWASAKI", "type-b", "sport",
                140, 45.0, 38.0, "水冷四行程單汽缸 SOHC 4V", 14.0, 785, 168
        );

        // Act：呼叫真正的 service，底層會真的執行 MotorcycleMapper.xml 裡的 SQL
        List<MotorcycleDto> result = motorcycleService.findMotorcycles(null, null, null);

        // Assert
        assertThat(result).hasSize(2);
        MotorcycleDto higherCategoryDto = result.get(0);
        assertThat(higherCategoryDto.getTitle()).isEqualTo("KAWASAKI NINJA 400");
        assertThat(higherCategoryDto.getBrand()).isEqualTo("KAWASAKI");
        assertThat(higherCategoryDto.getPriceCategory()).isEqualTo("type-b");
        assertThat(higherCategoryDto.getPrice()).isEqualTo(1500); // PricingService 真的計算出來的 type-b 5h 價格
        MotorcycleDto lowerCategoryDto = result.get(1);
        assertThat(lowerCategoryDto.getTitle()).isEqualTo("Yamaha R3");
        assertThat(lowerCategoryDto.getBrand()).isEqualTo("Yamaha");
        assertThat(lowerCategoryDto.getPriceCategory()).isEqualTo("type-a");
        assertThat(lowerCategoryDto.getPrice()).isEqualTo(2000); // PricingService 真的計算出來的 type-a 5h 價格
    }

}
