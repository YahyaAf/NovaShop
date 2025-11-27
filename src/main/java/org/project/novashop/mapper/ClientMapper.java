package org.project. novashop.mapper;

import org.mapstruct.*;
import org.project.novashop.dto.clients.ClientRequestDto;
import org.project.novashop.dto.clients.ClientResponseDto;
import org.project. novashop.dto.clients.ClientStatsDto;
import org. project.novashop.model.Client;

@Mapper(componentModel = "spring", uses = {UserMapper. class})
public interface ClientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "niveauFidelite",
            expression = "java(dto. getNiveauFidelite() != null ? dto.getNiveauFidelite() : org.project. novashop.enums.CustomerTier.BASIC)")
    @Mapping(target = "totalOrders", constant = "0")
    @Mapping(target = "totalSpent", constant = "0.0")
    @Mapping(target = "commandes", ignore = true)
    Client toEntity(ClientRequestDto dto);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    ClientResponseDto toResponseDto(Client client);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "niveauFidelite", ignore = true)
    @Mapping(target = "totalOrders", ignore = true)
    @Mapping(target = "totalSpent", ignore = true)
    @Mapping(target = "commandes", ignore = true)
    void updateEntityFromDto(ClientRequestDto dto, @MappingTarget Client client);

    @Mapping(source = "id", target = "clientId")
    @Mapping(source = "user.username", target = "nom")
    @Mapping(target = "prochainNiveau", ignore = true)
    @Mapping(target = "commandesRestantes", ignore = true)
    @Mapping(target = "montantRestant", ignore = true)
    ClientStatsDto toStatsDto(Client client);
}