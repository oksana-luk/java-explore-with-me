package ru.practicum.ewm.api.privateapi.requests.dto;


import lombok.*;
import ru.practicum.ewm.api.privateapi.requests.model.ParticipantRequest;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class ParticipantRequestDto {
    private Long id;
    private long requester;
    private long event;
    private String created;
    private ParticipantRequest.Status status;
}
