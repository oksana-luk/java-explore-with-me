package ru.practicum.ewm;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {
    private final StatsClient client;

    @GetMapping("/stats")
    public List<StatsDto> getStats3() {
        LocalDateTime start = LocalDateTime.of(2025, 7, 1, 12, 12, 12);
        LocalDateTime end = LocalDateTime.of(2025, 10, 1, 12, 12, 12);
        return client.getStats(start, end, List.of("/test/hit", "/events/42"), true);
    }

    @GetMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void postHit(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();
        String app = "explore-service";
        LocalDateTime timestamp = LocalDateTime.now();
        log.info("GET /test/hit uri={}, ip={}, app={}, timestamp={}", uri, ip, app, timestamp);
        client.postHit("explore-service", uri, ip, timestamp);
    }
}
