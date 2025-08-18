package ru.practicum.ewm.exception;

import org.hibernate.exception.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;

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

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException ex) {
        Map<String, String> errors = getErrorDescription(HttpStatus.NOT_FOUND,
                "The required object was not found.",
                ex.getMessage());
        log.warn("Handling NotFoundException, e.message={}", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ActionConflictException.class)
    public ResponseEntity<Map<String, String>> handleActionConflictException(ActionConflictException ex) {
        Map<String, String> errors = getErrorDescription(HttpStatus.CONFLICT,
                "For the requested operation the conditions are not met.",
                ex.getMessage());
        log.warn("Handling ActionConflictException, e.message={}", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
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

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Handling Constraint violation exception of jakarta, e.message={}", ex.getMessage());
        Map<String, String> errors = getErrorDescription(HttpStatus.BAD_REQUEST,
                "Internal server error",
                ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, String>> handleHttpStatusCodeException(HttpStatusCodeException ex) {
        Map<String, String> errors = getErrorDescription(HttpStatus.BAD_REQUEST,
                "Internal server error",
                ex.getMessage());
        log.warn("Handling HttpStatusCodeException, code={}, message={}", ex.getStatusCode(), ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Map<String, String> errors = getErrorDescription(HttpStatus.CONFLICT,
                "Integrity constraint has been violated.",
                ex.getMessage());
        log.warn("Handling DataIntegrityViolationException, e.message={}", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
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

