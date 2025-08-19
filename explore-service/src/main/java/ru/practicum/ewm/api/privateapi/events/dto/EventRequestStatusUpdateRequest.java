package ru.practicum.ewm.api.privateapi.events.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class EventRequestStatusUpdateRequest {
    @NotNull(message = "Requests ids should not be empty")
    private Set<Long> requestIds;
    @NotNull(message = "Status should not be empty")
    private RequestStatusAction status;

    public enum RequestStatusAction {
        CONFIRMED, REJECTED;
    }
}
