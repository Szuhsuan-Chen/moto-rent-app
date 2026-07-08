package com.motorent.service;

import com.motorent.dto.AvailabilityDto;
import com.motorent.dto.FilterInfoDto;
import com.motorent.dto.MotorcycleDto;
import com.motorent.dto.MotorcycleFilterParams;
import com.motorent.entity.Motorcycle;
import com.motorent.exception.ResourceNotFoundException;
import com.motorent.repository.MotorcycleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MotorcycleService {

    @Autowired
    private MotorcycleMapper motorcycleMapper;
    @Autowired
    private AvailabilityService availabilityService;
    @Autowired
    private PricingService pricingService;

    public List<MotorcycleDto> findMotorcycles(String branch, LocalDate date, String startTime,
                                               String duration, String priceCategory,
                                               String motoType, String brand) {
        // 把篩選參數包成物件傳給 MyBatis XML 動態查詢
        MotorcycleFilterParams params = new MotorcycleFilterParams(priceCategory, motoType, brand);
        List<Motorcycle> motorcycles = motorcycleMapper.findByFilters(params);

        return motorcycles.stream()
                .map(moto -> toListDto(moto, branch, date, startTime, duration))
                .toList();
    }

    public MotorcycleDto getMotorcycleById(Long id) {
        Motorcycle moto = motorcycleMapper.findById(id);
        if (moto == null) throw new ResourceNotFoundException("Motorcycle not found");

        return MotorcycleDto.builder()
                .id(moto.getId())
                .image(moto.getImage())
                .title(moto.getTitle())
                .brand(moto.getBrand())
                .priceCategory(moto.getPriceCategory())
                .motoType(moto.getMotoType())
                .engineDisplacement(moto.getEngineDisplacement())
                .maxHorsepower(moto.getMaxHorsepower())
                .maxTorque(moto.getMaxTorque())
                .engineType(moto.getEngineType())
                .fuelTankCapacity(moto.getFuelTankCapacity())
                .seatHeight(moto.getSeatHeight())
                .weight(moto.getWeight())
                .prices(pricingService.getAllPrices(moto.getPriceCategory()))
                .build();
    }

    public List<String> findAllBrands() {
        return motorcycleMapper.findDistinctBrands();
    }

    public List<String> findAllMotoTypes() {
        return motorcycleMapper.findDistinctMotoTypes();
    }

    private MotorcycleDto toListDto(Motorcycle moto, String branch, LocalDate date,
                                    String startTime, String duration) {
        AvailabilityDto availability = availabilityService.check(
                moto.getId(), branch, date, startTime, duration
        );

        return MotorcycleDto.builder()
                .id(moto.getId())
                .image(moto.getImage())
                .title(moto.getTitle())
                .brand(moto.getBrand())
                .priceCategory(moto.getPriceCategory())
                .price(pricingService.getPrice(moto.getPriceCategory(), duration))
                .motoType(moto.getMotoType())
                .engineDisplacement(moto.getEngineDisplacement())
                .maxHorsepower(moto.getMaxHorsepower())
                .maxTorque(moto.getMaxTorque())
                .engineType(moto.getEngineType())
                .fuelTankCapacity(moto.getFuelTankCapacity())
                .seatHeight(moto.getSeatHeight())
                .weight(moto.getWeight())
                .availability(availability)
                .filterInfo(FilterInfoDto.builder()
                        .branch(branch)
                        .date(date == null ? null : date.toString())
                        .startTime(startTime)
                        .duration(duration)
                        .build())
                .build();
    }
}
