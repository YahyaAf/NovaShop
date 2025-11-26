package org.project.novashop.mapper;

import org.mapstruct.*;
import org.project.novashop.dto.users.UserRequestDto;
import org.project.novashop.dto.users.UserResponseDto;
import org.project.novashop.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "active", expression = "java(dto.getActive() != null ? dto.getActive() : true)")
    User toEntity(UserRequestDto dto);

    UserResponseDto toResponseDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UserRequestDto dto, @MappingTarget User user);
}