package ru.practicum.ewm.stats;

import org.mapstruct.Mapper;
import ru.practicum.ewm.HitDto;

@Mapper(componentModel = "spring")
public interface HitMapper {

    Hit mapHitDtoToHit(HitDto hitDto);
}
