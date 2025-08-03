package ru.practicum.ewm.stats;

import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.HitDto;
import ru.practicum.ewm.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping()
@Validated
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/stats")
    public List<StatsDto> getStats(@NotNull @RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                   @NotNull @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                   @RequestParam(value = "uris", required = false) List<String> uris,
                                   @RequestParam(value = "unique", required = false) boolean unique) {
        log.info("GET /stats start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        if (start.isAfter(end) || start.equals(end)) {
            throw new ValidationException(String.format("The period of statistic is not correct. Start %s, end %s",
                    start, end));
        }
        List<StatsDto> result = statsService.getStats(start, end, uris, unique);
        log.info("GET /stats result={}", result);
        return result;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void postHit(@NotNull @RequestBody HitDto hitDto) {
        log.info("POST /hit body={}", hitDto);
        statsService.postHit(hitDto);
        log.info("POST /hit finished");
    }
}
