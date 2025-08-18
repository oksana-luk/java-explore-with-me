package ru.practicum.ewm.api.privateapi.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.api.adminapi.user.UserRepository;
import ru.practicum.ewm.api.adminapi.user.model.User;
import ru.practicum.ewm.api.privateapi.events.EventRepository;
import ru.practicum.ewm.api.privateapi.events.model.Event;
import ru.practicum.ewm.api.privateapi.requests.dto.ParticipantRequestCount;
import ru.practicum.ewm.api.privateapi.requests.dto.ParticipantRequestDto;
import ru.practicum.ewm.api.privateapi.requests.model.ParticipantRequest;
import ru.practicum.ewm.exception.ActionConflictException;
import ru.practicum.ewm.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipantRequestServiceImpl implements ParticipantRequestService {
    private final ParticipantRequestRepository requestRepository;
    private final ParticipantRequestMapper requestMapper;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public ParticipantRequestDto addRequest(long userId, long eventId) {
        User requester = validateUserNotFound(userId);
        Event event = validateEventNotFound(eventId);
        validateRequesterIsInitiator(requester, event.getInitiator());
        validateEventStatePublished(event);
        if (event.getParticipantLimit() > 0) {
            Map<Long, Integer> confirmedRequests = getCountOfConfirmedRequestsByEvents(List.of(event));
            log.info("Participant request private service, adding request by event: confirmedRequests={}", confirmedRequests);
            validateParticipantLimitReached(event, confirmedRequests.getOrDefault(event.getId(), 0));
        }

        ParticipantRequest request = new ParticipantRequest(
                0L,
                requester,
                event,
                LocalDateTime.now(),
                (!event.isRequestModeration() || event.getParticipantLimit() == 0) ?
                        ParticipantRequest.Status.CONFIRMED : ParticipantRequest.Status.PENDING);
        log.info("Participant request private service, adding request: request={}", request);
        request = requestRepository.save(request);
        return requestMapper.mapParticipantRequestToParticipantRequestDto(request);
    }

    @Override
    public List<ParticipantRequestDto> getRequests(long userId) {
        validateUserNotFound(userId);
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(requestMapper::mapParticipantRequestToParticipantRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public ParticipantRequestDto cancelRequest(long userId, long requestId) {
        User user = validateUserNotFound(userId);
        ParticipantRequest request = validateRequestNotFound(requestId);
        validateUserIsRequester(user, request.getRequester());
        request.setStatus(ParticipantRequest.Status.CANCELED);
        log.info("Participant request private service, canceling request: request={}", request);
        return requestMapper.mapParticipantRequestToParticipantRequestDto(requestRepository.save(request));
    }

    public Map<Long, Integer> getCountOfConfirmedRequestsByEvents(List<Event> events) {
        List<ParticipantRequestCount> requestCountList = requestRepository.findRequestCountsByEventsAndStatus(events,
                ParticipantRequest.Status.CONFIRMED);

        Map<Long, Integer> result = new HashMap<>();
        requestCountList.forEach(event -> result.put(event.getEventId(), event.getCountOfRequests()));
        return result;
    }

    private User validateUserNotFound(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id %d not found", userId)));
    }

    private Event validateEventNotFound(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event with id %d not found", eventId)));
    }

    private ParticipantRequest validateRequestNotFound(long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Participant request with id %d not found", requestId)));
    }

    private void validateRequesterIsInitiator(User requester, User initiator) {
        if (requester == initiator) {
            throw new ActionConflictException("Initiator should not be participant of event");
        }
    }

    private void validateUserIsRequester(User user, User requester) {
        if (user != requester) {
            throw new ActionConflictException("Action available only for requester.");
        }
    }

    private void validateEventStatePublished(Event event) {
        if (!event.getState().equals(Event.State.PUBLISHED)) {
            throw new ActionConflictException(String.format("Action available only in state PUBLISHED. Current state: %s",
                    event.getState()));
        }
    }

    private void validateParticipantLimitReached(Event event, int countConfirmed) {
        if (countConfirmed == event.getParticipantLimit()) {
            throw new ActionConflictException(String.format("The participant limit for event with id %d has been reached",
                    event.getId()));
        }
    }
}
