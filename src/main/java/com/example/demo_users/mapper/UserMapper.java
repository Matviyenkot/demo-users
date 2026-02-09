package com.example.demo_users.mapper;

import com.example.demo_users.dto.UserDto;
import com.example.demo_users.generated.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "id", source = "id")
  User toUser(UserDto userDto);
}
