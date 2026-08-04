package com.motorent.service;

import com.motorent.dto.MotorcycleDto;
import com.motorent.entity.Motorcycle;
import com.motorent.exception.ResourceNotFoundException;
import com.motorent.repository.MotorcycleMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MotorcycleServiceTest {

    @Mock
    private MotorcycleMapper motorcycleMapper;
    @Mock
    private PricingService pricingService;
    @Mock
    private AvailabilityService availabilityService;

    @InjectMocks
    private MotorcycleService motorcycleService;

    @Test
    void getMotorcycleById_whenFound_returnsDto() {
        // Arrange
        Motorcycle moto = new Motorcycle();
        moto.setId(1L);
        moto.setTitle("Yamaha R3");
        moto.setBrand("Yamaha");
        moto.setPriceCategory("type-a");
        Map<String, Integer> prices = Map.of("5h", 2000, "10h", 3200, "24h", 4000, "48h", 7200);

        when(motorcycleMapper.findById(1L)).thenReturn(moto);
        when(pricingService.getAllPrices("type-a")).thenReturn(prices);

        // Act
        MotorcycleDto result = motorcycleService.getMotorcycleById(1L);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Yamaha R3");
        assertThat(result.getBrand()).isEqualTo("Yamaha");
        assertThat(result.getPrices()).isEqualTo(prices);
    }

    @Test
    void getMotorcycleById_whenNotFound_throwsResourceNotFoundException() {
        // Arrange
        when(motorcycleMapper.findById(999L)).thenReturn(null);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> motorcycleService.getMotorcycleById(999L));
    }

}
