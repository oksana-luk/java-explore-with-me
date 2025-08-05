package ru.practicum.ewm.stats;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.HitDto;
import ru.practicum.ewm.StatsDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {
    private final StatsRepository statsRepository;

    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        validateDates(start, end);
        boolean hasUris = !(Objects.isNull(uris) || uris.isEmpty());
        if (hasUris) {
            if (unique) {
                return statsRepository.findStatsByUriUniqueIp(start, end, uris);
            } else {
                return statsRepository.findStatsByUri(start, end, uris);
            }
        } else if (unique) {
            return statsRepository.findStatsAllUriUniqueIp(start, end);
        } else {
            return statsRepository.findStatsAllUri(start, end);
        }
    }

    @Transactional
    public void postHit(HitDto hitDto) {
        statsRepository.save(hitDto);
    }

    public void validateDates(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end) || start.equals(end)) {
            throw new ValidationException(String.format("The period of statistic is not correct. Start %s, end %s",
                    start, end));
        }
    }
}
