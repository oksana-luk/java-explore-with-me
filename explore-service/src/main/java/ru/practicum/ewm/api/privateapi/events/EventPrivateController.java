package ru.practicum.ewm.api.privateapi.events;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.api.privateapi.events.dto.*;
import ru.practicum.ewm.api.privateapi.requests.dto.ParticipantRequestDto;
import ru.practicum.ewm.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {
    private final EventPrivateService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@Valid @RequestBody NewEventRequest newEventRequest,
                                 @PathVariable long userId) {
        log.info("POST /users/{userId}/events newEventRequest={}, userId={}", newEventRequest, userId);
        LocalDateTime eventDate = newEventRequest.getEventDate();
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException(String.format(
                    "Event date should contain future date, current value: %s", eventDate));
        }
        EventFullDto eventFullDto = eventService.addEvent(newEventRequest, userId);
        log.info("POST /users/{userId}/events result={}", eventFullDto);
        return eventFullDto;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEvents(@PathVariable long userId,
                                  @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                  @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("GET /users/{userId}/events userId={}", userId);

        List<EventShortDto> events = eventService.getEvents(userId, from, size);
        log.info("GET /users/{userId}/events result={}", events);
        return events;
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEvent(@PathVariable long userId,
                                 @PathVariable long eventId) {
        log.info("GET /users/{userId}/events/{eventId} userId={}, eventId={}", userId, eventId);
        EventFullDto event = eventService.getEvent(userId, eventId);
        log.info("GET /users/{userId}/events/{eventId} result={}", event);
        return event;
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@Valid @RequestBody UpdateEventUserRequest updateRequest,
                                    @PathVariable long userId,
                                    @PathVariable long eventId) {
        log.info("PATCH /users/{userId}/events/{eventId} userId={}, eventId={}, updateRequest={}", userId, eventId, updateRequest);
        LocalDateTime eventDate = updateRequest.getEventDate();
        if (Objects.nonNull(eventDate) && eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException(String.format(
                    "Event date should contain future date, current value: %s", eventDate));
        }
        EventFullDto eventFullDto = eventService.updateEvent(updateRequest, userId, eventId);
        log.info("PATCH /users/{userId}/events/{eventId} result={}", eventFullDto);
        return eventFullDto;
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipantRequestDto> getEventRequests(@PathVariable long userId,
                                                        @PathVariable long eventId) {
        log.info("GET /users/{userId}/events/{eventId}/requests userId={}, eventId={}", userId, eventId);
        List<ParticipantRequestDto> requests = eventService.getEventRequests(userId, eventId);
        log.info("GET /users/{userId}/events/{eventId}/requests result={}", requests);
        return requests;
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateEventRequests(@Valid @RequestBody EventRequestStatusUpdateRequest updateRequest,
                                                                @PathVariable long userId,
                                                                @PathVariable long eventId) {
        log.info("PATCH /users/{userId}/events/{eventId}/requests userId={}, eventId={}, updateRequest={}", userId, eventId, updateRequest);
        EventRequestStatusUpdateResult result = eventService.updateRequestsByEvent(updateRequest, userId, eventId);
        log.info("PATCH /users/{userId}/events/{eventId}/requests result={}", result);
        return result;
    }
}
