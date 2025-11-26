package org.project.novashop.mapper;

import org.mapstruct.*;
import org.project.novashop.dto.promos.PromoRequestDto;
import org.project.novashop.dto.promos.PromoResponseDto;
import org.project.novashop.model.Promo;

@Mapper(componentModel = "spring")
public interface PromoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usageCount", constant = "0")
    Promo toEntity(PromoRequestDto dto);

    PromoResponseDto toResponseDto(Promo promo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usageCount", ignore = true)
    void updateEntityFromDto(PromoRequestDto dto, @MappingTarget Promo promo);
}