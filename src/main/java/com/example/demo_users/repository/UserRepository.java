package com.example.demo_users.repository;

import com.example.demo_users.dto.UserDto;

import java.util.List;

public interface UserRepository {
  List<UserDto> findAllFromAllSources();
}

