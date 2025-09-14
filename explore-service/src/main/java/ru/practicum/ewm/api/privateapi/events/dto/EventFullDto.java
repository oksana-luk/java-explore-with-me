package ru.practicum.ewm.api.privateapi.events.dto;

import lombok.*;
import ru.practicum.ewm.api.adminapi.categories.dto.CategoryDto;
import ru.practicum.ewm.api.adminapi.user.dto.UserShortDto;
import ru.practicum.ewm.api.privateapi.events.model.Event;
import ru.practicum.ewm.api.privateapi.events.model.Location;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class EventFullDto {
    private long id;
    private UserShortDto initiator;
    private CategoryDto category;
    private String eventDate;
    private String title;
    private String description;
    private String annotation;
    private boolean requestModeration;
    private int participantLimit;
    private Location location;
    private String createdOn;
    private String publishedOn;
    private boolean paid;
    private Event.State state;
    private int confirmedRequests;
    private long views;
    private String moderationComment;
}
