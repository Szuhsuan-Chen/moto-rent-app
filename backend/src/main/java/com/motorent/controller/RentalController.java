package com.motorent.controller;

import com.motorent.dto.RentalCreateResponseDto;
import com.motorent.dto.RentalDto;
import com.motorent.dto.RentalRequestDto;
import com.motorent.dto.UpdateStatusDto;
import com.motorent.service.RentalService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// @Validated: 讓 @RequestParam 上的 @Pattern 驗證生效（失敗時拋出 ConstraintViolationException）
@RestController
@RequestMapping("/api/rentals")
@Validated
public class RentalController {

    @Autowired
    private RentalService rentalService;

    // @Valid: 觸發 RentalRequestDto 上的 @NotBlank/@NotNull 驗證
    // 驗證失敗會拋出 MethodArgumentNotValidException，由 GlobalExceptionHandler 統一處理
    @PostMapping
    public ResponseEntity<Map<String, Object>> createRental(@Valid @RequestBody RentalRequestDto request) {
        RentalCreateResponseDto result = rentalService.createRental(request);
        return ResponseEntity.status(201).body(Map.of(
                "message", "Rental booking successful",
                "data", result
        ));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getRentals(
            @RequestParam(required = false)
            @Pattern(regexp = "^(?:pending|confirmed|completed|cancelled)$", message = "status must be one of: pending, confirmed, completed, cancelled")
            String status,

            @RequestParam(required = false)
            @Pattern(regexp = "^(?:taipei|taichung|tainan)$", message = "branch must be one of: taipei, taichung, tainan")
            String branch,

            @RequestParam(name = "customer_phone", required = false)
            @Pattern(regexp = "^[0-9+\\-\\s]{8,20}$", message = "customer_phone format is invalid")
            String customerPhone
    ) {
        List<RentalDto> data = rentalService.getRentals(status, branch, customerPhone);
        return ResponseEntity.ok(Map.of("count", data.size(), "data", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRental(@PathVariable @Positive(message = "id must be a positive number") Long id) {
        return ResponseEntity.ok(Map.of("data", rentalService.getRentalById(id)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateStatus(
            @PathVariable @Positive(message = "id must be a positive number") Long id,
            @Valid @RequestBody UpdateStatusDto body
    ) {
        rentalService.updateStatus(id, body.getStatus());
        return ResponseEntity.ok(Map.of("message", "Status updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteRental(@PathVariable @Positive(message = "id must be a positive number") Long id) {
        rentalService.deleteRental(id);
        return ResponseEntity.ok(Map.of("message", "Rental record deleted"));
    }
}
