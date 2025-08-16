package ru.practicum.ewm.api.privateapi.requests;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.api.privateapi.events.model.Event;
import ru.practicum.ewm.api.privateapi.requests.dto.ParticipantRequestCount;
import ru.practicum.ewm.api.privateapi.requests.model.ParticipantRequest;

import java.util.List;
import java.util.Set;

public interface ParticipantRequestRepository extends JpaRepository<ParticipantRequest, Long> {
    List<ParticipantRequest> findAllByRequesterId(long requesterId);

    @Query("select event.id as eventId, " +
            "count(parReq.id) as countOfRequests " +
            "from ParticipantRequest parReq " +
            "where parReq.event in :events " +
            "and parReq.status = :status " +
            "group by event.id")
    List<ParticipantRequestCount> findRequestCountsByEventsAndStatus(List<Event> events, ParticipantRequest.Status status);

    List<ParticipantRequest> findAllByEventIn(List<Event> events);

    List<ParticipantRequest> findAllByIdInAndStatus(Set<Long> ids, ParticipantRequest.Status status);

    List<ParticipantRequest> findAllByIdIn(Set<Long> ids);

    List<ParticipantRequest> findAllByEventInAndStatus(List<Event> events, ParticipantRequest.Status status);

}