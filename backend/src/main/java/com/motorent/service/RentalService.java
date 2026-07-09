package com.motorent.service;

import com.motorent.dto.*;
import com.motorent.entity.Motorcycle;
import com.motorent.entity.RentalRecord;
import com.motorent.exception.BadRequestException;
import com.motorent.exception.ResourceNotFoundException;
import com.motorent.repository.MotorcycleMapper;
import com.motorent.repository.RentalRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Service
public class RentalService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter FULL_DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Set<String> VALID_STATUSES = Set.of("pending", "confirmed", "completed", "cancelled");

    @Autowired
    private RentalRecordMapper rentalRecordMapper;
    @Autowired
    private MotorcycleMapper motorcycleMapper;
    @Autowired
    private AvailabilityService availabilityService;
    @Autowired
    private PricingService pricingService;

    @Transactional
    public RentalCreateResponseDto createRental(RentalRequestDto request) {
        AvailabilityDto availability = availabilityService.check(
                request.getMotorcycleId(), request.getBranch(),
                request.getRentalDate(), request.getStartTime(), request.getDuration()
        );
        if (!availability.isAvailable()) {
            throw new BadRequestException(availability.getMessage());
        }

        Motorcycle motorcycle = motorcycleMapper.findById(request.getMotorcycleId());
        if (motorcycle == null) throw new ResourceNotFoundException("Motorcycle not found");

        String priceCategory = motorcycle.getPriceCategory();
        if (!pricingService.isValidCategory(priceCategory)) {
            throw new BadRequestException("Invalid motorcycle price category");
        }
        Integer totalPrice = pricingService.getPrice(priceCategory, request.getDuration());

        LocalDate rentalDate = request.getRentalDate();
        LocalTime startTime = LocalTime.parse(request.getStartTime());
        int durationHours = Integer.parseInt(request.getDuration().replace("h", ""));
        LocalDateTime endDatetime = LocalDateTime.of(rentalDate, startTime).plusHours(durationHours);

        RentalRecord rental = new RentalRecord();
        rental.setMotorcycleId(request.getMotorcycleId());  // MyBatis 版：只存 FK，不是物件
        rental.setCustomerName(request.getCustomerName());
        rental.setCustomerPhone(request.getCustomerPhone());
        rental.setCustomerEmail(request.getCustomerEmail());
        rental.setBranch(request.getBranch());
        rental.setRentalDate(rentalDate);
        rental.setStartTime(startTime);
        rental.setDuration(request.getDuration());
        rental.setEndDatetime(endDatetime);
        rental.setTotalPrice(BigDecimal.valueOf(totalPrice));
        rental.setStatus("confirmed");
        rental.setNotes(request.getNotes());

        rentalRecordMapper.insert(rental);  // @Options(useGeneratedKeys) 會把 id 寫回 rental

        return RentalCreateResponseDto.builder()
                .rentalId(rental.getId())
                .totalPrice(totalPrice)
                .endDatetime(endDatetime.format(DATETIME_FMT))
                .build();
    }

    public List<RentalDto> getRentals(String status, String branch, String customerPhone) {
        return rentalRecordMapper.findByFilters(new RentalFilterParams(status, branch, customerPhone))
                .stream().map(this::toDto).toList();
    }

    public RentalDto getRentalById(Long id) {
        RentalRecord record = rentalRecordMapper.findByIdWithMotorcycle(id);
        if (record == null) throw new ResourceNotFoundException("Rental record not found");
        return toDto(record);
    }

    @Transactional
    public void updateStatus(Long id, String status) {
        if (!VALID_STATUSES.contains(status)) {
            throw new BadRequestException("Invalid status value");
        }
        // MyBatis 版：直接呼叫 UPDATE，回傳影響行數
        // JPA 版是 dirty checking，MyBatis 需要明確呼叫
        int rows = rentalRecordMapper.updateStatus(id, status);
        if (rows == 0) {
            throw new ResourceNotFoundException("Rental record not found");
        }
    }

    @Transactional
    public void deleteRental(Long id) {
        int rows = rentalRecordMapper.deleteById(id);
        if (rows == 0) {
            throw new ResourceNotFoundException("Rental record not found");
        }
    }

    private RentalDto toDto(RentalRecord r) {
        return RentalDto.builder()
                .id(r.getId())
                .motorcycleId(r.getMotorcycleId())
                .motorcycleTitle(r.getMotorcycleTitle())
                .motorcycleBrand(r.getMotorcycleBrand())
                .customerName(r.getCustomerName())
                .customerPhone(r.getCustomerPhone())
                .customerEmail(r.getCustomerEmail())
                .branch(r.getBranch())
                .rentalDate(r.getRentalDate().format(DATE_FMT))
                .startTime(r.getStartTime().format(TIME_FMT))
                .duration(r.getDuration())
                .endDatetime(r.getEndDatetime().format(DATETIME_FMT))
                .totalPrice(r.getTotalPrice())
                .status(r.getStatus())
                .notes(r.getNotes())
                .createdAt(r.getCreatedAt().format(FULL_DATETIME_FMT))
                .updatedAt(r.getUpdatedAt().format(FULL_DATETIME_FMT))
                .build();
    }
}
