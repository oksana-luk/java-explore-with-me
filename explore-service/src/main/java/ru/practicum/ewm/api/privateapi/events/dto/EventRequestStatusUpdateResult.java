package ru.practicum.ewm.api.privateapi.events.dto;

import lombok.*;
import ru.practicum.ewm.api.privateapi.requests.dto.ParticipantRequestDto;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateResult {
    private List<ParticipantRequestDto> confirmedRequests;
    private List<ParticipantRequestDto> rejectedRequests;
}
