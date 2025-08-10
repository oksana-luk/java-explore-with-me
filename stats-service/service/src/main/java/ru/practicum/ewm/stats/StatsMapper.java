package ru.practicum.ewm.stats;

import org.mapstruct.Mapper;
import ru.practicum.ewm.StatsDto;

@Mapper(componentModel = "spring")
public interface StatsMapper {

    StatsDto mapStatsToStatsDto(Stats stats);
}
