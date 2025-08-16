package ru.practicum.ewm.api.privateapi.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.api.privateapi.requests.dto.ParticipantRequestDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
public class ParticipantRequestController {
    private final ParticipantRequestService requestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipantRequestDto> getRequests(@PathVariable long userId) {
        log.info("GET /users/{userId}/requests/ userId={}", userId);
        List<ParticipantRequestDto> requestDtos = requestService.getRequests(userId);
        log.info("GET /admin/requestDtos result={}", requestDtos);
        return requestDtos;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipantRequestDto addRequest(@PathVariable long userId,
                                            @RequestParam(name = "eventId", required = true) long eventId) {
        log.info("POST /users/{userId}/requests userId={}, eventId={}", userId, eventId);
        ParticipantRequestDto requestDto = requestService.addRequest(userId, eventId);
        log.info("POST /users/{userId}/requests result={}", requestDto);
        return requestDto;
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipantRequestDto cancelRequest(@PathVariable long userId,
                                               @PathVariable long requestId) {
        log.info("PATCH /users/{userId}/requests/{requestId}/cancel userId={}, requestId={}", userId, requestId);
        ParticipantRequestDto requestDto = requestService.cancelRequest(userId, requestId);
        log.info("PATCH users/{userId}/requests/{requestId}/cancel result={}", requestDto);
        return requestDto;
    }
}
