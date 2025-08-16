package ru.practicum.ewm.api.publicapi.events;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.api.adminapi.categories.CategoryRepository;
import ru.practicum.ewm.api.privateapi.events.EventMapper;
import ru.practicum.ewm.api.privateapi.events.EventRepository;
import ru.practicum.ewm.api.privateapi.events.dto.EventFullDto;
import ru.practicum.ewm.api.privateapi.events.dto.EventShortDto;
import ru.practicum.ewm.api.privateapi.events.model.Event;
import ru.practicum.ewm.api.privateapi.requests.ParticipantRequestService;
import ru.practicum.ewm.api.privateapi.requests.model.ParticipantRequest;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.statistic.StatisticService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventPublicServiceImpl implements EventPublicService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final StatisticService statisticService;
    private final ParticipantRequestService requestService;
    private final EventMapper eventMapper;

    @Override
    public List<EventShortDto> getEvents(EventFilterRequest filterRequest, int from, int size, HttpServletRequest request) {
        statisticService.saveHit(request.getRequestURI(), request.getRemoteAddr());

        Specification<Event> specification = getEventSpecification(filterRequest);

        Sort sort= Sort.by(Sort.Direction.ASC, "eventDate");
        PageRequest page  = PageRequest.of(from > 0 ? from / size : 0, size, sort);
        log.info("Event public service, getting events: page={}, sort={}", page, sort);

        List<Event> events = eventRepository.findAll(specification, page).getContent();
        log.info("Event public service, getting events result={}", events);

        Map<Long, Long> statistic = statisticService.getStatsByEvents(events, false);
        Map<Long, Integer> confirmedRequests = requestService.getCountOfConfirmedRequestsByEvents(events);

        List<EventShortDto> result = events.stream()
                .map(event -> eventMapper.mapEventToEventShortDto(
                        event,
                        confirmedRequests.getOrDefault(event.getId(), 0),
                        statistic.getOrDefault(event.getId(), 0L)))
                .toList();

        if (Objects.nonNull(filterRequest.getSort()) && filterRequest.getSort().equals(EventFilterRequest.Sort.VIEWS)) {
            result = result.stream()
                    .sorted(Comparator.comparingLong(EventShortDto::getViews))
                    .toList();
        }
        return result;
    }

    @Override
    public EventFullDto getEvent(long eventId, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(eventId, Event.State.PUBLISHED).orElseThrow(() ->
                new NotFoundException(String.format("Event with id %d not found", eventId)));
        log.info("Event public service, getting event by id: eventId={}", eventId);

        statisticService.saveHit(request.getRequestURI(), request.getRemoteAddr());

        Map<Long, Long> statistic = statisticService.getStatsByEvents(List.of(event), false);
        Map<Long, Integer> confirmedRequests = requestService.getCountOfConfirmedRequestsByEvents(List.of(event));

        return eventMapper.mapEventToEventFullDto(
                event,
                confirmedRequests.getOrDefault(event.getId(), 0),
                statistic.getOrDefault(event.getId(), 0L));
    }

    private Specification<Event> getEventSpecification(EventFilterRequest filterRequest) {
        Specification<Event> specification = Specification.where(null);
        if (Objects.nonNull(filterRequest.getText()) && !filterRequest.getText().isBlank()) {
                String search = "%" + filterRequest.getText().toLowerCase() + "%";
                specification = specification.and((root, query, cb) ->
                        cb.or(
                                cb.like(cb.lower(root.get("annotation")), search),
                                cb.like(cb.lower(root.get("description")), search)
                        ));
        }
        if (Objects.nonNull(filterRequest.getPaid())) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("paid"), filterRequest.getPaid()));
        }
        if (Objects.nonNull(filterRequest.getCategories()) && !filterRequest.getCategories().isEmpty()) {
            specification = specification.and((root, query, cb) -> root.get("category").get("id").in(
                    filterRequest.getCategories().stream().filter(Objects::nonNull).toList()));
        }
        LocalDateTime rangeStart = filterRequest.getRangeStart();
        LocalDateTime rangeEnd = filterRequest.getRangeEnd();
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
        if (Objects.nonNull(filterRequest.getPaid())) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("paid"), filterRequest.getPaid()));
        }
        if (Objects.nonNull(filterRequest.getOnlyAvailable()) && filterRequest.getOnlyAvailable()) {
            specification = specification.and((root, query, cb) -> {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<ParticipantRequest> requestRoot = subquery.from(ParticipantRequest.class);
                subquery.select(cb.count(requestRoot.get("id")))
                        .where(cb.equal(requestRoot.get("event"), root));

                return cb.greaterThan(root.get("participantLimit"), subquery);
            });
        }
        specification = specification.and((root, query, cb) -> cb.equal(root.get("state"), Event.State.PUBLISHED.name()));
        return specification;
    }
}
