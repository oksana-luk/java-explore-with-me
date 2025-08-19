package ru.practicum.ewm.statistic;

import ru.practicum.ewm.api.privateapi.events.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface StatisticService {
    void saveHit(String uri, String ip);

    Map<Long, Long> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);

    Map<Long, Long> getStatsByEvents(List<Event> events, boolean unique);

}
