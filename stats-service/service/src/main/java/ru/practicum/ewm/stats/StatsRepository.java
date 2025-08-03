package ru.practicum.ewm.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.HitDto;
import ru.practicum.ewm.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<HitDto, Long> {

    @Query("select new ru.practicum.ewm.StatsDto(h.app, h.uri, count(h.ip)) " +
            "from HitDto as h " +
            "where h.timestamp BETWEEN :start AND :end " +
            "group by h.app, h.uri " +
            "order by count(h.ip) desc")
    List<StatsDto> findStatsAllUri(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.ewm.StatsDto(h.app, h.uri, count(distinct h.ip)) " +
            "from HitDto as h " +
            "where h.timestamp BETWEEN :start AND :end " +
            "group by h.app, h.uri " +
            "order by count(distinct h.ip) desc")
    List<StatsDto> findStatsAllUriUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.ewm.StatsDto(h.app, h.uri, count(h.ip)) " +
            "from HitDto as h " +
            "where h.timestamp BETWEEN :start AND :end " +
            "and h.uri in :uris " +
            "group by h.app, h.uri " +
            "order by count(h.ip) desc")
    List<StatsDto> findStatsByUri(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.ewm.StatsDto(h.app, h.uri, count(distinct h.ip)) " +
            "from HitDto as h " +
            "where h.timestamp BETWEEN :start AND :end " +
            "and h.uri in :uris " +
            "group by h.app, h.uri " +
            "order by count(distinct h.ip) desc")
    List<StatsDto> findStatsByUriUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);
}
