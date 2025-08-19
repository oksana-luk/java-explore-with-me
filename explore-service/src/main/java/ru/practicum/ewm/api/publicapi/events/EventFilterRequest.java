package ru.practicum.ewm.api.publicapi.events;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventFilterRequest {
    private String text;
    private Boolean paid;
    private List<Long> categories;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeStart;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;
    private Sort sort;
    @Min(0)
    private int from = 0;
    @Positive
    private int size = 10;

    public enum Sort {
        EVENT_DATE, VIEWS;
    }
}
