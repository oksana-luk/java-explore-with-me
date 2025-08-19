package ru.practicum.ewm.api.privateapi.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.api.adminapi.categories.CategoryRepository;
import ru.practicum.ewm.api.adminapi.categories.model.Category;
import ru.practicum.ewm.api.adminapi.user.UserRepository;
import ru.practicum.ewm.api.adminapi.user.model.User;
import ru.practicum.ewm.api.privateapi.events.dto.*;
import ru.practicum.ewm.api.privateapi.events.model.Event;
import ru.practicum.ewm.api.privateapi.requests.ParticipantRequestMapper;
import ru.practicum.ewm.api.privateapi.requests.ParticipantRequestRepository;
import ru.practicum.ewm.api.privateapi.requests.ParticipantRequestService;
import ru.practicum.ewm.api.privateapi.requests.dto.ParticipantRequestDto;
import ru.practicum.ewm.api.privateapi.requests.model.ParticipantRequest;
import ru.practicum.ewm.exception.ActionConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.statistic.StatisticService;

import java.util.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventPrivateServiceImpl implements EventPrivateService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final StatisticService statisticService;
    private final ParticipantRequestService requestService;
    private final ParticipantRequestRepository requestRepository;
    private final ParticipantRequestMapper requestMapper;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public EventFullDto addEvent(NewEventRequest newEventRequest, long userId) {
        User initiator = validateUserNotFound(userId);
        Category category = validateCategoryNotFound(newEventRequest.getCategory());

        Event event = eventMapper.mapNewEventRequestToEvent(newEventRequest, initiator, category);
        log.info("Event private service, adding event: event={}", event);

        event = eventRepository.save(event);
        log.info("Event private service, adding event: result={}", event);

        EventFullDto eventFullDto = eventMapper.mapEventToEventFullDto(event, 0, 0);
        log.info("Event private service, adding event: eventFullDto={}", eventFullDto);

        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getEvents(long userId, int from, int size) {
        validateUserNotFound(userId);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        log.info("Event private service, getting events: page={}", page);

        Page<Event> eventsPage = eventRepository.findAllByInitiatorId(userId, page);
        List<Event> events = eventsPage.getContent();
        log.info("Event private service, getting events: result={}", events);
        if (events.isEmpty()) {
            return List.of();
        }

        Map<Long, Long> statistic = statisticService.getStatsByEvents(events, false);
        Map<Long, Integer> confirmedRequests = requestService.getCountOfConfirmedRequestsByEvents(events);
        log.info("Event private service, getting events: statistic={}, confirmedRequests={}",
                statistic, confirmedRequests);

        return events.stream()
                .map(event -> eventMapper.mapEventToEventShortDto(
                        event,
                        confirmedRequests.getOrDefault(event.getId(), 0),
                        statistic.getOrDefault(event.getId(), 0L)))
                .toList();
    }

    @Override
    public EventFullDto getEvent(long userId, long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event with id %d not found", eventId)));
        log.info("Event private service, getting event by id: eventId={}", eventId);

        Map<Long, Long> statistic = statisticService.getStatsByEvents(List.of(event), false);
        Map<Long, Integer> confirmedRequests = new HashMap<>();
        if (event.isRequestModeration()) {
            confirmedRequests = requestService.getCountOfConfirmedRequestsByEvents(List.of(event));
        }
        log.info("Event private service, getting event by id: statistic={}, confirmedRequests={}", statistic, confirmedRequests);

        return eventMapper.mapEventToEventFullDto(
                event,
                confirmedRequests.getOrDefault(event.getId(), 0),
                statistic.getOrDefault(event.getId(), 0L));
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(UpdateEventUserRequest updateRequest, long userId, long eventId) {
        Event event = validateEventNotFound(eventId);
        log.info("Event private service, updating event: event={}", event);

        validateInitiator(event.getInitiator().getId(), userId, eventId);
        validateEventStateNotPublished(event);
        Category category = null;
        if (updateRequest.hasCategory()) {
            category = validateCategoryNotFound(updateRequest.getCategory());
        }
        eventMapper.updateEventFields(event, updateRequest, category);

        updateState(updateRequest, event);
        event = eventRepository.save(event);
        log.info("Event private service, updating event: updatedEvent={}", event);

        Map<Long, Long> statistic = statisticService.getStatsByEvents(List.of(event), false);
        Map<Long, Integer> confirmedRequests = new HashMap<>();
        if (event.isRequestModeration()) {
            confirmedRequests = requestService.getCountOfConfirmedRequestsByEvents(List.of(event));
        }
        log.info("Event private service, updating event: statistic={}, confirmedRequests={}", statistic, confirmedRequests);

        return eventMapper.mapEventToEventFullDto(event,
                confirmedRequests.getOrDefault(event.getId(), 0),
                statistic.getOrDefault(event.getId(), 0L));
    }

    @Override
    public List<ParticipantRequestDto> getEventRequests(long userId, long eventId) {
        Event event = validateEventNotFound(eventId);
        validateInitiator(event.getInitiator().getId(), userId, eventId);

        return requestRepository.findAllByEventIn(List.of(event)).stream()
                .map(requestMapper::mapParticipantRequestToParticipantRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestsByEvent(EventRequestStatusUpdateRequest updateRequest,
                                                                long userId, long eventId) {
        Event event = validateEventNotFound(eventId);
        log.info("Event private service, updating request by event: event={}", event);

        validateInitiator(event.getInitiator().getId(), userId, eventId);
        validateRequestNeedConfirmation(event);
        validateEventStatePublished(event);

        List<ParticipantRequest> requests = requestRepository.findAllByIdIn(updateRequest.getRequestIds());

        requests.forEach(this::validateRequestStatePending);

        if (updateRequest.getStatus().equals(EventRequestStatusUpdateRequest.RequestStatusAction.CONFIRMED)) {
             return confirmRequests(event, requests);

        } else if (updateRequest.getStatus().equals(EventRequestStatusUpdateRequest.RequestStatusAction.REJECTED)) {
            return rejectRequests(requests);

        } else {
            throw new ActionConflictException("Action is not available");
        }
    }

    private EventRequestStatusUpdateResult rejectRequests(List<ParticipantRequest> requests) {
        EventRequestStatusUpdateResult requestStatusUpdateResult = new EventRequestStatusUpdateResult();

        requests.forEach(request -> request.setStatus(ParticipantRequest.Status.REJECTED));
        List<ParticipantRequestDto> canceledRequestDtos = requests.stream()
                .map(requestMapper::mapParticipantRequestToParticipantRequestDto)
                .toList();

        requestStatusUpdateResult.setRejectedRequests(canceledRequestDtos);
        return requestStatusUpdateResult;
    }

    private EventRequestStatusUpdateResult confirmRequests(Event event, List<ParticipantRequest> requests) {
        EventRequestStatusUpdateResult requestStatusUpdateResult = new EventRequestStatusUpdateResult();

        Map<Long, Integer> previouslyConfirmedRequests = new HashMap<>();
        if (event.isRequestModeration()) {
            previouslyConfirmedRequests = requestService.getCountOfConfirmedRequestsByEvents(List.of(event));
        }
        log.info("Event private service, updating request by event: previouslyConfirmedRequests={}", previouslyConfirmedRequests);

        int countConfirmed = previouslyConfirmedRequests.getOrDefault(event.getId(), 0);
        validateParticipantLimitReached(event, countConfirmed);

        long restOfParticipantLimit = event.getParticipantLimit() - countConfirmed;
        boolean rejectTheRestOfRequests = true;
        if (event.getParticipantLimit() == 0 || restOfParticipantLimit > requests.size()) {
            rejectTheRestOfRequests = false;
        } else {
            requests = requests.stream().limit(restOfParticipantLimit).toList();
        }

        log.info("Event private service, updating request by event: requests to confirm={}", requests);

        requests.forEach(request -> request.setStatus(ParticipantRequest.Status.CONFIRMED));
        requests = requestRepository.saveAll(requests);

        List<ParticipantRequestDto> confirmedRequestDtos = requests.stream()
                .map(requestMapper::mapParticipantRequestToParticipantRequestDto)
                .toList();

        List<ParticipantRequestDto> canceledRequestDtos = new ArrayList<>();
        if (rejectTheRestOfRequests) {
            List<ParticipantRequest> requestsToReject = requestRepository.findAllByEventInAndStatus(List.of(event),
                    ParticipantRequest.Status.PENDING);
            log.info("Event private service, updating request by event: requests to reject={}", requestsToReject);

            requestsToReject.forEach(request -> request.setStatus(ParticipantRequest.Status.REJECTED));
            requestsToReject = requestRepository.saveAll(requestsToReject);

            canceledRequestDtos = requestsToReject.stream()
                    .map(requestMapper::mapParticipantRequestToParticipantRequestDto)
                    .toList();
        }

        requestStatusUpdateResult.setRejectedRequests(canceledRequestDtos);
        requestStatusUpdateResult.setConfirmedRequests(confirmedRequestDtos);

        return requestStatusUpdateResult;
    }

    private void updateState(UpdateEventUserRequest updateRequest, Event event) {
        if (!updateRequest.hasStateAction()) {
            return;
        }
        UpdateEventUserRequest.StateAction stateAction = updateRequest.getStateAction();
        if (stateAction.equals(UpdateEventUserRequest.StateAction.SEND_TO_REVIEW)) {
            event.setPublishedOn(null);
            event.setState(Event.State.PENDING);
        }
        if (stateAction.equals(UpdateEventUserRequest.StateAction.CANCEL_REVIEW)) {
            if (event.getState().equals(Event.State.CANCELED)) {
                throw new ActionConflictException(String.format("Cannot sent to review canceled event, current state %s",
                        event.getState()));
            }
            event.setState(Event.State.CANCELED);
            event.setPublishedOn(null);
        }
    }

    private User validateUserNotFound(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id %d not found", userId)));
    }

    private Category validateCategoryNotFound(long categoryId) {
            return categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException(String.format("Category with id=%s was not found",
                            categoryId)));
    }

    private Event validateEventNotFound(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event with id %d not found", eventId)));
    }

    private void validateInitiator(long initiatorId, long userId, long eventId) {
        if (initiatorId != userId) {
            throw new ActionConflictException(String.format("Action is available only for initiator of event %d",
                    eventId));
        }
    }

    private void validateEventStateNotPublished(Event event) {
        if (event.getState().equals(Event.State.PUBLISHED)) {
            throw new ActionConflictException("Action it not available in state PUBLISHED");
        }
    }

    private void validateEventStatePublished(Event event) {
        if (!event.getState().equals(Event.State.PUBLISHED)) {
            throw new ActionConflictException(String.format("Action available only in state PUBLISHED. Current state: %d",
                    event.getId()));
        }
    }

    private void validateRequestStatePending(ParticipantRequest request) {
        if (!request.getStatus().equals(ParticipantRequest.Status.PENDING)) {
            throw new ActionConflictException(String.format("Request with id %d must have status PENDING. Current status: %s",
                    request.getId(), request.getStatus()));
        }
    }

    private void validateRequestNeedConfirmation(Event event) {
        if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
            throw new ActionConflictException("Request moderation is not need for event with id " + event.getId());
        }
    }

    private void validateParticipantLimitReached(Event event, int countConfirmed) {
        if (countConfirmed == event.getParticipantLimit()) {
            throw new ActionConflictException(String.format("The participant limit for event with id %d has been reached",
                    event.getId()));
        }
    }
}
