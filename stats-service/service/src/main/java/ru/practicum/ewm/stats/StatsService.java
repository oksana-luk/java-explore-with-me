package ru.practicum.ewm.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.HitDto;
import ru.practicum.ewm.StatsDto;
import ru.practicum.ewm.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {
    private final StatsRepository statsRepository;
    private final HitMapper hitMapper;
    private final StatsMapper statsMapper;

    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        validateDates(start, end);
        List<Stats> result;
        boolean hasUris = !(Objects.isNull(uris) || uris.isEmpty());
        if (hasUris) {
            if (unique) {
                result = statsRepository.findStatsByUriUniqueIp(start, end, uris);
            } else {
                result = statsRepository.findStatsByUri(start, end, uris);
            }
        } else if (unique) {
            result = statsRepository.findStatsAllUriUniqueIp(start, end);
        } else {
            result = statsRepository.findStatsAllUri(start, end);
        }
        return result.stream()
                .map(statsMapper::mapStatsToStatsDto)
                .toList();
    }

    @Transactional
    public void postHit(HitDto hitDto) {
        statsRepository.save(hitMapper.mapHitDtoToHit(hitDto));
    }

    public void validateDates(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end) || start.equals(end)) {
            throw new ValidationException(String.format("The period of statistic is not correct. Start %s, end %s",
                    start, end));
        }
    }
}
