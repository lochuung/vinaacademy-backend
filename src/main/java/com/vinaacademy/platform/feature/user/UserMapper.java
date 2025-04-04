package com.vinaacademy.platform.feature.user;

import com.vinaacademy.platform.feature.user.auth.dto.RegisterRequest;
import com.vinaacademy.platform.feature.user.dto.UserDto;
import com.vinaacademy.platform.feature.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toUser(RegisterRequest registerRequest);

    UserDto toDto(User user);
}
