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
        int durationHours = Integer.parseInt(duration.replace("h", ""));
        LocalDateTime endDatetime = startDatetime.plusHours(durationHours);

        // 雙向區間重疊判斷：既有訂單的開始 < 新訂單的結束，且既有訂單的結束 > 新訂單的開始
        long conflicts = rentalRecordMapper.countConflictingRentals(
                motorcycleId, branch, startDatetime, endDatetime
        );

        if (conflicts > 0) {
            return new AvailabilityDto(false, "This time slot is already booked");
        }

        return new AvailabilityDto(true, "Available for rent");
    }
}
