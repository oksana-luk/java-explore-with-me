package ru.practicum.ewm.api.privateapi.events.dto;

import lombok.*;
import ru.practicum.ewm.api.adminapi.categories.dto.CategoryDto;
import ru.practicum.ewm.api.adminapi.user.dto.UserShortDto;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class EventShortDto {
    private long id;
    private UserShortDto initiator;
    private CategoryDto category;
    private String eventDate;
    private String title;
    private String annotation;
    private boolean paid;
    private int confirmedRequests;
    private long views;
}
