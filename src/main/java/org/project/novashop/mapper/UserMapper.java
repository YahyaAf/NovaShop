package org.project.novashop.mapper;

import org.project.novashop.dto.users.UserRequestDto;
import org.project.novashop.dto.users.UserResponseDto;
import org.project.novashop.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequestDto dto, String encodedPassword) {
        if (dto == null) {
            return null;
        }

        return User.builder()
                .username(dto.getUsername())
                .password(encodedPassword)
                .role(dto.getRole())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
    }

    public UserResponseDto toResponseDto(User user) {
        if (user == null) {
            return null;
        }

        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .active(user.getActive())
                .build();
    }

    public void updateEntityFromDto(UserRequestDto dto, User user, String encodedPassword) {
        if (dto == null || user == null) {
            return;
        }

        if (dto.getUsername() != null) {
            user.setUsername(dto.getUsername());
        }
        if (encodedPassword != null) {
            user.setPassword(encodedPassword);
        }
        if (dto.getRole() != null) {
            user.setRole(dto.getRole());
        }
        if (dto.getActive() != null) {
            user.setActive(dto.getActive());
        }
    }
}