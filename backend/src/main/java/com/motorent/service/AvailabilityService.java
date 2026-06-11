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

    public AvailabilityDto check(Long motorcycleId, String branch, String date,
                                  String startTime, String duration) {
        if (date != null) {
            try {
                if (LocalDate.parse(date).isBefore(LocalDate.now())) {
                    return AvailabilityDto.of(false, "Selected date has expired");
                }
            } catch (Exception e) {
                return AvailabilityDto.of(false, "Incorrect date format");
            }
        }

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
            LocalDate rentalDate = LocalDate.parse(date);
            LocalDateTime startDatetime = LocalDateTime.of(rentalDate, LocalTime.parse(startTime));

            long conflicts = rentalRecordMapper.countConflictingRentals(
                    motorcycleId, branch, rentalDate, startDatetime
            );

            if (conflicts > 0) {
                return AvailabilityDto.of(false, "This time slot is already booked");
            }
        }

        return AvailabilityDto.of(true, "Available for rent");
    }
}
