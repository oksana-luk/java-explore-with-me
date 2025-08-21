package ru.practicum.ewm.api.privateapi.events;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.api.adminapi.categories.CategoryMapper;
import ru.practicum.ewm.api.adminapi.categories.model.Category;
import ru.practicum.ewm.api.adminapi.events.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.api.adminapi.user.UserMapper;
import ru.practicum.ewm.api.adminapi.user.model.User;
import ru.practicum.ewm.api.privateapi.events.dto.EventFullDto;
import ru.practicum.ewm.api.privateapi.events.dto.EventShortDto;
import ru.practicum.ewm.api.privateapi.events.dto.NewEventRequest;
import ru.practicum.ewm.api.privateapi.events.dto.UpdateEventUserRequest;
import ru.practicum.ewm.api.privateapi.events.model.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;

    public Event mapNewEventRequestToEvent(NewEventRequest newEventRequest, User initiator, Category category) {
        return new Event(
                0L,
                initiator,
                category,
                newEventRequest.getEventDate(),
                newEventRequest.getTitle(),
                newEventRequest.getDescription(),
                newEventRequest.getAnnotation(),
                newEventRequest.isRequestModeration(),
                newEventRequest.getParticipantLimit(),
                newEventRequest.getLocation(),
                LocalDateTime.now(),
                null,
                newEventRequest.isPaid(),
                Event.State.PENDING);
    }

    public EventFullDto mapEventToEventFullDto(Event event, int confirmedRequests, long views) {
        return mapEventToEventFullDto(event, confirmedRequests, views, null);
    }

    public EventFullDto mapEventToEventFullDto(Event event, int confirmedRequests, long views, String moderationComment) {
        return new EventFullDto(
                event.getId(),
                userMapper.mapUserToUserShortDto(event.getInitiator()),
                categoryMapper.mapCategoryToCategoryDto(event.getCategory()),
                DATE_TIME_FORMATTER.format(event.getEventDate()),
                event.getTitle(),
                event.getDescription(),
                event.getAnnotation(),
                event.isRequestModeration(),
                event.getParticipantLimit(),
                event.getLocation(),
                DATE_TIME_FORMATTER.format(event.getCreatedOn()),
                (event.getPublishedOn() == null) ? null : DATE_TIME_FORMATTER.format(event.getPublishedOn()),
                event.isPaid(),
                event.getState(),
                confirmedRequests,
                views,
                moderationComment);
    }

    public EventShortDto mapEventToEventShortDto(Event event, int confirmedRequests, long views) {
        return new EventShortDto(
                event.getId(),
                userMapper.mapUserToUserShortDto(event.getInitiator()),
                categoryMapper.mapCategoryToCategoryDto(event.getCategory()),
                DATE_TIME_FORMATTER.format(event.getEventDate()),
                event.getTitle(),
                event.getAnnotation(),
                event.isPaid(),
                confirmedRequests,
                views);
    }

    public void updateEventFields(Event event, UpdateEventUserRequest updateEventUserRequest, Category category) {
        if (updateEventUserRequest.hasCategory()) {
            event.setCategory(category);
        }
        if (updateEventUserRequest.hasEventDate()) {
            event.setEventDate(updateEventUserRequest.getEventDate());
        }
        if (updateEventUserRequest.hasTitle()) {
            event.setTitle(updateEventUserRequest.getTitle());
        }
        if (updateEventUserRequest.hasDescription()) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.hasAnnotation()) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.hasRequestModeration()) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.hasParticipantLimit()) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.hasLocation()) {
            event.setLocation(updateEventUserRequest.getLocation());
        }
        if (updateEventUserRequest.hasPaid()) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
    }

    public void updateEventFields(Event event, UpdateEventAdminRequest updateEventUserRequest, Category category) {
        if (updateEventUserRequest.hasCategory()) {
            event.setCategory(category);
        }
        if (updateEventUserRequest.hasEventDate()) {
            event.setEventDate(updateEventUserRequest.getEventDate());
        }
        if (updateEventUserRequest.hasTitle()) {
            event.setTitle(updateEventUserRequest.getTitle());
        }
        if (updateEventUserRequest.hasDescription()) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.hasAnnotation()) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.hasRequestModeration()) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.hasParticipantLimit()) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.hasLocation()) {
            event.setLocation(updateEventUserRequest.getLocation());
        }
        if (updateEventUserRequest.hasPaid()) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
    }
}
