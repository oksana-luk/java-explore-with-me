package ru.practicum.ewm.api.adminapi.events;

import ru.practicum.ewm.api.adminapi.events.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.api.privateapi.events.dto.EventFullDto;
import ru.practicum.ewm.api.privateapi.events.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventAdminService {
    List<EventFullDto> getEvents(List<Long> users, List<Event.State> states, List<Long> categories,
                                 LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    EventFullDto updateEvent(UpdateEventAdminRequest updateEventAdminRequest, long eventId);
}
