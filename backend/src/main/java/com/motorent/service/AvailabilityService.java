package com.motorent.service;

import com.motorent.dto.AvailabilityDto;
import com.motorent.repository.RentalRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class AvailabilityService {

    @Autowired
    private RentalRecordMapper rentalRecordMapper;

    public AvailabilityDto check(Long motorcycleId, String branch, LocalDate date,
                                  String startTime, String duration) {
        if (motorcycleId != null && branch != null && date != null
                && startTime != null && duration != null) {
        LocalDateTime startDatetime = LocalDateTime.of(date, LocalTime.parse(startTime));

        long conflicts = rentalRecordMapper.countConflictingRentals(
                motorcycleId, branch, date, startDatetime
        );

        if (conflicts > 0) {
            return new AvailabilityDto(false, "This time slot is already booked");
        }
        }

        return new AvailabilityDto(true, "Available for rent");
    }
}
