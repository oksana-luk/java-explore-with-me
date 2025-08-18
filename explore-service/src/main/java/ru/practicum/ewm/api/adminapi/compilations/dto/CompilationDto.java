package ru.practicum.ewm.api.adminapi.compilations.dto;

import lombok.*;
import ru.practicum.ewm.api.privateapi.events.dto.EventShortDto;

import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class CompilationDto {
    private long id;
    private  boolean pinned;
    private String title;
    private Set<EventShortDto> events;
}
