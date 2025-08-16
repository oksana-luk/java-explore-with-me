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
        ParticipantRequest request = new ParticipantRequest(
                0L,
                requester,
                event,
                LocalDateTime.now(),
                ParticipantRequest.Status.PENDING);
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
        if (user != request.getRequester()) {
            throw new ActionConflictException("Action available only for requester.");
        }
        request.setStatus(ParticipantRequest.Status.CANCELED);
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
        return eventRepository.findByIdAndState(eventId, Event.State.PUBLISHED).orElseThrow(() ->
                new NotFoundException(String.format("Event with id %d not found", eventId)));
    }

    private ParticipantRequest validateRequestNotFound(long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Participant request with id %d not found", requestId)));
    }
}
