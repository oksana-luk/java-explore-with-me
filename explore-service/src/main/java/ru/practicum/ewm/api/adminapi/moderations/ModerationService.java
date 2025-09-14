package ru.practicum.ewm.api.adminapi.moderations;

import ru.practicum.ewm.api.adminapi.moderations.dto.ModerationDto;
import ru.practicum.ewm.api.adminapi.user.model.User;
import ru.practicum.ewm.api.privateapi.events.model.Event;

import java.util.List;
import java.util.Map;

public interface ModerationService {
    ModerationDto addModeration(Event event, User admin, Event.State state, String comment);

    List<ModerationDto> getLatestModeration(List<Event> events);

    List<ModerationDto> getModerationHistory(long eventId);

    Map<Long, String> getLatestModerationsComments(List<Event> events);
}
