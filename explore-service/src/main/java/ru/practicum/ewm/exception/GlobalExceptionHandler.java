package ru.practicum.ewm.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String errorMessage = error.getDefaultMessage();
            errors.put("error", errorMessage);
        });
        log.warn("Handling MethodArgumentNotValidException, e.message={}", errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException ex) {
        Map<String, String> errors = getErrorDescription(HttpStatus.NOT_FOUND,
                "The required object was not found.",
                ex.getMessage());
        log.warn("Handling NotFoundException, e.message={}", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleCustomValidationException(ValidationException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        log.warn("Handling (custom) Validation Exception, e.message={}", ex.getMessage());

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleConstraintViolationException(ConstraintViolationException e) {
        return Map.of("Validation errors", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus()
    public ResponseEntity<Object> handleHttpStatusCodeException(HttpStatusCodeException ex) {
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(ex.getStatusCode());
        log.warn("Handling HttpStatusCodeException, e.code={}, body={}", ex.getStatusCode(), builder.body(ex.getResponseBodyAsByteArray()));
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsByteArray());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleInternalServerError(Exception e) {
        log.warn("Handling Internal Server Error, e.message={}", e.getMessage());
        return Map.of("Internal server error", e.getMessage());
    }

    private Map<String, String> getErrorDescription(HttpStatus status, String reason, String message) {
        Map<String, String> errors = new HashMap<>();
        errors.put("status", status.name());
        errors.put("reason", reason);
        errors.put("message", message);
        errors.put("timestamp", LocalDateTime.now().format(DATE_TIME_FORMATTER));
        return errors;
    }
}

