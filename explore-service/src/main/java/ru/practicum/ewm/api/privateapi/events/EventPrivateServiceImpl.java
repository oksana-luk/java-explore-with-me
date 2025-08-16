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
import ru.practicum.ewm.exception.ValidationException;
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
        log.info("Event service add event {}", event);

        event = eventRepository.save(event);
        log.info("Event service add event result={}", event);

        EventFullDto eventFullDto = eventMapper.mapEventToEventFullDto(event, 0, 0);
        log.info("Event service add event eventFullDto={}", eventFullDto);

        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getEvents(long userId, int from, int size) {
        validateUserNotFound(userId);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        log.info("Event service getting events page {}", page);

        Page<Event> eventsPage = eventRepository.findAllByInitiatorId(userId, page);
        List<Event> events = eventsPage.getContent();
        if (events.isEmpty()) {
            log.info("Event service getting events result={}", List.of());
            return List.of();
        }

        Map<Long, Long> statistic = statisticService.getStatsByEvents(events, false);
        Map<Long, Integer> confirmedRequests = requestService.getCountOfConfirmedRequestsByEvents(events);

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
        Map<Long, Integer> confirmedRequests = requestService.getCountOfConfirmedRequestsByEvents(List.of(event));

        return eventMapper.mapEventToEventFullDto(
                event,
                confirmedRequests.getOrDefault(event.getId(), 0),
                statistic.getOrDefault(event.getId(), 0L));
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(UpdateEventUserRequest updateRequest, long userId, long eventId) {
        Event event = validateEventNotFound(eventId);
        validateInitiator(event.getInitiator().getId(), userId, eventId);
        if (event.getState().equals(Event.State.PUBLISHED)) {
            throw new ActionConflictException(String.format("It is not available to change published events. Event id %s",
                    eventId));
        }
        if (updateRequest.hasCategory()) {
            Category category = validateCategoryNotFound(updateRequest.getCategory());
            eventMapper.updateEventFields(event, updateRequest, category);
        } else {
            eventMapper.updateEventFields(event, updateRequest, null);
        }
        updatePublishedCanceledFields(updateRequest, event);
        event = eventRepository.save(event);

        Map<Long, Long> statistic = statisticService.getStatsByEvents(List.of(event), false);
        Map<Long, Integer> confirmedRequests = requestService.getCountOfConfirmedRequestsByEvents(List.of(event));

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
        validateInitiator(event.getInitiator().getId(), userId, eventId);
        if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
            throw new ActionConflictException("Request moderation is not need for event with id " + eventId);
        }
        if (!event.getState().equals(Event.State.PUBLISHED)) {
            throw new ActionConflictException(String.format("""
                                 Event with id %d is not published. Participant requests are not available""", eventId));
        }
        EventRequestStatusUpdateResult requestStatusUpdateResult = new EventRequestStatusUpdateResult();

        List<ParticipantRequest> requests = requestRepository.findAllByIdIn(updateRequest.getRequestIds());

        requests.forEach(request -> {
            if (!request.getStatus().equals(ParticipantRequest.Status.PENDING)) {
                throw new ActionConflictException(String.format("Request with id %d must have status PENDING. Current status: %s",
                        request.getId(), request.getStatus()));
            }
        } );

        if (updateRequest.getStatus().equals(EventRequestStatusUpdateRequest.RequestStatusAction.CONFIRMED)) {
            Map<Long, Integer> confirmedRequestsYet = requestService.getCountOfConfirmedRequestsByEvents(List.of(event));
            int countConfirmedYet = confirmedRequestsYet.getOrDefault(event.getId(), 0);
            if (countConfirmedYet == event.getParticipantLimit()) {
                throw new ActionConflictException(String.format("The participant limit for event with id %d has been reached", eventId));
            }
            long rest = event.getParticipantLimit() - countConfirmedYet;

            if (requests.size() > rest) {
                requests = requests.stream().limit(rest).toList();
            }

            List<ParticipantRequestDto> confirmedRequestDtos = updateAndSaveStatusByRequests(requests,
                    ParticipantRequest.Status.CONFIRMED).stream()
                    .map(requestMapper::mapParticipantRequestToParticipantRequestDto)
                    .toList();

            requestStatusUpdateResult.setConfirmedRequests(confirmedRequestDtos);

            List<ParticipantRequest> restOfRequests = requestRepository.findAllByEventInAndStatus(List.of(event),
                    ParticipantRequest.Status.PENDING);

            List<ParticipantRequestDto> canceledRequestDtos = updateAndSaveStatusByRequests(restOfRequests,
                    ParticipantRequest.Status.CANCELED).stream()
                    .map(requestMapper::mapParticipantRequestToParticipantRequestDto)
                    .toList();

            requestStatusUpdateResult.setRejectedRequests(canceledRequestDtos);

        } else if (updateRequest.getStatus().equals(EventRequestStatusUpdateRequest.RequestStatusAction.REJECTED)) {
            List<ParticipantRequestDto> canceledRequestDtos = updateAndSaveStatusByRequests(requests,
                    ParticipantRequest.Status.CANCELED).stream()
                    .map(requestMapper::mapParticipantRequestToParticipantRequestDto)
                    .toList();

            requestStatusUpdateResult.setRejectedRequests(canceledRequestDtos);

        } else {
            throw new ActionConflictException("Action is not available");
        }
        return requestStatusUpdateResult;
    }

    private void updatePublishedCanceledFields(UpdateEventUserRequest updateRequest, Event event) {
        if (!updateRequest.hasStateAction()) {
            return;
        }
        UpdateEventUserRequest.StateAction stateAction = updateRequest.getStateAction();
        if (stateAction.equals(UpdateEventUserRequest.StateAction.SEND_TO_REVIEW)) {
            if (event.getState().equals(Event.State.CANCELED)) {
                throw new ActionConflictException(String.format("Cannot sent to review canceled event, current state %s",
                        event.getState()));
            }
            event.setPublishedOn(null);
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

    private List<ParticipantRequest> updateAndSaveStatusByRequests(List<ParticipantRequest> requests, ParticipantRequest.Status status) {
        requests.forEach(request -> request.setStatus(status));
        return requestRepository.saveAll(requests);
    }

    private User validateUserNotFound(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id %d not found", userId)));
    }

    private Category validateCategoryNotFound(long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%s was not found", categoryId)));
    }

    private Event validateEventNotFound(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Event with id %d not found", eventId)));
    }

    private void validateInitiator(long initiatorId, long userId, long eventId) {
        if (initiatorId != userId) {
            throw new ActionConflictException(String.format("User with id %d is not available to change event with id %d",
                    userId, eventId));
        }
    }
}
