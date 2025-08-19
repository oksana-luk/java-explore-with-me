package ru.practicum.ewm.api.privateapi.events;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.ewm.api.privateapi.events.model.Event;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    Page<Event> findAllByInitiatorId(long initiatorId, Pageable page);

    Optional<Event> findByIdAndState(long eventId, Event.State state);

    boolean existsByCategoryId(long categoryId);
}
