package ru.practicum.ewm.api.adminapi.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.api.adminapi.categories.CategoryRepository;
import ru.practicum.ewm.api.adminapi.categories.model.Category;
import ru.practicum.ewm.api.adminapi.events.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.api.adminapi.moderations.ModerationService;
import ru.practicum.ewm.api.privateapi.events.EventMapper;
import ru.practicum.ewm.api.privateapi.events.EventRepository;
import ru.practicum.ewm.api.privateapi.events.dto.EventFullDto;
import ru.practicum.ewm.api.privateapi.events.model.Event;
import ru.practicum.ewm.api.privateapi.requests.ParticipantRequestService;
import ru.practicum.ewm.exception.ActionConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.statistic.StatisticService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventAdminServiceImpl implements EventAdminService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final StatisticService statisticService;
    private final ParticipantRequestService requestService;
    private final EventMapper eventMapper;
    private final ModerationService moderationService;

    @Override
    public List<EventFullDto> getEvents(List<Long> users, List<Event.State> states, List<Long> categories,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        Specification<Event> specification = getEventSpecification(users, states, categories, rangeStart, rangeEnd);

        PageRequest page  = PageRequest.of(from > 0 ? from / size : 0, size);
        log.info("Event admin service, getting events: page={}", page);

        List<Event> events = eventRepository.findAll(specification, page).getContent();
        log.info("Event admin service, getting events: result={}", events);

        Map<Long, Long> statistic = statisticService.getStatsByEvents(events, false);
        Map<Long, Integer> confirmedRequests = requestService.getCountOfConfirmedRequestsByEvents(events);
        Map<Long, String> moderationComments = moderationService.getLatestModerationsComments(events);
        log.info("Event admin service, getting events statistic={}, confirmedRequests={}, moderationComments={}",
                statistic, confirmedRequests, moderationComments);

        return events.stream()
                .map(event -> eventMapper.mapEventToEventFullDto(
                        event,
                        confirmedRequests.getOrDefault(event.getId(), 0),
                        statistic.getOrDefault(event.getId(), 0L),
                        moderationComments.getOrDefault(event.getId(), null)))
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(UpdateEventAdminRequest updateRequest, long eventId) {
        Event event = validateEventNotFound(eventId);
        log.info("Event admin service, updating event: event={}", event);

        Category category = null;
        if (updateRequest.hasCategory()) {
            category = validateCategoryNotFound(updateRequest.getCategory());
        }
        eventMapper.updateEventFields(event, updateRequest, category);

        updateState(updateRequest, event);
        event = eventRepository.save(event);
        log.info("Event admin service, updating event: updatedEvent={}", event);

        Map<Long, Long> statistic = statisticService.getStatsByEvents(List.of(event), false);
        Map<Long, Integer> confirmedRequests = new HashMap<>();
        if (event.isRequestModeration()) {
            confirmedRequests = requestService.getCountOfConfirmedRequestsByEvents(List.of(event));
        }
        log.info("Event admin service, updating event: statistic={}, confirmedRequests={}", statistic, confirmedRequests);

        String moderationComment = null;
        if (event.getState().equals(Event.State.FIX_NEED) || event.getState().equals(Event.State.REJECTED)) {
            moderationComment = updateRequest.getComment();
        }
        moderationService.addModeration(event, null, event.getState(), moderationComment);

        return eventMapper.mapEventToEventFullDto(event,
                confirmedRequests.getOrDefault(event.getId(), 0),
                statistic.getOrDefault(event.getId(), 0L),
                moderationComment);
    }

    private Specification<Event> getEventSpecification(List<Long> users, List<Event.State> states, List<Long> categories,
                                                       LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        Specification<Event> specification = Specification.where(null);

        if (Objects.nonNull(users) && !users.isEmpty()) {
            specification = specification.and((root, query, cb) -> root.get("initiator").get("id").in(
                    users.stream().filter(Objects::nonNull).toList()));
        }
        if (Objects.nonNull(states) && !states.isEmpty()) {
            specification = specification.and((root, query, cb) -> root.get("state").in(states));
        }
        if (Objects.nonNull(categories) && !categories.isEmpty()) {
            specification = specification.and((root, query, cb) -> root.get("category").get("id").in(
                    categories.stream().filter(Objects::nonNull).toList()));
        }
        if (Objects.nonNull(rangeStart) && Objects.nonNull(rangeEnd) && !rangeEnd.isAfter(rangeStart)) {
            throw new ValidationException(String.format("""
                                                        Fields: rangeStart, rangeEnd. Start date should be before end date.
                                                        Current value start: %s, end: %s""", rangeStart, rangeEnd));
        }
        if (Objects.nonNull(rangeStart)) {
            specification = specification.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }
        if (Objects.nonNull(rangeEnd)) {
            specification = specification.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }
        return specification;
    }

    private void updateState(UpdateEventAdminRequest updateRequest, Event event) {
        if (!updateRequest.hasStateAction()) {
            return;
        }
        UpdateEventAdminRequest.StateAction stateAction = updateRequest.getStateAction();
        if (stateAction.equals(UpdateEventAdminRequest.StateAction.PUBLISH_EVENT)) {
            validateEventStatePending(event);
            if (Objects.nonNull(event.getPublishedOn())) {
                throw new ActionConflictException(String.format("Cannot publish the event because it has been published on %s",
                        event.getPublishedOn()));
            }
            if (event.getEventDate().isBefore(LocalDateTime.now())) {
                throw new ActionConflictException(String.format("Cannot publish the event because it has already taken place on %s",
                        event.getEventDate()));
            }
            event.setState(Event.State.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        }
        if (stateAction.equals(UpdateEventAdminRequest.StateAction.REJECT_EVENT)) {
            validateEventStatePending(event);
            event.setState(Event.State.REJECTED);
            event.setPublishedOn(null);
        }
        if (stateAction.equals(UpdateEventAdminRequest.StateAction.FIX_EVENT)) {
            validateEventStatePending(event);
            event.setState(Event.State.FIX_NEED);
            event.setPublishedOn(null);
        }
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

    private void validateEventStatePending(Event event) {
        if (!event.getState().equals(Event.State.PENDING)) {
            throw new ActionConflictException(String.format("Action it not available because it's not in the right state PENDING, current state %s",
                    event.getState()));
        }
    }
}
