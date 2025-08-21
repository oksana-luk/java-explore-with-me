package ru.practicum.ewm.api.adminapi.moderations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.api.adminapi.moderations.dto.ModerationDto;
import ru.practicum.ewm.api.adminapi.moderations.model.Moderation;
import ru.practicum.ewm.api.adminapi.user.model.User;
import ru.practicum.ewm.api.privateapi.events.model.Event;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ModerationServiceImpl implements ModerationService {
    private final ModerationRepository moderationRepository;

    @Override
    @Transactional
    public ModerationDto addModeration(Event event, User admin, Event.State state, String comment) {
        log.info("Moderation service, adding moderation: event={}, admin={}, state={}, comment={}", event, admin, state, comment);
        Moderation moderation = new Moderation(0L, event, null, state, LocalDateTime.now(), comment);
        moderation = moderationRepository.save(moderation);

        ModerationDto moderationDto = ModerationMapper.mapModerationToModerationDto(moderation);
        log.info("Moderation service, adding moderation: moderationDto={}", moderationDto);
        return moderationDto;
    }

    @Override
    public List<ModerationDto> getLatestModeration(List<Event> events) {
        return moderationRepository.findLatestModerationsByEventIds(events).stream()
                .map(ModerationMapper::mapModerationToModerationDto)
                .toList();
    }

    @Override
    public List<ModerationDto> getModerationHistory(long eventId) {
        log.info("Moderation service, getting moderation history: eventId={}", eventId);
        List<ModerationDto> moderationDtos = moderationRepository.findAllByEventId(eventId).stream()
                .map(ModerationMapper::mapModerationToModerationDto)
                .toList();
        log.info("Moderation service, getting moderation history: moderationDtos={}", moderationDtos);
        return moderationDtos;
    }

    @Override
    public Map<Long, String> getLatestModerationsComments(List<Event> events) {
        Map<Long, String> moderationComments = new HashMap<>();
        moderationRepository.findLatestModerationsByEventIds(events).forEach(moderation -> {
            if (Objects.nonNull(moderation.getComment())) {
                moderationComments.put(moderation.getEvent().getId(), moderation.getComment());
            }
        });
        return moderationComments;
    }
}
