package ru.practicum.ewm.statistic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.StatsDto;
import ru.practicum.ewm.api.privateapi.events.model.Event;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatisticServiceImp implements StatisticService {
    private final StatsClient client;
    private final String applicationName;

    @Override
    @Async
    public void saveHit(String uri, String ip) {
        LocalDateTime timestamp = LocalDateTime.now();
        log.info("Statistic service, save hit: uri={}, ip={}, app={}, timestamp={}", uri, ip, applicationName, timestamp);
        client.postHit(applicationName, uri, ip, timestamp);
    }

    @Override
    public Map<Long, Long> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.info("Statistic service, getting events statistic: start={}, end={}, uris={}, unique={}", start, end, uris, unique);

        List<StatsDto> statsDtos = client.getStats(start, end, uris, unique);
        log.info("Statistic service, getting events statistic: result={}", statsDtos);

        return transformStatistic(statsDtos);
    }

    @Override
    public Map<Long, Long> getStatsByEvents(List<Event> events, boolean unique) {
        List<String> uris = events.stream()
                .map(event -> String.format("/events/%d", event.getId()))
                .toList();
        LocalDateTime start = events.stream()
                .map(Event::getCreatedOn)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);
        start = (Objects.isNull(start) ? LocalDateTime.MIN : start);
        LocalDateTime end = LocalDateTime.now().plusMinutes(1);

        return getStats(start, end, uris, unique);
    }

    private Map<Long, Long> transformStatistic(List<StatsDto> statsDtos) {
        Map<Long, Long> result = new HashMap<>();
        for (StatsDto dto : statsDtos) {
            String uri = dto.getUri();
            Long id = Long.parseLong(uri.substring("/events/".length()));
            result.put(id, dto.getHits());
        }
        log.info("Statistic service, getting events statistic: transformed result={}", result);
        return result;
    }
}
