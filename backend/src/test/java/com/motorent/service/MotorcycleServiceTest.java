package com.motorent.service;

import com.motorent.dto.MotorcycleDto;
import com.motorent.dto.MotorcycleFilterParams;
import com.motorent.entity.Motorcycle;
import com.motorent.exception.ResourceNotFoundException;
import com.motorent.repository.MotorcycleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
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

    private Motorcycle r3Moto;

    @BeforeEach
    void setUp() {
        r3Moto = new Motorcycle();
        r3Moto.setId(1L);
        r3Moto.setImage("yamaha-r3.jpg");
        r3Moto.setTitle("Yamaha R3");
        r3Moto.setBrand("Yamaha");
        r3Moto.setPriceCategory("type-a");
        r3Moto.setMotoType("sport");
        r3Moto.setEngineDisplacement(321);
        r3Moto.setMaxHorsepower(42.0);
        r3Moto.setMaxTorque(29.6);
        r3Moto.setEngineType("雙缸");
        r3Moto.setFuelTankCapacity(14.0);
        r3Moto.setSeatHeight(780);
        r3Moto.setWeight(169);
    }

    @Test
    void getMotorcycleById_whenFound_returnsDto() {
        // Arrange
        Map<String, Integer> prices = Map.of("5h", 2000, "10h", 3200, "24h", 4000, "48h", 7200);

        when(motorcycleMapper.findById(1L)).thenReturn(r3Moto);
        when(pricingService.getAllPrices("type-a")).thenReturn(prices);

        // Act
        MotorcycleDto result = motorcycleService.getMotorcycleById(1L);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getImage()).isEqualTo("yamaha-r3.jpg");
        assertThat(result.getTitle()).isEqualTo("Yamaha R3");
        assertThat(result.getBrand()).isEqualTo("Yamaha");
        assertThat(result.getPriceCategory()).isEqualTo("type-a");
        assertThat(result.getMotoType()).isEqualTo("sport");
        assertThat(result.getEngineDisplacement()).isEqualTo(321);
        assertThat(result.getMaxHorsepower()).isEqualTo(42.0);
        assertThat(result.getMaxTorque()).isEqualTo(29.6);
        assertThat(result.getEngineType()).isEqualTo("雙缸");
        assertThat(result.getFuelTankCapacity()).isEqualTo(14.0);
        assertThat(result.getSeatHeight()).isEqualTo(780);
        assertThat(result.getWeight()).isEqualTo(169);
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

    @Test
    void findAllBrands_returnsMapperResultDirectly() {
        // Arrange
        List<String> list = new ArrayList<>();
        list.add("YAMAHA");
        list.add("HONDA");
        list.add("KAWASAKI");
        when(motorcycleMapper.findDistinctBrands()).thenReturn(list);

        // Act
        List<String> resultList = motorcycleService.findAllBrands();

        // Assert
        assertThat(resultList).isEqualTo(list);
    }

    @Test
    void findMotorcycles_returnsMapperResultDirectly() {
        // Arrange
        MotorcycleFilterParams params = new MotorcycleFilterParams("type-a", "sport", "YAMAHA");
        Motorcycle r7Moto = new Motorcycle();
        r7Moto.setId(2L);
        r7Moto.setTitle("Yamaha R7");
        r7Moto.setBrand("Yamaha");
        r7Moto.setPriceCategory("type-a");
        r7Moto.setMotoType("sport");
        r7Moto.setEngineDisplacement(689);
        r7Moto.setMaxHorsepower(73.0);
        r7Moto.setMaxTorque(67.0);
        r7Moto.setEngineType("水冷四行程並列雙汽缸 DOHC 4V");
        r7Moto.setFuelTankCapacity(13.0);
        r7Moto.setSeatHeight(835);
        r7Moto.setWeight(188);
        List<Motorcycle> motorcycles = new ArrayList<>();
        motorcycles.add(r3Moto);
        motorcycles.add(r7Moto);
        when(motorcycleMapper.findByFilters(params)).thenReturn(motorcycles);
        when(pricingService.getPrice("type-a", null)).thenReturn(2000);

        MotorcycleDto expectedR3Dto = MotorcycleDto.builder()
                .id(1L)
                .image("yamaha-r3.jpg")
                .title("Yamaha R3")
                .brand("Yamaha")
                .priceCategory("type-a")
                .price(2000)
                .motoType("sport")
                .engineDisplacement(321)
                .maxHorsepower(42.0)
                .maxTorque(29.6)
                .engineType("雙缸")
                .fuelTankCapacity(14.0)
                .seatHeight(780)
                .weight(169)
                .build();
        MotorcycleDto expectedR7Dto = MotorcycleDto.builder()
                .id(2L)
                .title("Yamaha R7")
                .brand("Yamaha")
                .priceCategory("type-a")
                .price(2000)
                .motoType("sport")
                .engineDisplacement(689)
                .maxHorsepower(73.0)
                .maxTorque(67.0)
                .engineType("水冷四行程並列雙汽缸 DOHC 4V")
                .fuelTankCapacity(13.0)
                .seatHeight(835)
                .weight(188)
                .build();

        // Act
        List<MotorcycleDto> motorcycleDtos = motorcycleService.findMotorcycles("type-a", "sport", "YAMAHA");

        // Assert
        assertThat(motorcycleDtos).containsExactly(expectedR3Dto, expectedR7Dto);
    }

    @Test
    void findMotorcycles_whenNotFound_returnsEmptyList() {
        // Arrange
        MotorcycleFilterParams params = new MotorcycleFilterParams("type-minibike", "sport", "YAMAHA");
        List<Motorcycle> motorcycles = new ArrayList<>();
        when(motorcycleMapper.findByFilters(params)).thenReturn(motorcycles);

        // Act
        List<MotorcycleDto> motorcycleDtos = motorcycleService.findMotorcycles("type-minibike", "sport", "YAMAHA");

        // Assert
        assertThat(motorcycleDtos).isEmpty();
    }

}
