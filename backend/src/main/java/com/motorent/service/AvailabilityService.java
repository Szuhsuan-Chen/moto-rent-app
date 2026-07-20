package com.motorent.service;

import com.motorent.dto.AvailabilityDto;
import com.motorent.repository.RentalRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AvailabilityService {

    @Autowired
    private RentalRecordMapper rentalRecordMapper;

    public AvailabilityDto check(Long motorcycleId, String branch, LocalDateTime startDatetime,
                                  String duration) {
        long conflicts = rentalRecordMapper.countConflictingRentals(
                motorcycleId, branch, startDatetime.toLocalDate(), startDatetime
        );

        if (conflicts > 0) {
            return new AvailabilityDto(false, "This time slot is already booked");
        }

        return new AvailabilityDto(true, "Available for rent");
    }
}
