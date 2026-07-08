package com.motorent.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

// @RestControllerAdvice: 全域例外處理器
// 面試考點：為什麼用這個？讓所有 Controller 不需要各自寫 try-catch，統一在這裡處理
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(BadRequestException e) {
        return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
    }

    // required = true 的 @RequestParam 沒有傳進來時觸發
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParam(MissingServletRequestParameterException e) {
        return ResponseEntity.status(400).body(Map.of("error", e.getParameterName() + " is required"));
    }

    // @Validated + @Pattern 在 @RequestParam 驗證失敗時觸發
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(cv -> cv.getMessage())
                .findFirst()
                .orElse("Validation failed");
        return ResponseEntity.status(400).body(Map.of("error", message));
    }

    // @PathVariable/@RequestParam 型別轉換失敗時觸發（例如 /api/motorcycles/abc，id 應為 Long；
    // 現在 date 也是 LocalDate，格式錯誤如 2026/13/40 一樣會落到這裡）
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.status(400).body(Map.of("error", e.getName() + " has invalid format"));
    }

    // @Valid 驗證失敗時觸發（例如 @NotBlank 欄位為空、@FutureOrPresent 日期是過去式）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        return ResponseEntity.status(400).body(Map.of("error", message));
    }

    // JSON 反序列化失敗時觸發（例如 rental_date 傳成 "abc" 這種 LocalDate 解析不了的格式，
    // 或整個 request body 不是合法 JSON）
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleMessageNotReadable(HttpMessageNotReadableException e) {
        return ResponseEntity.status(400).body(Map.of("error", "Request body is malformed"));
    }

    // 其他Exception，統一回傳 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception e) {
        log.error("Unhandled exception", e);
        return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
}
