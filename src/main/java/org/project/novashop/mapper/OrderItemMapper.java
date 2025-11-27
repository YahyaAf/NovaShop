package org.project.novashop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.project.novashop.dto.commandes.OrderItemResponseDto;
import org.project.novashop.model.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.nom", target = "productNom")
    OrderItemResponseDto toResponseDto(OrderItem orderItem);
}