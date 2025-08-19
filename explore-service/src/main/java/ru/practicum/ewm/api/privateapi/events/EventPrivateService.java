package ru.practicum.ewm.api.privateapi.events;

import ru.practicum.ewm.api.privateapi.events.dto.*;
import ru.practicum.ewm.api.privateapi.requests.dto.ParticipantRequestDto;

import java.util.List;

public interface EventPrivateService {
    EventFullDto addEvent(NewEventRequest newEventRequest, long userId);

    List<EventShortDto> getEvents(long userId, int from, int size);

    EventFullDto getEvent(long userId, long eventId);

    EventFullDto updateEvent(UpdateEventUserRequest updateRequest, long userId, long eventId);

    List<ParticipantRequestDto> getEventRequests(long userId, long eventId);

    EventRequestStatusUpdateResult updateRequestsByEvent(EventRequestStatusUpdateRequest updateRequest, long userId, long eventId);
}
