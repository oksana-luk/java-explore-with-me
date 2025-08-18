package ru.practicum.ewm.api.publicapi.compilations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.api.adminapi.compilations.CompilationMapper;
import ru.practicum.ewm.api.adminapi.compilations.CompilationRepository;
import ru.practicum.ewm.api.adminapi.compilations.dto.CompilationDto;
import ru.practicum.ewm.api.adminapi.compilations.model.Compilation;
import ru.practicum.ewm.api.privateapi.events.EventMapper;
import ru.practicum.ewm.api.privateapi.events.dto.EventShortDto;
import ru.practicum.ewm.api.privateapi.events.model.Event;
import ru.practicum.ewm.api.privateapi.requests.ParticipantRequestService;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.statistic.StatisticService;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationPublicServiceImpl implements CompilationPublicService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;
    private final StatisticService statisticService;
    private final ParticipantRequestService requestService;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        Specification<Compilation> specification = getEventSpecification(pinned);

        PageRequest page  = PageRequest.of(from > 0 ? from / size : 0, size);
        log.info("Compilation public service, getting compilations: page={}", page);

        List<Compilation> compilations = compilationRepository.findAll(specification, page).getContent();
        log.info("Compilation public service, getting compilations: resultCount={}", compilations.size());

        List<Event> events = compilations.stream()
                .flatMap(c -> c.getEvents().stream())
                .distinct()
                .toList();

        Map<Long, Set<EventShortDto>> eventShortDtos = new HashMap<>();
        if (!events.isEmpty()) {
            Map<Long, Long> statistic = statisticService.getStatsByEvents(events, false);
            Map<Long, Integer> confirmedRequests = requestService.getCountOfConfirmedRequestsByEvents(events);
            log.info("Compilation public service, getting compilations: statistic={}, confirmedRequests={}", statistic, confirmedRequests);

            compilations.forEach(compilation -> {
                Set<EventShortDto> setDtos = compilation.getEvents().stream()
                        .map(event -> eventMapper.mapEventToEventShortDto(
                                event,
                                confirmedRequests.getOrDefault(event.getId(), 0),
                                statistic.getOrDefault(event.getId(), 0L)))
                        .collect(Collectors.toSet());
                eventShortDtos.put(compilation.getId(), setDtos);
            });
        }
        log.info("Compilation public service, getting compilations: count eventShortDtos={}", eventShortDtos.size());

        return compilations.stream()
                .map(compilation -> compilationMapper.mapCompilationToCompilationDto(compilation,
                        eventShortDtos.getOrDefault(compilation.getId(), new HashSet<>())))
                .toList();
    }

    @Override
    public CompilationDto getCompilation(long compilationId) {
        Compilation compilation = compilationRepository.findById(compilationId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation with id %d not found", compilationId)));
        log.info("Compilation public service, getting compilation: compilationId={}", compilationId);

        List<Event> events = new ArrayList<>(compilation.getEvents());

        Set<EventShortDto> eventShortDtos = new HashSet<>();
        if (!events.isEmpty()) {
            Map<Long, Long> statistic = statisticService.getStatsByEvents(events, false);
            Map<Long, Integer> confirmedRequests = requestService.getCountOfConfirmedRequestsByEvents(events);
            log.info("Compilation public service, getting compilation: statistic={}, confirmedRequests={}", statistic, confirmedRequests);

            eventShortDtos = compilation.getEvents().stream()
                    .map(event -> eventMapper.mapEventToEventShortDto(
                            event,
                            confirmedRequests.getOrDefault(event.getId(), 0),
                            statistic.getOrDefault(event.getId(), 0L)))
                    .collect(Collectors.toSet());
        }
        log.info("Compilation public service, getting compilation: eventShortDtos={}", eventShortDtos);

        return compilationMapper.mapCompilationToCompilationDto(compilation, eventShortDtos);
    }

    private Specification<Compilation> getEventSpecification(Boolean pinned) {
        Specification<Compilation> specification = Specification.where(null);

        if (Objects.nonNull(pinned)) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("pinned"), pinned));
        }
        return specification;
    }
}
