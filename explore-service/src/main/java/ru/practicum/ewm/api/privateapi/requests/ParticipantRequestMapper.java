package ru.practicum.ewm.api.privateapi.requests;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.api.privateapi.requests.dto.ParticipantRequestDto;
import ru.practicum.ewm.api.privateapi.requests.model.ParticipantRequest;

@Mapper(componentModel = "spring")
public interface ParticipantRequestMapper {
    @Mapping(target = "requester", source = "requester.id")
    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "created", source = "created", dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    ParticipantRequestDto mapParticipantRequestToParticipantRequestDto(ParticipantRequest request);
}
