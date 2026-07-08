package com.motorent.service;

import com.motorent.dto.AvailabilityDto;
import com.motorent.repository.RentalRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AvailabilityService {

    @Autowired
    private RentalRecordMapper rentalRecordMapper;
    @Autowired
    private PricingService pricingService;

    private static final List<String> VALID_BRANCHES = List.of("taipei", "taichung", "tainan");

    public AvailabilityDto check(Long motorcycleId, String branch, LocalDate date,
                                  String startTime, String duration) {
        if (branch != null && !VALID_BRANCHES.contains(branch)) {
            return AvailabilityDto.of(false, "Invalid branch selection");
        }

        if (startTime != null) {
            try {
                LocalTime.parse(startTime);
            } catch (Exception e) {
                return AvailabilityDto.of(false, "Incorrect time format");
            }
        }

        if (duration != null && !pricingService.isValidDuration(duration)) {
            return AvailabilityDto.of(false, "Invalid rental duration");
        }

        if (motorcycleId != null && branch != null && date != null
                && startTime != null && duration != null) {
            LocalDateTime startDatetime = LocalDateTime.of(date, LocalTime.parse(startTime));

            long conflicts = rentalRecordMapper.countConflictingRentals(
                    motorcycleId, branch, date, startDatetime
            );

            if (conflicts > 0) {
                return AvailabilityDto.of(false, "This time slot is already booked");
            }
        }

        return AvailabilityDto.of(true, "Available for rent");
    }
}
