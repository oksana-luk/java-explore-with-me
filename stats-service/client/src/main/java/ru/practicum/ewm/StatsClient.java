package ru.practicum.ewm;

import jakarta.annotation.Nullable;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class StatsClient extends BaseClient {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatsClient(@Value("${stats-service.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public void postHit(String app, String uri, String ip, LocalDateTime timestamp) {
        HitDto hitDto = new HitDto(0, app, uri, ip, timestamp);

        ParameterizedTypeReference<HitDto> typeReference = new ParameterizedTypeReference<HitDto>() { };
        post("/hit", hitDto, typeReference);
    }

    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, @Nullable List<String> uris, boolean unique) {
        validateDate(start, end);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", DATE_TIME_FORMATTER.format(start));
        parameters.put("end", DATE_TIME_FORMATTER.format(end));
        boolean hasUris = !(Objects.isNull(uris) || uris.isEmpty());
        String paramUris = "";
         if (hasUris) {
            paramUris = "&uris={uris}";
            parameters.put("uris", String.join(",", uris));
        }
        if (unique) {
            parameters.put("unique", true);
        }
        String requestString = String.format("/stats?start={start}&end={end}%s%s", hasUris ? paramUris : "",
                unique ? "&unique=true" : "");

        ParameterizedTypeReference<List<StatsDto>> typeReference = new ParameterizedTypeReference<List<StatsDto>>() { };
        ResponseEntity<List<StatsDto>> responseEntity =  get(requestString, parameters, typeReference);
        return responseEntity.getBody();
    }

    private void validateDate(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new ValidationException(String.format("The period of statistic is not correct. Start %s, end %s",
                    start, end));
        }
    }
}
