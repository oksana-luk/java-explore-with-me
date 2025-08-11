package ru.practicum.ewm;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected <T, V> ResponseEntity<V> post(String path, T body, ParameterizedTypeReference<V> typeRef) {
        return makeAndSendRequest(HttpMethod.POST, path, null, body, typeRef);
    }

    protected <V> ResponseEntity<V> get(String path, @Nullable Map<String, Object> parameters, ParameterizedTypeReference<V> typeRef) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null, typeRef);
    }

    private <T, V> ResponseEntity<V> makeAndSendRequest(HttpMethod method, String path, @Nullable Map<String, Object> parameters,
                                                        @Nullable T body, ParameterizedTypeReference<V> typeRef) {
        log.info("Stats client send http request: httpMethod={}, path={}, params={}, body={}, typeRef={}",
                method, path, parameters, body, typeRef);

        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());
        ResponseEntity<V> statsServiceResponse;
        if (parameters != null) {
            statsServiceResponse = rest.exchange(path, method, requestEntity, typeRef, parameters);
        } else {
            statsServiceResponse = rest.exchange(path, method, requestEntity, typeRef);
        }
        ResponseEntity<V> response = prepareGatewayResponse(statsServiceResponse);
        log.info("Stats client get response of http request: httpMethod={}, path={}, response body={}",
                method, path, response.getBody());
        return response;
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static <V> ResponseEntity<V> prepareGatewayResponse(ResponseEntity<V> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
