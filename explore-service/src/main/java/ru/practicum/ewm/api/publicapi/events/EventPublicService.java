package ru.practicum.ewm.api.publicapi.events;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.api.privateapi.events.dto.EventFullDto;
import ru.practicum.ewm.api.privateapi.events.dto.EventShortDto;

import java.util.List;

public interface EventPublicService {
    List<EventShortDto> getEvents(EventFilterRequest filterRequest, int from, int size, HttpServletRequest request);

    EventFullDto getEvent(long eventId, HttpServletRequest request);
}
