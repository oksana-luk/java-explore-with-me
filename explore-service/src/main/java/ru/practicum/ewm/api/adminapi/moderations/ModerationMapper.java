package ru.practicum.ewm.api.adminapi.moderations;

import ru.practicum.ewm.api.adminapi.moderations.dto.ModerationDto;
import ru.practicum.ewm.api.adminapi.moderations.model.Moderation;

import java.time.format.DateTimeFormatter;

public class ModerationMapper {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ModerationDto mapModerationToModerationDto(Moderation moderation) {
        return new ModerationDto(
                moderation.getId(),
                moderation.getEvent().getId(),
                "System admin",
                moderation.getState(),
                DATE_TIME_FORMATTER.format(moderation.getModerateOn()),
                moderation.getComment()
        );
    }
}
