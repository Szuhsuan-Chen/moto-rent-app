package com.motorent.exception;

// RuntimeException 不需要 try-catch，Spring MVC 會往上傳給 @ControllerAdvice 處理
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
