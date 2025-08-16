package ru.practicum.ewm.api.publicapi.events;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.api.privateapi.events.dto.EventFullDto;
import ru.practicum.ewm.api.privateapi.events.dto.EventShortDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/events")
public class EventPublicController {
    private final EventPublicService eventPublicService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEvents(EventFilterRequest filterRequest,
                                         @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                         @RequestParam(name = "size", defaultValue = "10") @Positive int size,
                                         HttpServletRequest request) {
        log.info("GET /events  filterRequest={}, from={}, size={}", filterRequest, from, size);
        List<EventShortDto> events = eventPublicService.getEvents(filterRequest, from, size, request);
        log.info("GET /events result={}", events);
        return events;
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEvent(@PathVariable long eventId, HttpServletRequest request) {
        log.info("GET /events/{}", eventId);
        EventFullDto event = eventPublicService.getEvent(eventId, request);
        log.info("GET /events/{} result={}", eventId, event);
        return event;
    }
}
