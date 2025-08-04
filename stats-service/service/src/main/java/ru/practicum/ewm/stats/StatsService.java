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
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {
    private final StatsRepository statsRepository;
    private static final Pattern EVENT_URI_PATTERN = Pattern.compile("^/events/\\d+$");

    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        validateDates(start, end);
        boolean hasUris = !(Objects.isNull(uris) || uris.isEmpty());
        if (hasUris) {
            uris.forEach(this::validateEventUri);
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
        validateEventUri(hitDto.getUri());
        statsRepository.save(hitDto);
    }

    public void validateEventUri(String uri) {
        if (!EVENT_URI_PATTERN.matcher(uri).matches()) {
            throw new ValidationException("Parameter uri is invalid.");
        };
    }

    public void validateDates(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end) || start.equals(end)) {
            throw new ValidationException(String.format("The period of statistic is not correct. Start %s, end %s",
                    start, end));
        }
    }
}
