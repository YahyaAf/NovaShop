package org.project.novashop.mapper;

import org.mapstruct.*;
import org.project.novashop.dto.products.ProductRequestDto;
import org.project.novashop.dto.products.ProductResponseDto;
import org.project.novashop.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", constant = "false")
    Product toEntity(ProductRequestDto dto);

    ProductResponseDto toResponseDto(Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntityFromDto(ProductRequestDto dto, @MappingTarget Product product);
}