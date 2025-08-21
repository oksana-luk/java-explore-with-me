package ru.practicum.ewm.api.adminapi.moderations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.api.adminapi.moderations.model.Moderation;
import ru.practicum.ewm.api.privateapi.events.model.Event;

import java.util.List;

public interface ModerationRepository extends JpaRepository<Moderation, Long> {
    List<Moderation> findAllByEventId(long eventId);

    @Query("select m " +
            "from Moderation m " +
            "where m.event in :events " +
            "and m.moderateOn = (select max(m2.moderateOn) " +
                                "from Moderation m2 " +
                                "where m2.event = m.event)")
    List<Moderation> findLatestModerationsByEventIds(List<Event> events);
}
