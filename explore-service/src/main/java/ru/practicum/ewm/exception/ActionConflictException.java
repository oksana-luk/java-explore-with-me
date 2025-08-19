package ru.practicum.ewm.exception;

public class ActionConflictException extends RuntimeException {
    public ActionConflictException(String message) {
        super(message);
    }
}
