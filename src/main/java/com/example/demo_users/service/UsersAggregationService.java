package com.example.demo_users.service;

import com.example.demo_users.generated.model.User;
import com.example.demo_users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersAggregationService {
  private final UserRepository repo;

  public List<User> getAll() {
    var dtos = repo.findAllFromAllSources();
    return dtos.stream()
      .map(d -> new User()
        .id(d.id())
        .username(d.username())
        .name(d.name())
        .surname(d.surname()))
      .toList();
  }
}
