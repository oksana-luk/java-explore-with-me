package ru.practicum.ewm.api.privateapi.requests;

import ru.practicum.ewm.api.privateapi.events.model.Event;
import ru.practicum.ewm.api.privateapi.requests.dto.ParticipantRequestDto;

import java.util.List;
import java.util.Map;

public interface ParticipantRequestService {
    ParticipantRequestDto addRequest(long userId, long eventId);

    List<ParticipantRequestDto> getRequests(long userId);

    ParticipantRequestDto cancelRequest(long userId, long requestId);

    Map<Long, Integer> getCountOfConfirmedRequestsByEvents(List<Event> events);
}
