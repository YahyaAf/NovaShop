package org.project.novashop.mapper;

import org.mapstruct.*;
import org.project.novashop.dto.clients.ClientRequestDto;
import org.project.novashop.dto.clients.ClientResponseDto;
import org.project.novashop.model.Client;

@Mapper(componentModel = "spring", uses = {UserMapper. class})
public interface ClientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "niveauFidelite",
            expression = "java(dto.getNiveauFidelite() != null ? dto.getNiveauFidelite() : org.project.novashop.enums. CustomerTier.BASIC)")
    Client toEntity(ClientRequestDto dto);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    ClientResponseDto toResponseDto(Client client);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntityFromDto(ClientRequestDto dto, @MappingTarget Client client);
}