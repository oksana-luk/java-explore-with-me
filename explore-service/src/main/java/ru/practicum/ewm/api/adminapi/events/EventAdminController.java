package ru.practicum.ewm.api.adminapi.events;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.api.adminapi.events.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.api.privateapi.events.dto.EventFullDto;
import ru.practicum.ewm.api.privateapi.events.model.Event;
import ru.practicum.ewm.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/admin/events")
public class EventAdminController {
    private final EventAdminService eventAdminService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEvents(@RequestParam(name = "users", required = false) List<Long> users,
                                        @RequestParam(name = "states", required = false) List<Event.State> states,
                                        @RequestParam(name = "categories", required = false) List<Long> categories,
                                        @RequestParam(name = "rangeStart", required = false)
                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                        @RequestParam(name = "rangeEnd", required = false)
                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                        @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                        @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("GET /admin/events users={}, states={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size={}",
                users, states, categories, start, end, from, size);
        List<EventFullDto> events = eventAdminService.getEvents(users, states, categories, start, end, from, size);
        log.info("GET /admin/events result={}", events);
        return events;
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest,
                                    @PathVariable long eventId) {
        log.info("PATCH /admin/events/{eventId} eventId={}, updateEventAdminRequest={}", eventId, updateEventAdminRequest);
        LocalDateTime eventDate = updateEventAdminRequest.getEventDate();
        if (Objects.nonNull(eventDate) && eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException(String.format(
                    "Event date should contain future date, current value: %s", eventDate));
        }
        EventFullDto eventFullDto = eventAdminService.updateEvent(updateEventAdminRequest, eventId);
        log.info("PATCH /admin/events/{eventId} result={}", eventFullDto);
        return eventFullDto;
    }
}
