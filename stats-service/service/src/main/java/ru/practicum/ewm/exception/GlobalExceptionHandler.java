package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errorText = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String errorMessage = error.getDefaultMessage();
            errorText.append(errorMessage);
            errorText.append(".");
        });
        Map<String, String> errors = getErrorDescription(HttpStatus.BAD_REQUEST,
                "Incorrectly made request",
                errorText.toString());
        log.warn("Handling MethodArgumentNotValidException, e.message={}", errorText);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.hibernate.exception.ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Handling Constraint violation exception of jakarta, e.message={}", ex.getMessage());
        Map<String, String> errors = getErrorDescription(HttpStatus.BAD_REQUEST,
                "Internal server error",
                ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleCustomValidationException(ValidationException ex) {
        Map<String, String> errors = getErrorDescription(HttpStatus.BAD_REQUEST,
                "Incorrectly made request",
                ex.getMessage());
        log.warn("Handling (custom) Validation Exception, e.message={}", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleInternalServerError(Exception ex) {
        log.warn("Handling Internal Server Error, e.message={}", ex.getMessage());
        Map<String, String> errors = getErrorDescription(HttpStatus.BAD_REQUEST,
                "Internal server error",
                ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    private Map<String, String> getErrorDescription(HttpStatus status, String reason, String message) {
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put("status", status.name());
        errors.put("reason", reason);
        errors.put("message", message);
        errors.put("timestamp", LocalDateTime.now().format(DATE_TIME_FORMATTER));
        return errors;
    }
}


