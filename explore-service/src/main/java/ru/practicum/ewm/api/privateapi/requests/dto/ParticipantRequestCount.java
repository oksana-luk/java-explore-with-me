package ru.practicum.ewm.api.privateapi.requests.dto;

public interface ParticipantRequestCount {
    long getEventId();

    int getCountOfRequests();
}
