package ru.practicum.ewm.api.adminapi.compilations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.api.adminapi.compilations.dto.CompilationDto;
import ru.practicum.ewm.api.adminapi.compilations.dto.NewCompilationRequest;
import ru.practicum.ewm.api.adminapi.compilations.dto.UpdateCompilationRequest;
import ru.practicum.ewm.api.adminapi.compilations.model.Compilation;
import ru.practicum.ewm.api.privateapi.events.EventMapper;
import ru.practicum.ewm.api.privateapi.events.EventRepository;
import ru.practicum.ewm.api.privateapi.events.dto.EventShortDto;
import ru.practicum.ewm.api.privateapi.events.model.Event;
import ru.practicum.ewm.api.privateapi.requests.ParticipantRequestService;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.statistic.StatisticService;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CompilationAdminServiceImpl implements CompilationAdminService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final StatisticService statisticService;
    private final ParticipantRequestService requestService;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;

    @Override
    public CompilationDto addCompilation(NewCompilationRequest newCompilationRequest) {
        Set<Long> eventsIds = newCompilationRequest.getEvents();
        List<Event> events = eventsIds.isEmpty() ? List.of() : eventRepository.findAllById(eventsIds);
        log.info("Compilation admin service, adding compilation: eventsIds={}", eventsIds);

        Compilation compilation = compilationMapper.mapNewCompilationRequestToCompilation(newCompilationRequest,
                new HashSet<>(events));
        compilation = compilationRepository.save(compilation);
        log.info("Compilation admin service, adding compilation: count of events in compilation = {}",
                compilation.getEvents().size());

        Set<EventShortDto> eventShortDtos = new HashSet<>();
        if (!eventsIds.isEmpty()) {
            Map<Long, Long> statistic = statisticService.getStatsByEvents(events, false);
            Map<Long, Integer> confirmedRequests = requestService.getCountOfConfirmedRequestsByEvents(events);
            log.info("Compilation admin service, adding compilation: statistic={}, confirmedRequests={}",
                    statistic, confirmedRequests);

            eventShortDtos = compilation.getEvents().stream()
                    .map(event -> eventMapper.mapEventToEventShortDto(
                            event,
                            confirmedRequests.getOrDefault(event.getId(), 0),
                            statistic.getOrDefault(event.getId(), 0L)))
                    .collect(Collectors.toSet());
        }
        log.info("Compilation admin service, adding compilation: count of eventShortDto sets = {}", eventShortDtos.size());

        return compilationMapper.mapCompilationToCompilationDto(compilation, eventShortDtos);
    }

    @Override
    public void deleteCompilation(long compilationId) {
        validateNotFound(compilationId);
        log.info("Compilation admin service, deleting compilation by id {}", compilationId);
        compilationRepository.deleteById(compilationId);
    }

    @Override
    public CompilationDto updateCompilation(UpdateCompilationRequest updateCompilationRequest, long compilationId) {
        Compilation compilation = validateNotFound(compilationId);
        Set<Long> eventsIds = updateCompilationRequest.getEvents();
        log.info("Compilation admin service, updating compilation: eventsIds={}", eventsIds);

        List<Event> events = eventsIds.isEmpty() ? List.of() : eventRepository.findAllById(eventsIds);
        compilationMapper.updateFields(compilation, updateCompilationRequest, new HashSet<>(events));
        compilation = compilationRepository.save(compilation);
        log.info("Compilation admin service, updating compilation: count of events in updated compilation = {}",
                compilation.getEvents().size());

        Set<EventShortDto> eventShortDtos = new HashSet<>();
        if (!eventsIds.isEmpty()) {
            Map<Long, Long> statistic = statisticService.getStatsByEvents(events, false);
            Map<Long, Integer> confirmedRequests = requestService.getCountOfConfirmedRequestsByEvents(events);
            log.info("Compilation admin service, updating compilation: statistic={}, confirmedRequests={}",
                    statistic, confirmedRequests);

            eventShortDtos = compilation.getEvents().stream()
                    .map(event -> eventMapper.mapEventToEventShortDto(
                            event,
                            confirmedRequests.getOrDefault(event.getId(), 0),
                            statistic.getOrDefault(event.getId(), 0L)))
                    .collect(Collectors.toSet());
        }
        log.info("Compilation admin service, updating compilation: count of eventShortDto sets = {}", eventShortDtos.size());

        return compilationMapper.mapCompilationToCompilationDto(compilation, eventShortDtos);
    }

    private Compilation validateNotFound(long compilationId) {
        return compilationRepository.findById(compilationId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation with id %d not found", compilationId)));
    }
}
