package org.project. novashop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org. project.novashop.dto. commandes.CommandeResponseDto;
import org.project. novashop.model.Commande;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface CommandeMapper {

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.user.username", target = "clientNom")
    @Mapping(source = "client.niveauFidelite", target = "clientNiveauFidelite")
    @Mapping(source = "orderItems", target = "items")
    @Mapping(target = "montantRemiseFidelite", ignore = true)
    @Mapping(target = "montantRemisePromo", ignore = true)
    @Mapping(target = "montantRemiseTotal", expression = "java(commande.getMontantRemise())")
    @Mapping(target = "montantHtApresRemise", expression = "java(commande.getSousTotalHt() - commande.getMontantRemise())")
    CommandeResponseDto toResponseDto(Commande commande);

    List<CommandeResponseDto> toResponseDtoList(List<Commande> commandes);
}