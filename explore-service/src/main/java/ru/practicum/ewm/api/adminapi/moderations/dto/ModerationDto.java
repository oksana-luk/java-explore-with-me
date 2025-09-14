package ru.practicum.ewm.api.adminapi.moderations.dto;

import lombok.*;
import ru.practicum.ewm.api.privateapi.events.model.Event;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ModerationDto {
    private long id;
    private long event;
    private String admin;
    private Event.State state;
    private String moderatedOn;
    private String comment;
}
